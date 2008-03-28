package demo.job;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;

// This test is for the SAGA gridsam adaptor. The user must have
// started an ftp server on port 12345, on the submitting machine, beforehand.

public class TestJob1 implements Callback {

    public static void main(String[] args) {
        try {
            // Make sure the gridsam adaptor is selected.
            System.setProperty("JobService.adaptor.name", "gridsam");

            // Create the JobService. Gridsam service lives on titan.cs.vu.nl.
            JobService js = JobFactory.createJobService(new URL(
                    "https://titan.cs.vu.nl:18443/gridsam/services/gridsam"));

            // Create a job description to execute "/bin/uname -a" on
            // titan.cs.vu.nl.
            // The output will be staged out to the current directory.
            JobDescription jd = JobFactory.createJobDescription();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { "titan.cs.vu.nl" });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/uname");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "-a" });
            jd.setAttribute(JobDescription.OUTPUT, "uname.out");
            
            // Get hostname for poststage target.
            String host = java.net.InetAddress.getLocalHost()
                    .getCanonicalHostName();
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { "ftp://" + host
                            + ":12345/uname.out < uname.out" });

            // Create the job, run it, and wait for it.
            Job job = js.createJob(jd);
            job.addCallback(Job.JOB_STATE, new TestJob1());
            job.addCallback(Job.JOB_STATEDETAIL, new TestJob1());
            job.run();
            job.waitFor();
            System.out.println("Exit status = " + job.getAttribute(Job.EXITCODE));
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
