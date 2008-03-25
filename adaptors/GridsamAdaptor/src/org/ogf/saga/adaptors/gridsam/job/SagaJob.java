package org.ogf.saga.adaptors.gridsam.job;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
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
import org.ogf.saga.impl.job.JobDescription;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.task.State;

/**
 * This is an implementation of the SAGA Job SPI on top of the GridSAM.
 * Some JobDescription attributes, Job Attributes and Job Metrics
 * unfortunately cannot be implemented on top of the GridSAM.
 * These are the JobDescription attributes
 * JobDescription.INTERACTIVE, JobDescription.THREADSPERPROCESS.
 * JobDescription.JOBCONTACT, JobDescription.JOBSTARTTIME,
 * the Job attribute Job.EXECUTIONHOSTS, the Job Metrics 
 * JOB_CPUTIME, JOB_MEMORYUSE, JOB_VMEMORYUSE, JOB_PERFORMANCE.
 * TODO: update this list.
 * In addition, the method {@link #signal(int)} cannot be implemented.
 */

public class SagaJob extends org.ogf.saga.impl.job.Job 
        implements JobInstanceChangeListener {

    private static final Logger logger = Logger.getLogger(SagaJob.class);

    private final JobServiceAdaptor service;

    private String jobID = null;

    private JobDefinitionDocument jobDefinitionDocument;

    private final String JSDL;

    private PollingThread pollingThread;
    
    private boolean created = false;
    private boolean started = false;
    private boolean stopped = false;
    // how many events have we fired already ?
    private int firedEventCount = 0;
    private JobState savedState = null;

    public SagaJob(JobServiceAdaptor service, JobDescription jobDescription,
            Session session) throws NotImplementedException, BadParameterException, NoSuccessException {
        super(jobDescription, session);
        this.service = service;
        JSDL = createJSDLDescription();
        if (logger.isDebugEnabled()) {
            logger.debug("Created JSDL " + JSDL);
        }

        try {
            jobDefinitionDocument = JobDefinitionDocument.Factory.parse(JSDL);
        } catch (XmlException e) {
            logger.error("Produced illegal xml: " + jobDefinitionDocument);
            throw new NoSuccessException("Illegal xml?", e);
        }
        JobInstance jobInstance;
        
        try {
            jobInstance = service.jobManager.submitJob(jobDefinitionDocument, true);
            // Unfortunately, notification is not yet supported!!
            // So, we use a polling thread instead.
            // service.jobManager.registerChangeListener(jobInstance.getID(), this);
        } catch (Throwable e1) {
            throw new NoSuccessException("Job submission failed", e1);
        }
 
        jobID = jobInstance.getID();

        String id = "[" + service.url + "]-[" + jobID + "]";
        try {
            setValue(JOBID, id);
        } catch (Throwable e) {
            // Should not happen.
        }
        service.addJob(this, id);
        
        // Polling thread needed as long as gridSAM does not support
        // registerChangeListener.
        pollingThread = new PollingThread(this);
        pollingThread.setDaemon(true);
    }

    private SagaJob(SagaJob orig) {
        super(orig);
        service = orig.service;
        JSDL = orig.JSDL;
        jobDefinitionDocument = orig.jobDefinitionDocument;
        jobID = orig.jobID;
        pollingThread = orig.pollingThread;
    }

    private String getV(String s) {
        try {
            s = jobDescription.getAttribute(s);
            if ("".equals(s)) {
                throw new Error("Not initialized");
            }
        } catch(Throwable e) {
            throw new Error("Not present");
        }
        return s;
    }

    private String[] getVec(String s) {
        String[] result;
        try {
            result = jobDescription.getVectorAttribute(s);
            if (result == null || result.length == 0) {
                throw new Error("Not initialized");
            }

        } catch(Throwable e) {
            throw new Error("Not present");
        }
        return result;
    }

    private String createJSDLDescription()
        throws BadParameterException, NotImplementedException, NoSuccessException {
        StringBuilder builder = new StringBuilder();
        addBegin(builder);
        addApplication(builder);
        addResource(builder);
        addDataStaging(builder);
        addEnd(builder);
        return builder.toString();
    }

    private StringBuilder addResource(StringBuilder builder) {
        String[] hosts;
        try {
            hosts = getVec(JobDescription.CANDIDATEHOSTS);
        } catch(Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("did not found any properties to use, not adding <Resources> tag");
            }
            return builder;
        }
        StringBuilder tmp = new StringBuilder();
        for (String host: hosts) {
            addSimpleTag(tmp, "HostName", host);
        }
        builder.append("<Resources><CandidateHosts>");
        builder.append(tmp);
        builder.append("</CandidateHosts></Resources>");
        return builder;
    }

    private StringBuilder addBegin(StringBuilder builder) {
        builder.append("<JobDefinition xmlns=\"http://schemas.ggf.org/jsdl/2005/11/jsdl\">" + "<JobDescription>" + "<JobIdentification>"
                + "<JobProject>gridsam</JobProject>" + "</JobIdentification>");
        return builder;
    }

    private StringBuilder addEnd(StringBuilder builder) {
        builder.append("</JobDescription>" + "</JobDefinition>");
        return builder;

    }

    private StringBuilder addApplication(StringBuilder builder) throws BadParameterException {
        String exec;
        try {
            exec = getV(JobDescription.EXECUTABLE);
        } catch(Throwable e) {
            throw new BadParameterException("Could not get Executable for job", e);
        }
        builder.append("<Application>");
        builder.append("<POSIXApplication xmlns=\"http://schemas.ggf.org/jsdl/2005/11/jsdl-posix\">");

        // add executable
        builder.append("<Executable>").append(exec).append("</Executable>");
        if (logger.isDebugEnabled()) {
            logger.debug("executable =" + exec);
        }

        String[] arguments;
        try {
            arguments = getVec(JobDescription.ARGUMENTS);
            for (String argument : arguments) {
                if (logger.isDebugEnabled()) {
                    logger.debug("argument=" + argument);
                }
                builder.append("<Argument>").append(argument).append("</Argument>");
            }
        } catch(Throwable e) {
            // ignored
        }

        try {
            String stdin = getV(JobDescription.INPUT);
            addSimpleTag(builder, "Input", stdin);
        } catch(Throwable e) {
            // ignored
        }

        try {
            String stdin = getV(JobDescription.OUTPUT);
            addSimpleTag(builder, "Output", stdin);
        } catch(Throwable e) {
            // ignored
        }

        try {
            String stdin = getV(JobDescription.ERROR);
            addSimpleTag(builder, "Error", stdin);
        } catch(Throwable e) {
            // ignored
        }

        try {
            String[] env = getVec(JobDescription.ENVIRONMENT);
            for (String e : env) {
                String key = e;
                String val;
                int index = e.indexOf('=');
                if (index == -1) {
                    val = "";
                } else {
                    key = e.substring(0, index);
                    val = e.substring(index+1);
                }
                builder.append("<Environment name=\"").append(key).append("\">").append(val).append("</Environment>");
            }
        } catch(Throwable e) {
            // ignored
        }

        addLimitsInfo(builder);

        addCpuInfo(builder);

        builder.append("</POSIXApplication></Application>");

        return builder;
    }

    private StringBuilder addDataStage(StringBuilder builder, String fileName,
            String type, String url, boolean deleteOnTermination) {
        builder.append("<DataStaging>");
        builder.append("<FileName>").append(fileName).append("</FileName>");
        builder.append("<CreationFlag>overwrite</CreationFlag><DeleteOnTermination>").append(deleteOnTermination ? "true" : "false").append("</DeleteOnTermination>");
        builder.append("<").append(type).append("><URI>").append(url).append("</URI></").append(type).append(">");
        builder.append("</DataStaging>");
        return builder;
    }

    private StringBuilder addDataStaging(StringBuilder builder) 
            throws BadParameterException, NotImplementedException {

        String[] transfers = null;

        try {
            transfers = getVec(JobDescription.FILETRANSFER);
        } catch(Throwable e) {
            return builder;
        }

        for (String s : transfers) {
            String[] parts = s.split(" << ");
            if (parts.length == 1) {
                // no match                  
            } else {
                throw new NotImplementedException("PostStage append is not supported");
            }
            parts = s.split(" >> ");
            if (parts.length == 1) {
                // no match                  
            } else {
                throw new NotImplementedException("PreStage append is not supported");
            }
            boolean prestage = true;
            parts = s.split(" > ");
            if (parts.length == 1) {
                prestage = false;
                parts = s.split(" < "); 
                if (parts.length == 1) {
                    throw new BadParameterException("Unrecognized FileTransfer part: "
                            + s);
                }
            }

            addDataStage(builder, parts[1], prestage ? "Source" : "Target",
                    parts[0], true);
        }

        return builder;
    }

    /**
     * Appends XML info about maximum CPU time, memory limits and so on.
     * 
     * @param builder
     */
    private void addLimitsInfo(StringBuilder builder) {
        try {
            addSimpleTag(builder, "TotalPhysicalMemory",
                    getV(JobDescription.TOTALPHYSICALMEMORY));
        } catch(Throwable e) {
            // ignored
        }
        try {
            addSimpleTag(builder, "TotalCPUTime",
                    getV(JobDescription.TOTALCPUTIME));
        } catch(Throwable e) {
            // ignored
        }
    }

    /**
     * Appends XML info about CPU type, OS type, ...
     * 
     * @param builder
     */
    private void addCpuInfo(StringBuilder builder) {
        try {
            addSimpleTag(builder, "CPUArchitecture",
                    getV(JobDescription.CPUARCHITECTURE));
        } catch(Throwable e) {
            // ignored
        }
        try {
            addSimpleTag(builder, "OperatingSystemType",
                    getV(JobDescription.OPERATINGSYSTEMTYPE));
        } catch(Throwable e) {
            // ignored
        }
    }

    private StringBuilder addSimpleTag(StringBuilder builder, String tagName, Object tagValue) {
        builder.append("<").append(tagName).append(">").append(tagValue.toString()).append("</").append(tagName).append(">");
        return builder;
    }

    void notImplemented(String s) throws NotImplementedException {
        try {
            s = getV(s);           
        } catch(Throwable e) {
            // If getV throws an exception, the attribute string is not used,
            // so it does not matter that it is not implemented :-)
            return;
        } 
        throw new NotImplementedException(s + " not implemented");
    }

    public synchronized void cancel(float timeoutInSeconds) throws NotImplementedException, IncorrectStateException, TimeoutException, NoSuccessException {
        if (state == State.NEW) {
            throw new IncorrectStateException("cancel() called on job in state New");
        }
        if (isDone()) {
            return;
        }
        try {
            service.jobManager.terminateJob(jobID);
        }  catch (Throwable e) {
            logger.error("unable to cancel job with id " + jobID, e);
            throw new NoSuccessException("unable to cancel job with id " + jobID, e);
        } 
        setState(State.CANCELED);
    }

    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (state == State.RUNNING || state == State.SUSPENDED) {
            if (mayInterruptIfRunning) {
                try {
                    cancel(0.0F);
                } catch(Throwable e) {
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
        return state == State.FAILED || state == State.DONE || state == State.CANCELED;
    }

    public synchronized void run() throws NotImplementedException, IncorrectStateException, TimeoutException, NoSuccessException {
        if (state != State.NEW) {
            throw new IncorrectStateException("run() called on job in state " + state);
        }

        setState(State.RUNNING);

        pollingThread.start();
        try {
            service.jobManager.startJob(jobID);
        } catch (Throwable e1) {
            setState(State.FAILED);
            stopped = true;             // finishes polling thread.
            throw new NoSuccessException("Job start failed");
        }

    }

    private void setDetail(String s) {
        try {
            jobStateDetail.setValue("GridSAM." + s);
            jobStateDetail.internalFire();
        } catch(Throwable e) {
            // ignored
        }
    }

    public synchronized boolean waitFor(float timeoutInSeconds)
            throws NotImplementedException, IncorrectStateException, TimeoutException, NoSuccessException {
        switch(state) {
        case NEW:
            throw new IncorrectStateException("waitFor called on new job");
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
                    } catch(Exception e) {
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
                    } catch(Exception e) {
                        // ignored
                    }
                    currentTime = System.currentTimeMillis();
                }
            }
        }

        return state != State.SUSPENDED && state != State.RUNNING;
    }

    public void checkpoint() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
           NoSuccessException {
               throw new NotImplementedException("checkpoint() is not implemented");
    }

    public InputStream getStderr() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException,
           TimeoutException, IncorrectStateException, NoSuccessException {
               throw new NotImplementedException("getStderr is not implemented, "
                       + "gridSAM does not support interactive jobs");
    }

    public OutputStream getStdin() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException,
           TimeoutException, IncorrectStateException, NoSuccessException {
               throw new NotImplementedException("getStdin is not implemented, "
                       + "gridSAM does not support interactive jobs");
    }

    public InputStream getStdout() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException,
           TimeoutException, IncorrectStateException, NoSuccessException {       
               throw new NotImplementedException("getStdout is not implemented, "
                       + "gridSAM does not support interactive jobs");
    }

    public void migrate(org.ogf.saga.job.JobDescription jd) throws NotImplementedException,
           AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
           BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException {
               throw new NotImplementedException("migrate() not implemented");
    }

    public void resume() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
           NoSuccessException {
               throw new NotImplementedException("resume() not implemented");
    }

    public void signal(int signum) throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, BadParameterException,
           IncorrectStateException, TimeoutException, NoSuccessException {
               throw new NotImplementedException("signal() not implemented");

    }

    public void suspend() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
           NoSuccessException {
               throw new NotImplementedException("suspend() not implemented");
    }

    public String getGroup() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
               throw new NotImplementedException("getGroup() not supported");
    }

    public String getOwner() throws NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
               throw new NotImplementedException("getOwner not supported");
    }

    public void permissionsAllow(String id, int permissions)
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
                          PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
                   throw new NotImplementedException("permissionsAllow not supported");
    }

    public boolean permissionsCheck(String id, int permissions)
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
                          PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
                   throw new NotImplementedException("permissionsCheck not supported");
    }

    public void permissionsDeny(String id, int permissions)
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
                          PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
                   throw new NotImplementedException("permissionsDeny not supported");        
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
            if (! created) {
                created = true;
                try {
                    setValue(CREATED, "" + stage.getDate().getTime());
                } catch (Throwable e) {
                    // ignored
                }
            }
        } else if (state.equals(JobState.ACTIVE) || state.equals(JobState.EXECUTED)
                || state.equals(JobState.STAGED_IN) || state.equals(JobState.STAGING_IN)
                || state.equals(JobState.STAGED_OUT) || state.equals(JobState.STAGING_OUT)) {
            if (! started) {
                started = true;
                try {
                    setValue(STARTED, "" + stage.getDate().getTime());
                } catch (Throwable e) {
                    // ignored
                }
            }
        } else if (state.equals(JobState.TERMINATED)
                || state.equals(JobState.DONE)
                || state.equals(JobState.FAILED)) {
            if (! stopped) {
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
        synchronized(this) {
            notifyAll();
        }
    }
    
    private static class PollingThread extends Thread {
        private Logger logger = Logger.getLogger(PollingThread.class);

        private SagaJob parent;
               
        public PollingThread(SagaJob parent) {
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            while (true) {
                // update the manager state of the job
                JobInstance jobInstance;
                try {
                    jobInstance = parent.service.jobManager.findJobInstance(parent.jobID);
                } catch(Throwable e) {
                    logger.error("got exception", e);
                    throw new RuntimeException(e);
                }
                if (logger.isDebugEnabled()) {
                    StringBuilder props = new StringBuilder();
                    Map properties = jobInstance.getProperties();
                    Iterator iterator = properties.keySet().iterator();
                    while (iterator.hasNext()) {
                        Object next = iterator.next();
                        props.append("\n    ").append(next).append("=").append(properties.get(next));
                    }
                    logger.debug("job properties (from GridSAM)=" + props.toString());
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
        List stages = jobInstance.getJobStages();
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
