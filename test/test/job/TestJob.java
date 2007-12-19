package test.job;

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
        JobDescription jd = JobFactory.createJobDescription();
        jd.setAttribute(JobDescription.EXECUTABLE, "/bin/date");
        jd.setAttribute(JobDescription.OUTPUT, "/home/ceriel/date.out");
        JobService js = JobFactory.createJobService();
        Job job = js.createJob(jd);
        job.run();
        job.waitFor();
        } catch(Throwable e) {
            System.out.println("Got exception " + e);
            e.printStackTrace();
            e.getCause().printStackTrace();
        }
    }
    
    public boolean cb(Monitorable m, Metric metric, Context ctxt) {
        try {
            String value = metric.getAttribute(Metric.VALUE);
            System.out.println("Callback called, value = " + value);
        } catch(Throwable e) {
            System.err.println("error" + e);
            e.printStackTrace(System.err);
        }
        return true;
    }

}
