package demo.job;

import org.ogf.saga.context.Context;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.task.State;
import org.ogf.saga.url.URLFactory;

/**
 * Runs a specified job on a single processor, on the job service specified by the parameter.
 * The standard output is redirected to a file "job.out", the standard error is
 * redirected to "job.err".
 */
public class JobRunner implements Callback {

   public static void main(String[] args) throws Exception {
       
       String serverUrlString = null;
       String jobToRun = null;
       String[] jobArgs = null;
       
       if (args.length < 2) {
           System.err.println("args.length = " + args.length);
           System.err.println("Usage: java demo.job.JobRunner <serverUrl> <job> <job-args>*");
           System.exit(1);
       } else {
           serverUrlString = args[0];
           jobToRun = args[1];
           jobArgs = new String[args.length-2];
           System.arraycopy(args, 2, jobArgs, 0, args.length-2);
       }
       try {
           new JobRunner(serverUrlString, jobToRun, jobArgs).runJob();
        } catch(SagaException e) {
            System.err.println(e.toString());
        }
   }
   
   private final JobService js;

   private final JobDescription jd;
   
   public JobRunner(String srvr, String command, String[] args)
           throws Exception {
       // Create a job description
       jd = JobFactory.createJobDescription();
       jd.setAttribute(JobDescription.EXECUTABLE, command);
       jd.setAttribute(JobDescription.NUMBEROFPROCESSES, "1");
       jd.setVectorAttribute(JobDescription.ARGUMENTS, args);
       jd.setAttribute(JobDescription.OUTPUT, "job.out");
       jd.setAttribute(JobDescription.ERROR, "job.err");
       jd.setVectorAttribute(JobDescription.FILETRANSFER,
               new String[] { "job.out < job.out", "job.err < job.err"});
       // Create a job service
       js = JobFactory.createJobService(URLFactory.createURL(srvr));

       System.out.println("try to submit job " + command + " to: " + srvr);
   }

   public void runJob() throws Exception {
       // Create the job, run it, and wait for it.
       Job job = js.createJob(jd);
       job.addCallback(Job.JOB_STATE, this);
       job.addCallback(Job.JOB_STATEDETAIL, this);
       job.run();
       job.waitFor();
       if (job.getState() == State.FAILED) {
           throw new Error("Job failed");
       }
   }

   // Callback monitors job.
   public boolean cb(Monitorable m, Metric metric, Context ctxt) {
       try {
           String value = metric.getAttribute(Metric.VALUE);
           String name = metric.getAttribute(Metric.NAME);
           System.out.println("Callback: metric " + name + " = " + value);
       } catch (Throwable e) {
           System.err.println("error" + e);
           e.printStackTrace(System.err);
       }
       // Keep the callback.
       return true;
   }
}
