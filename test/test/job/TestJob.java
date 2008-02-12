package test.job;

import org.ogf.saga.URL;
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

public class TestJob implements Callback {

    public static void main(String[] args) {
        try {
            Session session = SessionFactory.createSession(true);
            
            // Create a preferences context for JavaGAT.
            // The "preferences" context is special: it is extensible.
            Context context = ContextFactory.createContext("preferences");
            // Make sure that javaGAT picks the gridsam adaptor.
            context.setAttribute("ResourceBroker.adaptor.name", "gridsam");
            // Set a sandbox host for javaGAT, otherwise javagat thinks it is
            // just localhost. This is only needed because the gridsam server
            // is reached through a tunnel.
            context.setAttribute("ResourceBroker.sandbox.host", "ceriel@localhost:4567");
            // Sandbox root must be an absolute path for the javaGAT gridsam
            // adaptor.
            context.setAttribute("ResourceBroker.sandbox.root", "/tmp");
            
            session.addContext(context);
                        
            // Make sure that the SAGA engine picks the javagat adaptor for
            // JobService.
            System.setProperty("JobService.adaptor.name", "javaGAT");

            // Create the JobService. Note: the gridsam service is behind a
            // firewall and is reached through an ssh tunnel, which the user
            // must have set up beforehand.
            JobService js = JobFactory.createJobService(new URL(
                    "https://localhost:18443/gridsam/services/gridsam"));

            // Create a job description to execute "/bin/uname -a" on
            // fs0.das3.cs.vu.nl.
            // The output will be staged out to the current directory.
            JobDescription jd = JobFactory.createJobDescription();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { "fs0.das3.cs.vu.nl" });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/uname");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "-a" });
            jd.setAttribute(JobDescription.OUTPUT, "uname.out");
            
            // Get hostname and current directory for poststage target.
            String host = java.net.InetAddress.getLocalHost()
                    .getCanonicalHostName();
            String dir = System.getProperty("user.dir");
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { "file://" + host + dir
                            + "/uname.out < uname.out" });

            // Create the job, run it, and wait for it.
            Job job = js.createJob(jd);
            job.addCallback(Job.JOB_STATE, new TestJob());
            job.addCallback(Job.JOB_STATEDETAIL, new TestJob());
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
