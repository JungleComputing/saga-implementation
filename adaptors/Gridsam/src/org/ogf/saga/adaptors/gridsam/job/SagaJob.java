package org.ogf.saga.adaptors.gridsam.job;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.icenigrid.gridsam.core.JobInstance;
import org.icenigrid.gridsam.core.JobInstanceChangeListener;
import org.icenigrid.gridsam.core.JobStage;
import org.icenigrid.gridsam.core.JobState;
import org.icenigrid.schema.jsdl.y2005.m11.JobDefinitionDocument;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.job.JobDescriptionImpl;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.task.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an implementation of the SAGA Job SPI on top of the GridSAM. Some
 * JobDescription attributes, Job Attributes and Job Metrics unfortunately
 * cannot be implemented on top of the GridSAM. These are the JobDescription
 * attributes JobDescription.INTERACTIVE, JobDescription.THREADSPERPROCESS.
 * JobDescription.JOBCONTACT, JobDescription.JOBSTARTTIME, the Job attribute
 * Job.EXECUTIONHOSTS, the Job Metrics JOB_CPUTIME, JOB_MEMORYUSE,
 * JOB_VMEMORYUSE, JOB_PERFORMANCE. In addition, the method {@link #signal(int)}
 * cannot be implemented.
 */

public final class SagaJob extends org.ogf.saga.impl.job.JobImpl implements
        JobInstanceChangeListener, Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(SagaJob.class);
    
    private static final String AXIS_CONFIG = "axis.ClientConfigFile";

    private final JobServiceAdaptor service;

    private String jobID = null;

    private JobDefinitionDocument jobDefinitionDocument;

    private PollingThread pollingThread;

    private boolean created = false;

    private boolean started = false;

    private boolean stopped = false;

    // how many events have we fired already ?
    private int firedEventCount = 0;

    private JobState savedState = null;

    /**
     * Some methods of this class are called directly, not through a Saga engine
     * dispatcher. Therefore, these methods have to set the context classloader.
     */
    private static ClassLoader loader = SagaJob.class.getClassLoader();

    public SagaJob(JobServiceAdaptor service,
            JobDescriptionImpl jobDescriptionImpl, SessionImpl sessionImpl)
            throws NotImplementedException, BadParameterException,
            NoSuccessException {
        super(jobDescriptionImpl, sessionImpl);
        this.service = service;

        jobDefinitionDocument = new JSDLGenerator(jobDescriptionImpl, service
                .getWrapper()).getJSDL();

        if (logger.isDebugEnabled()) {
            logger.debug("Created JSDL " + jobDefinitionDocument.toString());
        }

        JobInstance jobInstance;

        // Take care of axis.ClientConfigFile system property: it may
        // be set by some Globus adaptor, but GridSAM cannot stand that.
        // So, save and restore it.
        synchronized(SagaJob.class) {
            
            String saved = System.getProperty(AXIS_CONFIG);
            if (saved != null) {
                System.clearProperty(AXIS_CONFIG);
            }

            try {
                jobInstance = service.jobManager.submitJob(
                        jobDefinitionDocument, true);
                // Unfortunately, notification is not yet supported!!
                // So, we use a polling thread instead.
                // service.jobManager.registerChangeListener(jobInstance.getID(),
                // this);
            } catch (Throwable e1) {
                throw new NoSuccessException("Job submission failed", e1,
                        service.getWrapper());
            } finally {
                if (saved != null) {
                    System.setProperty(AXIS_CONFIG, saved);
                } else {
                    System.clearProperty(AXIS_CONFIG);
                }
            }
        }
 
        jobID = jobInstance.getID();

        String id = "[" + service.url + "]-[" + jobID + "]";
        try {
            setValue(JOBID, id);
        } catch (Throwable e) {
            // Should not happen.
        }
        try {
            String w = jobDescriptionImpl.getAttribute(JobDescription.WORKINGDIRECTORY);
            setValue(WORKINGDIRECTORY, w);
        } catch(Throwable e) {
            // ignored, should not happen.
        }
        service.addJob(this, id);

        // Polling thread needed as long as gridSAM does not support
        // registerChangeListener.
        pollingThread = new PollingThread(this);
        pollingThread.setDaemon(true);
        pollingThread.setContextClassLoader(loader);
    }

    private SagaJob(SagaJob orig) {
        super(orig);
        service = orig.service;
        jobDefinitionDocument = orig.jobDefinitionDocument;
        jobID = orig.jobID;
        pollingThread = orig.pollingThread;
    }

    public synchronized void cancel(float timeoutInSeconds)
            throws NotImplementedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        if (state == State.NEW) {
            throw new IncorrectStateException(
                    "cancel() called on job in state New", this);
        }
        if (isDone()) {
            return;
        }
        ClassLoader saved = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            service.jobManager.terminateJob(jobID);
        } catch (Throwable e) {
            logger.error("unable to cancel job with id " + jobID, e);
            throw new NoSuccessException("unable to cancel job with id "
                    + jobID, e, this);
        } finally {
            Thread.currentThread().setContextClassLoader(saved);
        }
        setState(State.CANCELED);
    }

    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (state == State.RUNNING || state == State.SUSPENDED) {
            if (mayInterruptIfRunning) {
                try {
                    cancel(0.0F);
                } catch (Throwable e) {
                    // ignored
                }
                return true;
            }
            return false;
        }
        if (isDone()) {
            return false;
        }
        setState(State.CANCELED);
        return true;
    }

    public synchronized boolean isCancelled() {
        return state == State.CANCELED;
    }

    public synchronized boolean isDone() {
        return state == State.FAILED || state == State.DONE
                || state == State.CANCELED;
    }

    public synchronized void run() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (state != State.NEW) {
            throw new IncorrectStateException("run() called on job in state "
                    + state, this);
        }

        setState(State.RUNNING);

        ClassLoader savedLoader = Thread.currentThread()
                .getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);

        synchronized (SagaJob.class) {
            // Take care of axis.ClientConfigFile system property: it may
            // be set by some Globus adaptor, but GridSAM cannot stand that.
            // So, save and restore it.
            String saved = System.getProperty(AXIS_CONFIG);
            if (saved != null) {
                System.clearProperty(AXIS_CONFIG);
            }

            pollingThread.start();

            try {
                service.jobManager.startJob(jobID);
            } catch (Throwable e1) {
                setState(State.FAILED);
                stopped = true; // finishes polling thread.
                throw new NoSuccessException("Job start failed", e1, this);
            } finally {
                if (saved != null) {
                    System.setProperty(AXIS_CONFIG, saved);
                } else {
                    System.clearProperty(AXIS_CONFIG);
                }
                Thread.currentThread().setContextClassLoader(savedLoader);
            }
        }
    }

    private void setDetail(String s) {
        try {
            jobStateDetail.setValue("GridSAM." + s);
            jobStateDetail.internalFire();
        } catch (Throwable e) {
            // ignored
        }
    }

    public synchronized boolean waitFor(float timeoutInSeconds)
            throws NotImplementedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        switch (state) {
        case NEW:
            throw new IncorrectStateException("waitFor called on new job", this);
        case DONE:
        case CANCELED:
        case FAILED:
            return true;
        case SUSPENDED:
        case RUNNING:
            if (timeoutInSeconds < 0) {
                while (state == State.SUSPENDED || state == State.RUNNING) {
                    try {
                        wait();
                    } catch (Exception e) {
                        // ignored
                    }
                }
            } else {
                long interval = (long) (timeoutInSeconds * 1000.0);
                long currentTime = System.currentTimeMillis();
                long endTime = currentTime + interval;
                while ((state == State.SUSPENDED || state == State.RUNNING)
                        && currentTime < endTime) {
                    interval = endTime - currentTime;
                    try {
                        wait(interval);
                    } catch (Exception e) {
                        // ignored
                    }
                    currentTime = System.currentTimeMillis();
                }
            }
        }

        return state != State.SUSPENDED && state != State.RUNNING;
    }

    public void checkpoint() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("checkpoint() is not implemented",
                this);
    }

    public InputStream getStderr() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, TimeoutException, IncorrectStateException,
            NoSuccessException {
        throw new NotImplementedException("getStderr is not implemented, "
                + "gridSAM does not support interactive jobs", this);
    }

    public OutputStream getStdin() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, TimeoutException, IncorrectStateException,
            NoSuccessException {
        throw new NotImplementedException("getStdin is not implemented, "
                + "gridSAM does not support interactive jobs", this);
    }

    public InputStream getStdout() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, TimeoutException, IncorrectStateException,
            NoSuccessException {
        throw new NotImplementedException("getStdout is not implemented, "
                + "gridSAM does not support interactive jobs", this);
    }

    public void migrate(org.ogf.saga.job.JobDescription jd)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("migrate() not implemented", this);
    }

    public void resume() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("resume() not implemented", this);
    }

    public void signal(int signum) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("signal() not implemented", this);

    }

    public void suspend() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("suspend() not implemented", this);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getGroup() not supported", this);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getOwner not supported", this);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsAllow not supported",
                this);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsCheck not supported",
                this);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsDeny not supported", this);
    }

    public Object clone() {
        return new SagaJob(this);
    }

    private String getExitCode(JobInstance jobInstance) throws Exception {

        String code = (String) jobInstance.getProperties().get(
                "urn:gridsam:exitcode");
        if (code == null) {
            logger.info("No exitcode from gridsam available");
            throw new Exception();
        }
        return code;
    }

    private void fireEvent(JobInstance jobInstance, JobStage stage) {
        JobState state = stage.getState();
        if (state.equals(savedState)) {
            return;
        }
        savedState = state;
        setDetail(state.toString());
        if (state.equals(JobState.TERMINATING)) {
        } else if (state.equals(JobState.PENDING)) {
            if (!created) {
                created = true;
                try {
                    setValue(CREATED, "" + stage.getDate().getTime());
                } catch (Throwable e) {
                    // ignored
                }
            }
        } else if (state.equals(JobState.ACTIVE)
                || state.equals(JobState.EXECUTED)
                || state.equals(JobState.STAGED_IN)
                || state.equals(JobState.STAGING_IN)
                || state.equals(JobState.STAGED_OUT)
                || state.equals(JobState.STAGING_OUT)) {
            if (!started) {
                started = true;
                try {
                    setValue(STARTED, "" + stage.getDate().getTime());
                } catch (Throwable e) {
                    // ignored
                }
            }
        } else if (state.equals(JobState.TERMINATED)
                || state.equals(JobState.DONE) || state.equals(JobState.FAILED)) {
            if (!stopped) {
                stopped = true;
                try {
                    setValue(FINISHED, "" + stage.getDate().getTime());
                } catch (Throwable e) {
                    // ignored
                }
                try {
                    String exitCode = getExitCode(jobInstance);
                    setValue(EXITCODE, exitCode);
                    int exitval = Integer.parseInt(exitCode);
                    if ((((exitval & 127) + 1) >> 1) > 0) {
                        setValue(TERMSIG, "" + (exitval & 127));
                        jobSignal.setValue("" + (exitval & 127));
                        jobSignal.internalFire();
                    }
                } catch (Throwable e) {
                    // ignored
                }
            }
            setState(state.equals(JobState.TERMINATED) ? State.CANCELED
                    : (state.equals(JobState.DONE) ? State.DONE : State.FAILED));
        } else if (state.equals(JobState.UNDEFINED)) {
        } else {
            logger.warn("unknown job state: " + jobState.toString());
        }
        synchronized (this) {
            notifyAll(); // findbugs complains about a naked notify ...
            // but this is OK.
        }
    }

    private static class PollingThread extends Thread {
        private Logger logger = LoggerFactory.getLogger(PollingThread.class);

        private SagaJob parent;

        public PollingThread(SagaJob parent) {
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            while (true) {
                // update the manager state of the job
                JobInstance jobInstance;
                // Take care of axis.ClientConfigFile system property: it may
                // be set by some Globus adaptor, but GridSAM cannot stand that.
                // So, save and restore it.
                synchronized (SagaJob.class) {
                    String saved = System.getProperty(AXIS_CONFIG);
                    if (saved != null) {
                        System.clearProperty(AXIS_CONFIG);
                    }
                    try {
                        jobInstance = parent.service.jobManager
                                .findJobInstance(parent.jobID);
                    } catch (Throwable e) {
                        logger.error("got exception", e);
                        throw new RuntimeException(e);
                    } finally {
                        if (saved != null) {
                            System.setProperty(AXIS_CONFIG, saved);
                        } else {
                            System.clearProperty(AXIS_CONFIG);
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    StringBuilder props = new StringBuilder();
                    Map<Object, Object> properties = jobInstance
                            .getProperties();
                    for (Map.Entry<Object, Object> entry : properties
                            .entrySet()) {
                        props.append("\n    ").append(entry.getKey()).append(
                                "=").append(properties.get(entry.getValue()));
                    }
                    logger.debug("job properties (from GridSAM)="
                            + props.toString());
                }

                parent.onChange(jobInstance);

                if (parent.stopped) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("sleeping interrupted");
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("polling thread exiting, gridsam job is finished");
            }
        }
    }

    public void onChange(JobInstance jobInstance) {
        List<?> stages = jobInstance.getJobStages();
        if (stages.size() > firedEventCount) {
            // There are some event that were not fired
            // (and we don't know about). Lets get that knowledge :)
            // We fire the event for each of the states we have found.
            // We can get ith element because:
            // 1. this list is not big;
            // 2. GridSAM actually uses ArrayList for this.
            for (int i = firedEventCount; i < stages.size(); i++) {
                fireEvent(jobInstance, (JobStage) stages.get(i));
            }

            // finally we set that we have fired every event
            firedEventCount = stages.size();
        }
    }
}
