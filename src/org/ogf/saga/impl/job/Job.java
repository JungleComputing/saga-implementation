package org.ogf.saga.impl.job;

import java.io.InputStream;
import java.io.OutputStream;
import org.ogf.saga.ObjectType;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/*
 * Jobs are not created by SAGA applications, so there is no SPI for it.
 * Rather, a JobService SPI can use this class as a base class to construct jobs
 * from.
 */
public abstract class Job extends org.ogf.saga.impl.task.Task implements
        org.ogf.saga.job.Job {

    protected final JobAttributes attributes;
    protected final JobDescription jobDescription;
    protected final Metric jobState;
    protected final Metric jobStateDetail;
    protected final Metric jobSignal;
    protected final Metric jobCpuTime;
    protected final Metric jobMemoryUse;
    protected final Metric jobVMemoryUse;
    protected final Metric jobPerformance;
    
    public Job(JobDescription jobDescription, Session session)
            throws NotImplemented, BadParameter {
        super(session);
        attributes = new JobAttributes(this, session);
        this.jobDescription = jobDescription;
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

    public ObjectType getType() {
        return ObjectType.JOB;
    }

    public Task checkpoint(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "checkpoint", new Class[] { });
    }

    public org.ogf.saga.job.JobDescription getJobDescription() throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        if (jobDescription == null) {
            throw new DoesNotExist("No jobDescription available for this job");
        }
        try { 
            return (JobDescription) jobDescription.clone();
        } catch(CloneNotSupportedException e) {
            // Should not happen.
            throw new NoSuccess("getJobDescription failure", e);
        }
    }

    public Task<org.ogf.saga.job.JobDescription> getJobDescription(TaskMode mode)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.job.JobDescription>(
                this, session, mode, "getJobDescription", new Class[] { } );
    }

    public Task<InputStream> getStderr(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<InputStream>(this, session, mode,
                "getStderr", new Class[] { });
    }

    public Task<OutputStream> getStdin(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<OutputStream>(this, session, mode,
                "getStdin", new Class[] { });
    }

    public Task<InputStream> getStdout(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<InputStream>(this, session, mode,
                "getStdout", new Class[] { });
    }

    public Task migrate(TaskMode mode, org.ogf.saga.job.JobDescription jd)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "resume", new Class[] { org.ogf.saga.job.JobDescription.class }, jd);
    }

    public Task resume(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "resume", new Class[] { });
    }

    public Task signal(TaskMode mode, int signum) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "signal", new Class[] { Integer.TYPE }, signum);
    }

    public Task suspend(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "suspend", new Class[] { });
    }

    public Object getResult() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        throw new NoSuccess("getResult() not available for jobs");
    }

    public void rethrow() throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        throw new NoSuccess("rethrow() not available for jobs");
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
    throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsAllow", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "permissionsCheck", new Class[] { String.class,
                Integer.TYPE }, id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
    throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsDeny", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(this, session,
                mode, "getGroup", new Class[] {});
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(this, session,
                mode, "getOwner", new Class[] {});
    }

    public String[] findAttributes(String... patterns) throws NotImplemented,
            BadParameter, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, Timeout, NoSuccess {
        return attributes.findAttributes(patterns);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplemented {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getAttribute(key);
    }

    public Task<String> getAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isReadOnlyAttribute(key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isRemovableAttribute(key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isVectorAttribute(key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isWritableAttribute(key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return attributes.listAttributes();
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplemented {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        attributes.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        attributes.setAttribute(key, value);
    }

    public Task setAttribute(TaskMode mode, String key, String value)
            throws NotImplemented {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, DoesNotExist,
            Timeout, NoSuccess {
        attributes.setVectorAttribute(key, values);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplemented {
        return attributes.setVectorAttribute(mode, key, values);
    }

}
