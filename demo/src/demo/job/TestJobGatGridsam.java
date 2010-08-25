package demo.job;

import org.ogf.saga.context.Context;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class TestJobGatGridsam implements Callback {

    public static void main(String[] args) {

        // Make sure that the SAGA engine picks the javagat adaptor for
        // JobService.
        System.setProperty("JobService.adaptor.name", "javaGAT");
        
        String server = "gridsam://titan.cs.vu.nl:18443/gridsam/services/gridsam";

        if (args.length > 1) {
            System.err.println("Usage: java demo.job.TestJobGatGridsam [<serverURL>]");
            System.exit(1);
        } else if (args.length == 1) {
            server = args[0];
        }
        
        try {           
            URL serverURL = URLFactory.createURL(server);
                        
            // Create the JobService.
            JobService js = JobFactory.createJobService(serverURL);

            // Create a job description to execute "/bin/uname -a" on
            // the server host.
            // The output will be staged out to the current directory.
            JobDescription jd = JobFactory.createJobDescription();
            String serverHost = serverURL.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/uname");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "-a" });
            jd.setAttribute(JobDescription.OUTPUT, "uname.out");

            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { "uname.out < uname.out" });
            
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
