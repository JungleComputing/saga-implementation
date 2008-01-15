package test.job;

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

public class TestJob implements Callback {

    public static void main(String[] args) {
        try {
            Session session = SessionFactory.createSession(true);
            JobDescription jd = JobFactory.createJobDescription();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS, new String[] {"fs0.das3.cs.vu.nl"});
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/uname");
            jd.setVectorAttribute(JobDescription.ARGUMENTS, new String[] { "-a" });
            jd.setAttribute(JobDescription.OUTPUT, "uname.out");
            JobService js = JobFactory.createJobService(session);
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
