package org.ogf.saga.proxies.job;

import java.util.List;

import org.ogf.saga.ObjectType;
import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobSelf;
import org.ogf.saga.job.JobService;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.job.JobServiceSpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class JobServiceWrapper extends SagaObjectBase implements JobService {
    
    private JobServiceSpiInterface proxy;

    public JobServiceWrapper(Session session, String rm) throws NoSuccess,
                Timeout, PermissionDenied, AuthorizationFailed, AuthenticationFailed,
                IncorrectURL, NotImplemented {
        super(session);
        Object[] parameters = { this, session, rm };
        try {
            proxy = (JobServiceSpiInterface) SAGAEngine.createAdaptorProxy(
                    JobServiceSpiInterface.class,
                    new Class[] { JobServiceWrapper.class, Session.class, String.class },
                    parameters);
        } catch(org.ogf.saga.error.Exception e) {
            if (e instanceof NotImplemented) {
                throw (NotImplemented) e;
            }
            if (e instanceof IncorrectURL) {
                throw (IncorrectURL) e;
            }
            if (e instanceof AuthenticationFailed) {
                throw (AuthenticationFailed) e;
            }
            if (e instanceof AuthorizationFailed) {
                throw (AuthorizationFailed) e;
            }
            if (e instanceof PermissionDenied) {
                throw (PermissionDenied) e;
            }
            if (e instanceof Timeout) {
                throw (Timeout) e;
            }
            if (e instanceof NoSuccess) {
                throw (NoSuccess) e;
            }
            throw new NoSuccess("Constructor failed", e);
        } 
    }

    public Job createJob(JobDescription jd) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, Timeout, NoSuccess {
        return proxy.createJob(jd);
    }

    public Task<Job> createJob(TaskMode mode, JobDescription jd)
            throws NotImplemented {
        return proxy.createJob(mode, jd);
    }

    public Job getJob(String jobId) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, Timeout, NoSuccess {
        return proxy.getJob(jobId);
    }

    public Task<Job> getJob(TaskMode mode, String jobId) throws NotImplemented {
        return proxy.getJob(mode, jobId);
    }

    public JobSelf getSelf() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getSelf();
    }

    public Task<JobSelf> getSelf(TaskMode mode) throws NotImplemented {
        return proxy.getSelf(mode);
    }

    public List<String> list() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.list();
    }

    public Task<List<String>> list(TaskMode mode) throws NotImplemented {
        return proxy.list(mode);
    }

    public ObjectType getType() {
        return ObjectType.JOBSERVICE;
    }
    
    public Object clone() throws CloneNotSupportedException {
        JobServiceWrapper clone = (JobServiceWrapper) super.clone();
        clone.proxy = (JobServiceSpiInterface) SAGAEngine.createAdaptorCopy(
                    JobServiceSpiInterface.class, proxy, clone);
        return clone;
    }
    
    

}
