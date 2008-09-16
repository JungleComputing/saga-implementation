package demo.job;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class TestJobGatGridsam implements Callback {

    public static void main(String[] args) {

        // Make sure that the SAGA engine picks the javagat adaptor for
        // JobService.
        System.setProperty("JobService.adaptor.name", "javaGAT");
        
        String serverURL = "https://titan.cs.vu.nl:18443/gridsam/services/gridsam";

        if (args.length > 1) {
            System.err.println("Usage: java demo.job.TestJobGatGridsam [<serverURL>]");
            System.exit(1);
        } else if (args.length == 1) {
            serverURL = args[0];
        }
        
        try {
            Session session = SessionFactory.createSession(true);
            
            // Create a preferences context for JavaGAT.
            // The "preferences" context is special: it is extensible.
            Context context = ContextFactory.createContext("preferences");
            // Make sure that javaGAT picks the gridsam adaptor.
            context.setAttribute("ResourceBroker.adaptor.name", "gridsam");
            // Sandbox root must be an absolute path for the javaGAT gridsam
            // adaptor.
            context.setAttribute("ResourceBroker.sandbox.root", "/tmp");
            
            session.addContext(context);
            
            URL url = URLFactory.createURL(serverURL);
                        
            // Create the JobService.
            JobService js = JobFactory.createJobService(url);

            // Create a job description to execute "/bin/uname -a" on
            // the server host.
            // The output will be staged out to the current directory.
            JobDescription jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/uname");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "-a" });
            jd.setAttribute(JobDescription.OUTPUT, "uname.out");
            
            // Get hostname and current directory for poststage target.
            String host = java.net.InetAddress.getLocalHost()
                    .getCanonicalHostName();
            // Note: this does not work on windows, which gives a
            // directory string like c:\...\...., which does not give
            // a valid url.
            String dir = System.getProperty("user.dir");
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { "file://" + host + dir
                            + "/uname.out < uname.out" });

            // Create the job, run it, and wait for it.
            Job job = js.createJob(jd);
            job.addCallback(Job.JOB_STATE, new TestJobGatGridsam());
            job.addCallback(Job.JOB_STATEDETAIL, new TestJobGatGridsam());
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            System.out.println("Got exception " + e);
            e.printStackTrace();
            e.getCause().printStackTrace();
        }
    }

    // Callback monitors job.
    public boolean cb(Monitorable m, Metric metric, Context ctxt) {
        try {
            String value = metric.getAttribute(Metric.VALUE);
            String name = metric.getAttribute(Metric.NAME);
            System.out.println("Callback called for metric "
                    + name + ", value = " + value);
        } catch (Throwable e) {
            System.err.println("error" + e);
            e.printStackTrace(System.err);
        }
        // Keep the callback.
        return true;
    }
}
