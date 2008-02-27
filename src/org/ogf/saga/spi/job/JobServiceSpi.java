package org.ogf.saga.spi.job;

import java.util.ArrayList;
import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobSelf;
import org.ogf.saga.proxies.job.JobServiceWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class JobServiceSpi extends AdaptorBase implements JobServiceSpiInterface {

    protected URL rm;
    
    public JobServiceSpi(JobServiceWrapper wrapper, Session session, URL rm) {
        super(session, wrapper);
        this.rm = rm;
    }
    
    List<String> getCommandLineElements(String commandLine) {
        ArrayList<String> elts = new ArrayList<String>();
        StringBuffer elt = new StringBuffer();
        boolean escaped = false;
        boolean inString = false;
        boolean sawBytes = false;
        
        // From the Saga specification:
        // - Elements are delimited by white space, which is either a space or a tab.
        // - A string surrounded by double quotation marks is interpreted as a
        //   single element, regardless of white space contained within.
        //   A quoted string can be embedded in an element.
        // - A double quotation mark preceded by a backslash (\) is interpreted as a
        //   literal double quotation mark (").
        // - Backslashes are interpreted literally, unless they immediately precede
        //   a double quotation mark.
        
        for (char c : commandLine.toCharArray()) {
            switch(c) {
            case ' ':
            case '\t':
                if (escaped) {
                    elt.append('\\');
                    escaped = false;
                }
                if (inString) {
                    elt.append(c);
                } else {
                    if (sawBytes) {
                        elts.add(elt.toString());
                        elt = new StringBuffer();
                        sawBytes = false;
                    }
                }
                break;
            case '"':
                sawBytes = true;
                if (escaped) {
                    elt.append(c);
                    escaped = false;
                } else {
                    inString = ! inString;
                }
                break;
            case '\\':
                if (escaped) {
                    elt.append('\\');
                }
                sawBytes = true;
                escaped = true;
                break;
            default:
                if (escaped) {
                    elt.append('\\');
                }
                sawBytes = true;
                escaped = false;
                elt.append(c);
                break;
            }
        }
        
        // Now, elt may have no elements but still represent an argument,
        // for instance when it had "". In this case, sawBytes is true.
        if (sawBytes) {
            if (escaped) {
                elt.append('\\');
            }
            elts.add(elt.toString());
            // Note: no check on inString???
        }
        return elts;
    }
    
    public Job runJob(String commandLine, String host, boolean interactive) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        JobDescription jd = JobFactory.createJobDescription();
        List<String> elts = getCommandLineElements(commandLine);
        if (elts.size() == 0) {
            throw new BadParameterException("Empty command line");
        }
        try {
            jd.setAttribute(JobDescription.EXECUTABLE, elts.remove(0));
            jd.setVectorAttribute(JobDescription.ARGUMENTS, elts.toArray(new String[elts.size()]));
            if (host != null && ! "".equals(host)) {
                jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS, new String[] {host});
            }
        } catch (Throwable e) {
            throw new SagaRuntimeException("Should not happen", e);
        }
        Job job = createJob(jd);
        try {
            job.run();
        } catch (IncorrectStateException e) {
            throw new SagaRuntimeException("Internal error", e);
        }
        return job;
    }
   
    // No dedicated clone() method needed. The session field does not have to
    // be cloned, and the rm field is immutable.

    public Task<Job> createJob(TaskMode mode, JobDescription jd)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Job>(wrapper, session, mode,
                "createJob", new Class[] { JobDescription.class }, jd);
    }
    
    public Task<Job> runJob(TaskMode mode, String commandLine, String host, boolean interactive)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Job>(wrapper, session, mode,
                "runJob", new Class[] { String.class, String.class, Boolean.TYPE },
                commandLine, host, interactive);
    }
    
    public Task<Job> getJob(TaskMode mode, String jobId) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Job>(wrapper, session, mode,
                "getJob", new Class[] { String.class }, jobId);
    }

    public Task<JobSelf> getSelf(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<JobSelf>(wrapper, session, mode,
                "getSelf", new Class[] { });
    }

    public Task<List<String>> list(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<List<String>>(wrapper, session, mode,
                "list", new Class[] { } );
    }

}
