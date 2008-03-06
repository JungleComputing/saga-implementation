package org.ogf.saga.impl.job;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.State;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/*
 * Jobs are not created by SAGA applications, so there is no SPI for it.
 * Rather, a JobService SPI can use this class as a base class to construct jobs
 * from.
 */
public abstract class Job extends org.ogf.saga.impl.task.Task<Void> implements
        org.ogf.saga.job.Job {

    private JobAttributes attributes;
    protected JobDescription jobDescription;
    protected Metric jobState;
    protected Metric jobStateDetail;
    protected Metric jobSignal;
    protected Metric jobCpuTime;
    protected Metric jobMemoryUse;
    protected Metric jobVMemoryUse;
    protected Metric jobPerformance;
    
    public Job(JobDescription jobDescription, Session session)
            throws NotImplementedException, BadParameterException {
        super(session);
        attributes = new JobAttributes(this, session);
        this.jobDescription = new JobDescription(jobDescription);

        jobState = new Metric(this, session,
                JOB_STATE,
                "fires on state changes of the job, and has the literal value of the job state enum",
                "ReadOnly", "1", "Enum", "New");
        jobStateDetail = new Metric(this, session,
                JOB_STATEDETAIL,
                "fires as a job changes its state detail",
                "ReadOnly", "1", "String", "");
        jobSignal = new Metric(this, session,
                JOB_SIGNAL,
                "fires as the job receives a signal, and has a value indicating the signal number",
                "ReadOnly", "1", "Int", "");
        jobCpuTime = new Metric(this, session,
                JOB_CPUTIME,
                "number of CPU seconds consumed by the job, aggregated",
                "ReadOnly", "1", "Int", "");
        jobMemoryUse = new Metric(this, session,
                JOB_MEMORYUSE,
                "current aggregate memory usage",
                "ReadOnly", "1", "Float", "0.0");
        jobVMemoryUse = new Metric(this, session,
                JOB_VMEMORYUSE,
                "current aggregate virtual memory usage",
                "ReadOnly", "1", "Float", "0.0");
        jobPerformance = new Metric(this, session,
                JOB_PERFORMANCE,
                "current performance",
                "ReadOnly", "1", "Float", "0.0"); 
        addMetric(JOB_STATE, jobState);
        addMetric(JOB_STATEDETAIL, jobStateDetail);
        addMetric(JOB_SIGNAL, jobSignal);
        addMetric(JOB_CPUTIME, jobCpuTime);
        addMetric(JOB_MEMORYUSE, jobMemoryUse);
        addMetric(JOB_VMEMORYUSE, jobVMemoryUse);
        addMetric(JOB_PERFORMANCE, jobPerformance);
    }
    
    protected Job(Job orig) {
        super(orig);
        
        attributes = new JobAttributes(orig.attributes);
        jobDescription = new JobDescription(orig.jobDescription);
        
        jobState = metrics.get(JOB_STATE);
        jobStateDetail = metrics.get(JOB_STATEDETAIL);
        jobSignal = metrics.get(JOB_SIGNAL);
        jobCpuTime = metrics.get(JOB_CPUTIME);
        jobMemoryUse = metrics.get(JOB_MEMORYUSE);       
        jobVMemoryUse = metrics.get(JOB_VMEMORYUSE);
        jobPerformance = metrics.get(JOB_PERFORMANCE);
    }
    
    protected synchronized void setState(State value) {
        setStateValue(value);
        notifyAll();
        try {
            if (value == State.DONE || value == State.FAILED
                    || value == State.CANCELED) {
                jobState.setMode("Final");
            }
            jobState.setValue(value.toString());
            jobState.internalFire();
        } catch(Throwable e) {
            throw new SagaRuntimeException("Internal error", e);
        }
    }
    
    // Methods from task that should be re-implemented
    public abstract  void cancel(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException;
    
    public abstract boolean cancel(boolean mayInterruptIfRunning);
    
    public abstract void run() throws NotImplementedException, IncorrectStateException, TimeoutException, NoSuccessException;
    
    public abstract boolean waitFor(float timeoutInSeconds)
            throws NotImplementedException, IncorrectStateException, TimeoutException, NoSuccessException;
    
    public abstract boolean isCancelled();
    
    public abstract boolean isDone();
    
    public abstract Object clone();
    
    // Methods from task that are impossible on jobs   
    public <T> T getObject() throws NotImplementedException, TimeoutException, NoSuccessException {
        throw new NoSuccessException("getObject() called on Job");
    }
    
    public Void getResult() throws NotImplementedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        throw new NoSuccessException("getResult() called on Job");
    }  
    
    public Void get() throws InterruptedException, ExecutionException {
        throw new SagaRuntimeException("get() called on Job");
    }
    
    public Void get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, java.util.concurrent.TimeoutException {
        throw new SagaRuntimeException("get() called on Job");
    }
    
    public void rethrow() throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        throw new NoSuccessException("rethrow() called on Job");
    }
    
    // Base implementations.

    public Task checkpoint(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "checkpoint", new Class[] { });
    }

    public org.ogf.saga.job.JobDescription getJobDescription() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (jobDescription == null) {
            throw new DoesNotExistException("No jobDescription available for this job");
        }
        return new JobDescription(jobDescription);
    }

    public Task<org.ogf.saga.job.JobDescription> getJobDescription(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.job.JobDescription>(
                this, session, mode, "getJobDescription", new Class[] { } );
    }

    public Task<InputStream> getStderr(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<InputStream>(this, session, mode,
                "getStderr", new Class[] { });
    }

    public Task<OutputStream> getStdin(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<OutputStream>(this, session, mode,
                "getStdin", new Class[] { });
    }

    public Task<InputStream> getStdout(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<InputStream>(this, session, mode,
                "getStdout", new Class[] { });
    }

    public Task migrate(TaskMode mode, org.ogf.saga.job.JobDescription jd)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "resume", new Class[] { org.ogf.saga.job.JobDescription.class }, jd);
    }

    public Task resume(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "resume", new Class[] { });
    }

    public Task signal(TaskMode mode, int signum) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "signal", new Class[] { Integer.TYPE }, signum);
    }

    public Task suspend(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "suspend", new Class[] { });
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
    throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsAllow", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "permissionsCheck", new Class[] { String.class,
                Integer.TYPE }, id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
    throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsDeny", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String>(this, session,
                mode, "getGroup", new Class[] {});
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String>(this, session,
                mode, "getOwner", new Class[] {});
    }

    public String[] findAttributes(String... patterns) throws NotImplementedException,
            BadParameterException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplementedException {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public Task<String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplementedException {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public Task setAttribute(TaskMode mode, String key, String value)
            throws NotImplementedException {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, BadParameterException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplementedException {
        return attributes.setVectorAttribute(mode, key, values);
    }

    protected void setValue(String key, String value) throws DoesNotExistException,
            NotImplementedException, IncorrectStateException, BadParameterException {
        attributes.setValue(key, value);
    }
    
    protected void setVectorValue(String key, String[] value) throws DoesNotExistException,
            NotImplementedException, IncorrectStateException, BadParameterException {
        attributes.setVectorValue(key, value);
    }
}
