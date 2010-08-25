package demo.job;

import org.ogf.saga.context.Context;
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

public class TestJobGT2 implements Callback {

   public static void main(String[] args) {
       // Make sure that the SAGA engine picks the javagat adaptor for JobService.
       System.setProperty("JobService.adaptor.name", "javaGAT");
     
       String server = "globus://ngs.rl.ac.uk";

       if (args.length > 1) {
           System.err.println("Usage: java demo.job.TestJob [<serverURL>]");
           System.exit(1);
       } else if (args.length == 1) {
           server = args[0];
       }
       
       System.out.println("try to submit gt2 job to: "+server);
       
       try {
           URL serverURL = URLFactory.createURL(server);
 
           // Create the JobService.
           JobService js = JobFactory.createJobService(serverURL);
           // JobService js = JobFactory.createJobService(session);
           //JobService js = JobFactory.createJobService();

           // Create a job: /bin/hostname executed on 1 nodes.
           JobDescription jd = JobFactory.createJobDescription();
           jd.setAttribute(JobDescription.EXECUTABLE, "/bin/hostname");
           jd.setVectorAttribute(JobDescription.ARGUMENTS, new String[0]);
           jd.setAttribute(JobDescription.NUMBEROFPROCESSES, "1"); //10
           jd.setAttribute(JobDescription.OUTPUT, "hostname.out");
           jd.setAttribute(JobDescription.ERROR, "hostname.err");
          
           jd.setVectorAttribute(JobDescription.FILETRANSFER,
                   new String[] { "hostname.out < hostname.out", "hostname.err < hostname.err"});

           // Create the job, run it, and wait for it.
           
           Job job = js.createJob(jd);
           /*
           job.addCallback(Job.JOB_STATE, new TestJobGT2());
           job.addCallback(Job.JOB_STATEDETAIL, new TestJobGT2());
           */
           job.run();
           job.waitFor();
           Session session = SessionFactory.createSession(true);
           session.close();
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
