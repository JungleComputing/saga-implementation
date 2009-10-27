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
import org.ogf.saga.impl.monitoring.MetricImpl;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.State;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/*
 * Jobs are not created by SAGA applications, so there is no SPI for it.
 * Rather, a JobService adaptor can use this class as a base class to construct jobs
 * from.
 */
public abstract class JobImpl extends
        org.ogf.saga.impl.task.TaskImpl<Void, Void> implements
        org.ogf.saga.job.Job {

    private JobAttributes attributes;
    protected JobDescriptionImpl jobDescriptionImpl;
    protected MetricImpl jobState;
    protected MetricImpl jobStateDetail;
    protected MetricImpl jobSignal;
    protected MetricImpl jobCpuTime;
    protected MetricImpl jobMemoryUse;
    protected MetricImpl jobVMemoryUse;
    protected MetricImpl jobPerformance;

    public JobImpl(JobDescriptionImpl jobDescriptionImpl, Session session)
            throws NotImplementedException, BadParameterException {
        super(session, null);
        attributes = new JobAttributes(this, session);
        this.jobDescriptionImpl = new JobDescriptionImpl(jobDescriptionImpl);

        jobState = new MetricImpl(
                this,
                session,
                JOB_STATE,
                "fires on state changes of the job, and has the literal value of the job state enum",
                "ReadOnly", "1", "Enum", "New");
        jobStateDetail = new MetricImpl(this, session, JOB_STATEDETAIL,
                "fires as a job changes its state detail", "ReadOnly", "1",
                "String", "");
        jobSignal = new MetricImpl(
                this,
                session,
                JOB_SIGNAL,
                "fires as the job receives a signal, and has a value indicating the signal number",
                "ReadOnly", "1", "Int", "");
        jobCpuTime = new MetricImpl(this, session, JOB_CPUTIME,
                "number of CPU seconds consumed by the job, aggregated",
                "ReadOnly", "1", "Int", "");
        jobMemoryUse = new MetricImpl(this, session, JOB_MEMORYUSE,
                "current aggregate memory usage", "ReadOnly", "1", "Float",
                "0.0");
        jobVMemoryUse = new MetricImpl(this, session, JOB_VMEMORYUSE,
                "current aggregate virtual memory usage", "ReadOnly", "1",
                "Float", "0.0");
        jobPerformance = new MetricImpl(this, session, JOB_PERFORMANCE,
                "current performance", "ReadOnly", "1", "Float", "0.0");
        addMetric(JOB_STATE, jobState);
        addMetric(JOB_STATEDETAIL, jobStateDetail);
        addMetric(JOB_SIGNAL, jobSignal);
        addMetric(JOB_CPUTIME, jobCpuTime);
        addMetric(JOB_MEMORYUSE, jobMemoryUse);
        addMetric(JOB_VMEMORYUSE, jobVMemoryUse);
        addMetric(JOB_PERFORMANCE, jobPerformance);
    }

    protected JobImpl(JobImpl orig) {
        super(orig);

        attributes = new JobAttributes(orig.attributes);
        jobDescriptionImpl = new JobDescriptionImpl(orig.jobDescriptionImpl);

        jobState = metricImpls.get(JOB_STATE);
        jobStateDetail = metricImpls.get(JOB_STATEDETAIL);
        jobSignal = metricImpls.get(JOB_SIGNAL);
        jobCpuTime = metricImpls.get(JOB_CPUTIME);
        jobMemoryUse = metricImpls.get(JOB_MEMORYUSE);
        jobVMemoryUse = metricImpls.get(JOB_VMEMORYUSE);
        jobPerformance = metricImpls.get(JOB_PERFORMANCE);
    }

    protected void setState(State value) {
        synchronized(this) {
            setStateValue(value);
            notifyAll();
        }
        try {
            if (value == State.DONE || value == State.FAILED
                    || value == State.CANCELED) {
                jobState.setMode("Final");
            }
            jobState.setValue(value.toString());
            jobState.internalFire();
        } catch (Throwable e) {
            throw new SagaRuntimeException("Internal error", e);
        }
    }

    // Methods from task that should be re-implemented
    public abstract void cancel(float timeoutInSeconds)
            throws NotImplementedException, IncorrectStateException,
            TimeoutException, NoSuccessException;

    public abstract boolean cancel(boolean mayInterruptIfRunning);

    public abstract void run() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException;

    public abstract boolean waitFor(float timeoutInSeconds)
            throws NotImplementedException, IncorrectStateException,
            TimeoutException, NoSuccessException;

    public abstract boolean isCancelled();

    public abstract boolean isDone();

    public abstract Object clone();

    // Methods from task that are impossible on jobs
    public Void getObject() throws NotImplementedException, TimeoutException,
            NoSuccessException {
        throw new NoSuccessException("getObject() called on Job");
    }

    public Void getResult() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NoSuccessException("getResult() called on Job");
    }

    public Void get() throws InterruptedException, ExecutionException {
        throw new SagaRuntimeException("get() called on Job");
    }

    public Void get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, java.util.concurrent.TimeoutException {
        throw new SagaRuntimeException("get() called on Job");
    }

    public void rethrow() throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        throw new NoSuccessException("rethrow() called on Job");
    }

    // Base implementations.

    public Task<org.ogf.saga.job.Job, Void> checkpoint(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Void>(
                this, sessionImpl, mode, "checkpoint", new Class[] {});
    }

    public org.ogf.saga.job.JobDescription getJobDescription()
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (jobDescriptionImpl == null) {
            throw new DoesNotExistException(
                    "No jobDescription available for this job");
        }
        return new JobDescriptionImpl(jobDescriptionImpl);
    }

    public Task<org.ogf.saga.job.Job, org.ogf.saga.job.JobDescription> getJobDescription(
            TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, org.ogf.saga.job.JobDescription>(
                this, sessionImpl, mode, "getJobDescription", new Class[] {});
    }

    public Task<org.ogf.saga.job.Job, InputStream> getStderr(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, InputStream>(
                this, sessionImpl, mode, "getStderr", new Class[] {});
    }

    public Task<org.ogf.saga.job.Job, OutputStream> getStdin(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, OutputStream>(
                this, sessionImpl, mode, "getStdin", new Class[] {});
    }

    public Task<org.ogf.saga.job.Job, InputStream> getStdout(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, InputStream>(
                this, sessionImpl, mode, "getStdout", new Class[] {});
    }

    public Task<org.ogf.saga.job.Job, Void> migrate(TaskMode mode,
            org.ogf.saga.job.JobDescription jd) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Void>(
                this, sessionImpl, mode, "resume",
                new Class[] { org.ogf.saga.job.JobDescription.class }, jd);
    }

    public Task<org.ogf.saga.job.Job, Void> resume(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Void>(
                this, sessionImpl, mode, "resume", new Class[] {});
    }

    public Task<org.ogf.saga.job.Job, Void> signal(TaskMode mode, int signum)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Void>(
                this, sessionImpl, mode, "signal",
                new Class[] { Integer.TYPE }, signum);
    }

    public Task<org.ogf.saga.job.Job, Void> suspend(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Void>(
                this, sessionImpl, mode, "suspend", new Class[] {});
    }

    public Task<org.ogf.saga.job.Job, Void> permissionsAllow(TaskMode mode,
            String id, int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Void>(
                this, sessionImpl, mode, "permissionsAllow", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<org.ogf.saga.job.Job, Boolean> permissionsCheck(TaskMode mode,
            String id, int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Boolean>(
                this, sessionImpl, mode, "permissionsCheck", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<org.ogf.saga.job.Job, Void> permissionsDeny(TaskMode mode,
            String id, int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, Void>(
                this, sessionImpl, mode, "permissionsDeny", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<org.ogf.saga.job.Job, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, String>(
                this, sessionImpl, mode, "getGroup", new Class[] {});
    }

    public Task<org.ogf.saga.job.Job, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<org.ogf.saga.job.Job, String>(
                this, sessionImpl, mode, "getOwner", new Class[] {});
    }

    public String[] findAttributes(String... patterns)
            throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public Task<org.ogf.saga.job.Job, String[]> findAttributes(TaskMode mode,
            String... patterns) throws NotImplementedException {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, String> getAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, String[]> getVectorAttribute(
            TaskMode mode, String key) throws NotImplementedException {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean existsAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.existsAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, Boolean> existsAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.existsAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, Boolean> isReadOnlyAttribute(
            TaskMode mode, String key) throws NotImplementedException {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, Boolean> isRemovableAttribute(
            TaskMode mode, String key) throws NotImplementedException {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, Boolean> isVectorAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, Boolean> isWritableAttribute(
            TaskMode mode, String key) throws NotImplementedException {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public Task<org.ogf.saga.job.Job, String[]> listAttributes(TaskMode mode)
            throws NotImplementedException {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        attributes.removeAttribute(key);
    }

    public Task<org.ogf.saga.job.Job, Void> removeAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public Task<org.ogf.saga.job.Job, Void> setAttribute(TaskMode mode,
            String key, String value) throws NotImplementedException {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public Task<org.ogf.saga.job.Job, Void> setVectorAttribute(TaskMode mode,
            String key, String[] values) throws NotImplementedException {
        return attributes.setVectorAttribute(mode, key, values);
    }

    protected void setValue(String key, String value)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        attributes.setValue(key, value);
    }

    protected void setVectorValue(String key, String[] value)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        attributes.setVectorValue(key, value);
    }
}
