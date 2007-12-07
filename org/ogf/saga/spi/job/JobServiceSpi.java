package org.ogf.saga.spi.job;

import java.util.List;

import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobSelf;
import org.ogf.saga.proxies.job.JobServiceWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class JobServiceSpi extends AdaptorBase implements JobServiceSpiInterface {

    protected Session session;
    protected JobServiceWrapper wrapper;
    protected String rm;
    
    public JobServiceSpi(JobServiceWrapper wrapper, Session session, String rm) {
        this.session = session;
        this.rm = rm;
        this.wrapper = wrapper;
    }

    public Task<Job> createJob(TaskMode mode, JobDescription jd)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Job>(wrapper, session, mode,
                "createJob", new Class[] { JobDescription.class }, jd);
    }

    public Task<Job> getJob(TaskMode mode, String jobId) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Job>(wrapper, session, mode,
                "getJob", new Class[] { String.class }, jobId);
    }

    public Task<JobSelf> getSelf(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<JobSelf>(wrapper, session, mode,
                "getSelf", new Class[] { });
    }

    public Task<List<String>> list(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<String>>(wrapper, session, mode,
                "list", new Class[] { } );
    }

}
