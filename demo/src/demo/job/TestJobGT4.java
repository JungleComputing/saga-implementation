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

public class TestJobGT4 implements Callback {

    public static void main(String[] args) {

        // Make sure that the SAGA engine picks the javagat adaptor for
        // JobService.
        System.setProperty("JobService.adaptor.name", "javaGAT");

        String server = "https://fs0.das3.cs.vu.nl";

        if (args.length > 1) {
            System.err.println("Usage: java demo.job.TestJob [<serverURL>]");
            System.exit(1);
        } else if (args.length == 1) {
            server = args[0];
        }
        
        try {
            Session session = SessionFactory.createSession(true);
            
            URL serverURL = URLFactory.createURL(server);
            
            // Create a preferences context for JavaGAT.
            // The "preferences" context is special: it is extensible.
            Context context = ContextFactory.createContext("preferences");

            // Make sure that javaGAT picks the wsgt4 adaptor.
            context.setAttribute("ResourceBroker.adaptor.name", "wsgt4new");
            context.setAttribute("wsgt4new.factory.type", "SGE");
            // context.setAttribute("File.adaptor.name", "Local,GridFTP");
            
            session.addContext(context);
                        
            // Create the JobService.
            JobService js = JobFactory.createJobService(serverURL);

            // Create a job: /bin/hostname executed on 10 nodes.
            JobDescription jd = JobFactory.createJobDescription();
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/hostname");
            jd.setAttribute(JobDescription.NUMBEROFPROCESSES, "10");
            jd.setAttribute(JobDescription.OUTPUT, "hostname.out");
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { "hostname.out < hostname.out" });
            
            // Create the job, run it, and wait for it.
            Job job = js.createJob(jd);
            job.addCallback(Job.JOB_STATE, new TestJobGT4());
            job.addCallback(Job.JOB_STATEDETAIL, new TestJobGT4());
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
