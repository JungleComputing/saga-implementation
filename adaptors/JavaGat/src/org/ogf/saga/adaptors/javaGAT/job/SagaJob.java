package org.ogf.saga.adaptors.javaGAT.job;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;
import org.gridlab.gat.monitoring.MetricDefinition;
import org.gridlab.gat.monitoring.MetricEvent;
import org.gridlab.gat.monitoring.MetricListener;
import org.gridlab.gat.resources.HardwareResourceDescription;
import org.gridlab.gat.resources.SoftwareDescription;
import org.gridlab.gat.resources.SoftwareResourceDescription;
import org.gridlab.gat.security.SecurityContext;
import org.ogf.saga.URL;
import org.ogf.saga.adaptors.javaGAT.namespace.NSEntryAdaptor;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.job.JobDescription;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.task.State;

/**
 * This is an implementation of the SAGA Job SPI on top of the JavaGAT.
 * Some JobDescription attributes, Job Attributes and Job Metrics unfortunately cannot
 * be implemented on top of the JavaGAT. These are the JobDescription attributes
 * JobDescription.INTERACTIVE, JobDescription.THREADSPERPROCESS.
 * JobDescription.JOBCONTACT, JobDescription.JOBSTARTTIME,
 * the Job attribute Job.TERMSIG, the Job Metrics 
 * JOB_SIGNAL, JOB_CPUTIME, JOB_MEMORYUSE, JOB_VMEMORYUSE, JOB_PERFORMANCE.
 * In addition, the method {@link #signal(int)} cannot be implemented.
 * Apart from that, JavaGAT at least has the interface to support SAGA Jobs.
 * How much actually is implemented depends on the JavaGAT adaptor at hand.
 */

public class SagaJob extends org.ogf.saga.impl.job.Job implements MetricListener {
    
    private static final Logger logger = Logger.getLogger(SagaJob.class);

    private final JobServiceAdaptor service;
    private org.gridlab.gat.resources.Job gatJob = null;
    private final GATContext gatContext;
    private org.gridlab.gat.resources.JobDescription gatJobDescription;
    private int savedState = -1;
    
    private static int jobCount = 0;
    
    public SagaJob(JobServiceAdaptor service, JobDescription jobDescription,
            Session session, GATContext gatContext) throws NotImplementedException,
            BadParameterException, NoSuccessException {
        super(jobDescription, session);
        this.service = service;
        this.gatContext = gatContext;
        gatJobDescription = new org.gridlab.gat.resources.JobDescription(
                createSoftwareDescription(), createHardwareResourceDescription());
        if (logger.isDebugEnabled()) {
            logger.debug("Created gatJobDescription " + gatJobDescription);
        }
    }
    
