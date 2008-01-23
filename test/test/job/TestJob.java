package test.job;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;

public class TestJob implements Callback {

    public static void main(String[] args) {
        try {
            // Make sure that the SAGA engine picks the javagat adaptor for
            // JobService.
            System.setProperty("JobService.adaptor.name", "javaGAT");

            // Make sure that javaGAT picks the gridsam adaptor.
            System.setProperty("GATPreferences.ResourceBroker.adaptor.name",
                    "gridsam");

            // Sandbox root must be an absolute path for the javaGAT gridsam
            // adaptor.
            System.setProperty("GAT.sandbox.root", "/tmp");

            // Set a sandbox host for javaGAT, otherwise javagat thinks it is
            // just localhost. This is only needed because the gridsam server
            // is reached through a tunnel.
            System.setProperty("GATPreferences.ResourceBroker.sandbox.host",
                    "localhost:4567");

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
            System.out.println("Callback called, value = " + value);
        } catch (Throwable e) {
            System.err.println("error" + e);
            e.printStackTrace(System.err);
        }
        // Keep the callback.
        return true;
    }
}
