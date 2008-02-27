package org.ogf.saga.proxies.job;

import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
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

    public JobServiceWrapper(Session session, URL rm) throws NoSuccessException,
                TimeoutException, PermissionDeniedException, AuthorizationFailedException, AuthenticationFailedException,
                IncorrectURLException, NotImplementedException {
        super(session);
        Object[] parameters = { this, session, rm };
        try {
            proxy = (JobServiceSpiInterface) SAGAEngine.createAdaptorProxy(
                    JobServiceSpiInterface.class,
                    new Class[] { JobServiceWrapper.class, 
                        org.ogf.saga.impl.session.Session.class, URL.class },
                    parameters);
        } catch(org.ogf.saga.error.SagaException e) {
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
            }
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            if (e instanceof AuthenticationFailedException) {
                throw (AuthenticationFailedException) e;
            }
            if (e instanceof AuthorizationFailedException) {
                throw (AuthorizationFailedException) e;
            }
            if (e instanceof PermissionDeniedException) {
                throw (PermissionDeniedException) e;
            }
            if (e instanceof TimeoutException) {
                throw (TimeoutException) e;
            }
            if (e instanceof NoSuccessException) {
                throw (NoSuccessException) e;
            }
            throw new NoSuccessException("Constructor failed", e);
        } 
    }

    public Job createJob(JobDescription jd) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return proxy.createJob(jd);
    }

    public Task<Job> createJob(TaskMode mode, JobDescription jd)
            throws NotImplementedException {
        return proxy.createJob(mode, jd);
    }

    public Job getJob(String jobId) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.getJob(jobId);
    }

    public Task<Job> getJob(TaskMode mode, String jobId) throws NotImplementedException {
        return proxy.getJob(mode, jobId);
    }

    public JobSelf getSelf() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getSelf();
    }

    public Task<JobSelf> getSelf(TaskMode mode) throws NotImplementedException {
        return proxy.getSelf(mode);
    }

    public List<String> list() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.list();
    }

    public Task<List<String>> list(TaskMode mode) throws NotImplementedException {
        return proxy.list(mode);
    }
    
    public Object clone() throws CloneNotSupportedException {
        JobServiceWrapper clone = (JobServiceWrapper) super.clone();
        clone.proxy = (JobServiceSpiInterface) SAGAEngine.createAdaptorCopy(
                    JobServiceSpiInterface.class, proxy, clone);
        return clone;
    }

    public Job runJob(String commandLine, String host, boolean interactive) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return proxy.runJob(commandLine, host, interactive);
    }

    public Job runJob(String commandLine, String host) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return runJob(commandLine, host, false);
    }

    public Job runJob(String commandLine, boolean interactive) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return runJob(commandLine, "", interactive);
    }

    public Job runJob(String commandLine) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        return runJob(commandLine, "", false);
    }

    public Task<Job> runJob(TaskMode mode, String commandLine, String host, boolean interactive)
            throws NotImplementedException {
        return proxy.runJob(mode, commandLine, host, interactive);
    }

    public Task<Job> runJob(TaskMode mode, String commandLine, String host) throws NotImplementedException {
        return runJob(mode, commandLine, host, false);
    }

    public Task<Job> runJob(TaskMode mode, String commandLine, boolean interactive) throws NotImplementedException {
        return runJob(mode, commandLine, "", interactive);
    }

    public Task<Job> runJob(TaskMode mode, String commandLine) throws NotImplementedException {
        return runJob(mode, commandLine, "", false);
    }
    
    

}
