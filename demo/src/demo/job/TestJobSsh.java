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

public class TestJobSsh implements Callback {

   public static void main(String[] args) {
       // Make sure that the SAGA engine picks the javagat adaptor for JobService.
       System.setProperty("JobService.adaptor.name", "javaGAT");
     
       String serverNode = "fs0.das3.cs.vu.nl";

       if (args.length > 1) {
           System.err.println("Usage: java demo.job.TestJobSsh [<serverNode>]");
           System.exit(1);
       } else if (args.length == 1) {
           serverNode = args[0];
       }
       
       System.out.println("try to submit ssh job to: " + serverNode);
       
       try {
           // Create the JobService.
           JobService js = JobFactory.createJobService(URLFactory.createURL("commandlinessh://" + serverNode));

           // Create a job: /bin/hostname executed on 1 node.
           JobDescription jd = JobFactory.createJobDescription();
           jd.setAttribute(JobDescription.EXECUTABLE, "/bin/hostname");
           jd.setAttribute(JobDescription.NUMBEROFPROCESSES, "1"); //10
           jd.setAttribute(JobDescription.OUTPUT, "hostname.out");
           jd.setAttribute(JobDescription.ERROR, "hostname.err");
           
           jd.setVectorAttribute(JobDescription.FILETRANSFER,
                   new String[] { "hostname.out < hostname.out", "hostname.err < hostname.err"});
           
           // Create the job, run it, and wait for it.
           Job job = js.createJob(jd);
           job.addCallback(Job.JOB_STATE, new TestJobSsh());
           job.addCallback(Job.JOB_STATEDETAIL, new TestJobSsh());
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
