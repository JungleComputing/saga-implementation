package demo.job;

import org.ogf.saga.context.Context;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.url.URLFactory;

/**
 * Runs a specified job on a single processor, on the job service specified by the parameter.
 * The standard output is redirected to a file "job.out", the standard error is
 * redirected to "job.err".
 */
public class JobRunner implements Callback {

   public static void main(String[] args) {
       
       JobRunner me = new JobRunner();

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
       
       System.out.println("try to submit job " + jobToRun + " to: " + serverUrlString);
       
       try {
           // Create the JobService.
           JobService js = JobFactory.createJobService(URLFactory.createURL(serverUrlString));

           // Create the job description
           JobDescription jd = JobFactory.createJobDescription();
           jd.setAttribute(JobDescription.EXECUTABLE, jobToRun);
           jd.setVectorAttribute(JobDescription.ARGUMENTS, jobArgs);
           jd.setAttribute(JobDescription.NUMBEROFPROCESSES, "1"); //10
           jd.setAttribute(JobDescription.OUTPUT, "job.out");
           jd.setAttribute(JobDescription.ERROR, "job.err");           
           jd.setVectorAttribute(JobDescription.FILETRANSFER,
                   new String[] { "job.out < job.out", "job.err < job.err"});
           
           // Create the job, run it, and wait for it.
           Job job = js.createJob(jd);
           job.addCallback(Job.JOB_STATE, me);
           job.addCallback(Job.JOB_STATEDETAIL, me);
           job.run();
           job.waitFor();
       } catch (Throwable e) {
           System.out.println("Got exception " + e);
           e.printStackTrace();
           e = e.getCause();
           if (e != null) {
               e.printStackTrace();
           }
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