    private SagaJob(SagaJob orig) {
        super(orig);
        gatContext = new GATContext();
        gatContext.addPreferences(orig.gatContext.getPreferences());
        for (SecurityContext c : orig.gatContext.getSecurityContexts()) {
            gatContext.addSecurityContext(c);           
        }
        service = orig.service;
        gatJob = orig.gatJob;
        savedState = orig.savedState;
        gatJobDescription = new org.gridlab.gat.resources.JobDescription(
                orig.gatJobDescription.getSoftwareDescription(),
                createHardwareResourceDescription());
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
    
    private SoftwareDescription createSoftwareDescription()
            throws BadParameterException, NotImplementedException, NoSuccessException {
        try {
            String s = getV(JobDescription.INTERACTIVE);
            if ("True".equals(s)) {
                throw new NotImplementedException("Interactive jobs cannot be implemented on JavaGAT");
            }
        } catch(Throwable e) {
            // ignored
        }
        SoftwareDescription sd = new SoftwareDescription();
               
        // Strange default in JavaGat? Keep sandbox?
        sd.addAttribute("sandbox.delete", "true");
        
        try {
            String s = getV(JobDescription.EXECUTABLE);
            sd.setExecutable(s);
         } catch(Throwable e) {
            throw new BadParameterException("Could not get Executable for job", e);
        }
        try {
            sd.setArguments(getVec(JobDescription.ARGUMENTS));
        } catch(Throwable e) {
            // ignored
        }
        try {
            String[] env = getVec(JobDescription.ENVIRONMENT);
            HashMap<String, Object> environment = new HashMap<String, Object>();
            for (String e : env) {
                int index = e.indexOf('=');
                if (index == -1) {
                    environment.put(e, "");
                } else {
                    environment.put(e.substring(0, index), e.substring(index+1));
                }
            }
            sd.setEnvironment(environment);
        } catch(Throwable e) {
            // ignored
        }
        try {
            sd.addAttribute("count", getV(JobDescription.NUMBEROFPROCESSES));
        } catch(Throwable e) {
            // ignored
        }
        try {
            sd.addAttribute("job.type", getV(JobDescription.SPMDVARIATION));
        } catch(Throwable e) {
            // ignored
        }
        
        // notImplemented(JobDescription.THREADSPERPROCESS);
        // notImplemented(JobDescription.JOBCONTACT);
        // notImplemented(JobDescription.JOBSTARTTIME);

        try {
            // What to do if PROCESSESPERHOST is set but NUMBEROFPROCESSES is not???
            sd.addAttribute("host.count", 
                    Integer.parseInt(getV(JobDescription.NUMBEROFPROCESSES))
                    / Integer.parseInt(getV(JobDescription.PROCESSESPERHOST)));
        } catch(Throwable e) {
            // ignored
        }
        try {
            sd.addAttribute("directory", getV(JobDescription.WORKINGDIRECTORY));
        } catch(Throwable e) {
            sd.addAttribute("directory", ".");
        }
        try {
            sd.addAttribute("queue", getV(JobDescription.QUEUE));
        } catch(Throwable e) {
            // ignored
        }
        try {
            sd.addAttribute("memory.min", getV(JobDescription.TOTALPHYSICALMEMORY));
        } catch(Throwable e) {
            // ignored
        }
        try {
            sd.addAttribute("cputime.max", getV(JobDescription.TOTALCPUTIME));
        } catch(Throwable e) {
            // ignored
        }
        try {
            sd.addAttribute("save.state", 
                    ("True".equals(getV(JobDescription.CLEANUP)) ? "false" : "true"));
        } catch(Throwable e) {
            // ignored
        }
        
        URI stdin = getURI(JobDescription.INPUT);

        URI stdout = getURI(JobDescription.OUTPUT);

        URI stderr = getURI(JobDescription.ERROR);
        
        String[] transfers = null;
        
        try {
            transfers = getVec(JobDescription.FILETRANSFER);
        } catch(Throwable e) {
            // ignored
        }
        
        if (transfers != null) {
            for (int i = 0; i < transfers.length; i++) {
                String[] parts = transfers[i].split(" << ");
                if (parts.length == 1) {
                    // no match                  
                } else {
                    throw new NotImplementedException("PostStage append is not supported");
                }
                parts = transfers[i].split(" >> ");
                if (parts.length == 1) {
                    // no match                  
                } else {
                    throw new NotImplementedException("PreStage append is not supported");
                }
                boolean prestage = true;
                parts = transfers[i].split(" > ");
                if (parts.length == 1) {
                    prestage = false;
                    parts = transfers[i].split(" < "); 
                    if (parts.length == 1) {
                        throw new BadParameterException("Unrecognized FileTransfer part: " + transfers[i]);
                    }
                }
                
                URI s1 = null;
                URI s2 = null;
                try {
                    s1 = NSEntryAdaptor.cvtToGatURI(new URL(parts[0]));
                    s2 = NSEntryAdaptor.cvtToGatURI(new URL(parts[1]));
                } catch (BadParameterException e) {
                    throw e;
                }
                
                if (! prestage) {
                    if (stdout != null && ! stdout.isAbsolute() && s2.equals(stdout)) {
                        // In SAGA, a non-absolute stdout is relative to target
                        // and is probably staged-out explicitly.
                        // In javaGAT we should use the destination instead.
                        stdout = s1;
                        continue;
                    }
                    if (stderr != null && ! stderr.isAbsolute() && s2.equals(stderr)) {
                        stderr = s1;
                        continue;
                    }
                } else if (stdin != null && ! stdin.isAbsolute() && s2.equals(stdin)) {
                    // In SAGA, a non-absolute stdin is relative to target
                    // and is probably staged-in explicitly.
                    // In javaGAT we should use the source instead.
                    stdin = s1;
                    continue;
                }

                File f1 = createFile(s1);
                File f2 = createFile(s2);

                if (prestage) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Add prestage: src = " + s1 + ", dst = " + s2);
                    }
                    sd.addPreStagedFile(f1, f2);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Add poststage: dst = " + s1 + ", src = " + s2);
                    }
                    sd.addPostStagedFile(f2, f1);
                }
            }
        }
        
        if (stdin != null) {
            sd.setStdin(createFile(stdin));
        }
        
        if (stdout != null) {
            sd.setStdout(createFile(stdout));
        }
        
        if (stderr != null) {
            sd.setStderr(createFile(stderr));
        }
      
        return sd;
    }
    
    private File createFile(URI uri) throws BadParameterException {
        try {
            return GAT.createFile(gatContext, uri);
        } catch (GATObjectCreationException e) {
            throw new BadParameterException("Could not create GAT File for " + uri, e);
        }
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
    
    private HardwareResourceDescription createHardwareResourceDescription() {
        HardwareResourceDescription hd = new HardwareResourceDescription();
        try {
            String s = getV(JobDescription.TOTALCPUCOUNT);
            hd.addResourceAttribute("cpu.count", s);
        } catch(Throwable e) {
            // ignored
        }
        
        try {
            String[] hosts = getVec(JobDescription.CANDIDATEHOSTS);
            hd.addResourceAttribute("machine.node", hosts);
        } catch(Throwable e) {
            // ignored
        }
        
        try {
            hd.addResourceAttribute("cpu.type",
                    getV(JobDescription.CPUARCHITECTURE));
        } catch(Throwable e) {
            // ignored
        }
        
        try {
            String s = getV(JobDescription.OPERATINGSYSTEMTYPE);
            SoftwareResourceDescription sd = new SoftwareResourceDescription();
            sd.addResourceAttribute("os.type", s);
            hd.addResourceDescription(sd);
        } catch(Throwable e) {
            // ignored
        }
        
        return hd;
    }
    
    private URI getURI(String s) throws NoSuccessException, BadParameterException  {
        try {
            URL url = new URL(getV(s)); 
            return NSEntryAdaptor.cvtToGatURI(url);
        } catch(BadParameterException e) {
            throw e;
        } catch (Throwable e) {
            // ignored
        }
        return null;       
    }
    
    @Override
    public synchronized void cancel(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (state == State.NEW) {
            throw new IncorrectStateException("cancel() called on job in state New");
        }
        try {
            gatJob.stop();
        } catch (GATInvocationException e) {
            throw new NoSuccessException("Could not cancel job");
        }
        setState(State.CANCELED);
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (state == State.RUNNING || state == State.SUSPENDED) {
            if (mayInterruptIfRunning) {
                try {
                    gatJob.stop();
                } catch (GATInvocationException e) {
                    throw new SagaRuntimeException("Could not cancel job");
                }
                setState(State.CANCELED);
                return true;
            }
            return false;
        }
        if (state == State.CANCELED || state == State.FAILED || state == State.DONE) {
            return false;
        }
        setState(State.CANCELED);
        return true;
    }

    @Override
    public synchronized boolean isCancelled() {
        return state == State.CANCELED;
    }

    @Override
    public synchronized boolean isDone() {
        return state == State.FAILED || state == State.DONE || state == State.CANCELED;
    }

    @Override
    public synchronized void run() throws NotImplementedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        if (state != State.NEW) {
            throw new IncorrectStateException("run() called on job in state " + state);
        }
              
        setState(State.RUNNING);
        try {
            // gatJob = service.broker.submitJob(gatJobDescription, this, "job.status");
            // Does not work properly yet (javaGAT), but the code below has a race.
            
            gatJob = service.broker.submitJob(gatJobDescription);
            MetricDefinition md = gatJob.getMetricDefinitionByName("job.status");
            gatJob.addMetricListener(this, md.createMetric(null));

        } catch (GATInvocationException e) {
            setState(State.FAILED);
            throw new NoSuccessException("Job.run() failed", e);
        }
        String id;
        try {
            id = gatJob.getJobID();
        } catch (Throwable e) {
            // Apparently not provided by JavaGAT adaptor ...`
            id = "" + jobCount ++;
        }
        id = "[" + JobServiceAdaptor.JAVAGAT + "]-[" + id + "]";
        try {
            setValue(JOBID, id);
        } catch (Throwable e) {
            // Should not happen.
        }
        service.addJob(this, id);
    }
    
    private void setDetail(String s) {
        try {
            jobStateDetail.setValue(JobServiceAdaptor.JAVAGAT + "." + s);
            jobStateDetail.internalFire();
        } catch(Throwable e) {
            // ignored
        }
    }
    
    public synchronized void processMetricEvent(MetricEvent val) {
        int gatState = gatJob.getState();
        if (gatState == savedState) {
            return;
        }
        savedState = gatState;
        Map<String, Object> info = null;
        if (logger.isDebugEnabled()) {
            logger.debug("processMetricEvent: " + val);
        }
        try {
            info = gatJob.getInfo();
        } catch (Throwable e) {
            // ignored
        }
        if (logger.isDebugEnabled()) {
            if (info != null) {
                logger.debug("processMetricEvent: info = " + info);
            }
            logger.debug("state = " + gatState);
        }
        switch(gatState) {
        case org.gridlab.gat.resources.Job.ON_HOLD:
            setState(State.SUSPENDED);
            setDetail("ON_HOLD");
            break;

        case org.gridlab.gat.resources.Job.POST_STAGING:
            setDetail("POST_STAGING");
            break;

        case org.gridlab.gat.resources.Job.PRE_STAGING:
            setDetail("PRE_STAGING");
            break;

        case org.gridlab.gat.resources.Job.RUNNING:
            if (info != null) {
                Long l = (Long) info.get("starttime");
                String s = (String) info.get("hostname");
                if (l != null) {
                    try {
                        setValue(STARTED, l.toString());
                    } catch (Throwable e) {
                        // ignored
                    }
                }
                if (s != null) {
                    try {
                        setVectorValue(EXECUTIONHOSTS, s.split(" "));
                    } catch (Throwable e) {
                        // ignored
                    }
                }
            }
            setDetail("RUNNING");
            break;
        case org.gridlab.gat.resources.Job.SCHEDULED:
            setDetail("SCHEDULED");
            if (info != null) {
                Long l = (Long) info.get("submissiontime");
                if (l != null) {
                    try {
                        setValue(CREATED, l.toString());
                    } catch (Throwable e) {
                        // ignored
                    }
                }
            }
            
            break;  

        case org.gridlab.gat.resources.Job.STOPPED:
            setDetail("STOPPED");
            if (state == State.RUNNING) {
                setState(State.DONE);
                notifyAll();
            }
            if (info != null) {
                Long l = (Long) info.get("stoptime");
                if (l != null) {
                    try {
                        setValue(FINISHED, l.toString());
                    } catch (Throwable e) {
                        // ignored
                    }
                }
            }
            try {
                int n = gatJob.getExitStatus();
                setValue(EXITCODE, "" + n);
            } catch(Throwable e) {
                // ignored
            }
            
            break;

        case org.gridlab.gat.resources.Job.SUBMISSION_ERROR:
            setDetail("SUBMISSION_ERROR");
            setException(new NoSuccessException("Submission error"));
            setState(State.FAILED);
            notifyAll();
            break;
        }
    }

    @Override
    public synchronized boolean waitFor(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
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
        throw new NotImplementedException("checkpoint() not implemented: JavaGAT does not support it");     
    }

    public InputStream getStderr() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException,
            TimeoutException, IncorrectStateException, NoSuccessException {
        throw new NotImplementedException("getStderr is not implemented, "
                + "javaGAT does not support interactive jobs");
    }

    public OutputStream getStdin() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException,
            TimeoutException, IncorrectStateException, NoSuccessException {
        throw new NotImplementedException("getStdin is not implemented, "
                + "javaGAT does not support interactive jobs");
    }

    public InputStream getStdout() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException, DoesNotExistException,
            TimeoutException, IncorrectStateException, NoSuccessException {       
        throw new NotImplementedException("getStdout is not implemented, "
                + "javaGAT does not support interactive jobs");
    }

    public void migrate(org.ogf.saga.job.JobDescription jd) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("migrate() not implemented: JavaGAT does not support it");
    }

    public void resume() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        if (state != State.SUSPENDED) {
            throw new IncorrectStateException("resume() called when job state was " + state);
        }
        try {
            gatJob.resume();
        } catch (GATInvocationException e) {
            throw new NoSuccessException("resume failed", e);
        }
        setState(State.RUNNING);
    }

    public void signal(int signum) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("signal() not implemented: javaGAT does not support this");
        
    }

    public void suspend() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        if (state != State.RUNNING) {
            throw new IncorrectStateException("suspend() called when job state was " + state);
        }
        try {
            gatJob.hold();
        } catch (GATInvocationException e) {
            throw new NoSuccessException("resume failed", e);
        }
        setState(State.SUSPENDED);
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

    @Override
    public Object clone() {
        return new SagaJob(this);
    }

}
