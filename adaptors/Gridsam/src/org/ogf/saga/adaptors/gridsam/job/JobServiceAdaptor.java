package org.ogf.saga.adaptors.gridsam.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.icenigrid.gridsam.client.common.ClientSideJobManager;
import org.icenigrid.gridsam.core.ConfigurationException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobSelf;
import org.ogf.saga.proxies.job.JobServiceWrapper;
import org.ogf.saga.spi.job.JobServiceAdaptorBase;
import org.ogf.saga.url.URL;

public class JobServiceAdaptor extends JobServiceAdaptorBase {

    private HashMap<String, SagaJob> jobs = new HashMap<String, SagaJob>();

    ClientSideJobManager jobManager;

    String url;

    public JobServiceAdaptor(JobServiceWrapper wrapper, SessionImpl sessionImpl, URL rm)
            throws NoSuccessException, NotImplementedException, IncorrectURLException {
        super(wrapper, sessionImpl, rm);
        String scheme = rm.getScheme();

        if (scheme.equals("any") || scheme.equals("gridsam")) {
            scheme = "https";
            try {
                this.rm.setScheme(scheme);
            } catch (Exception e) {
                throw new NoSuccessException("Should not happen", e, wrapper);
            }
        }
        if ("http".equals(scheme) || "https".equals(scheme)) {
            // this is OK.
        } else {
            throw new IncorrectURLException(
                    "Wrong scheme for gridsam adaptor", wrapper);
        }
        url = rm.toString();
        try {
            jobManager = new ClientSideJobManager(new String[] { "-s", url },
                    ClientSideJobManager.getStandardOptions());
        } catch (ConfigurationException e) {
            throw new NoSuccessException("Could not create job service", e, wrapper);
        }
    }

    public Job createJob(JobDescription jd) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        SagaJob job = new SagaJob(this,
                (org.ogf.saga.impl.job.JobDescriptionImpl) jd, sessionImpl);
        return job;
    }

    synchronized void addJob(SagaJob job, String id) {
        jobs.put(id, job);
    }

    public synchronized Job getJob(String jobId)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        Job job = jobs.get(jobId);
        if (job == null) {
            throw new DoesNotExistException("Job " + jobId + " does not exist", wrapper);
        }
        return job;
    }

    public JobSelf getSelf() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        // TODO Implement this!
        throw new NotImplementedException("getSelf", wrapper);
    }

    public List<String> list() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return new ArrayList<String>(jobs.keySet());
    }
}
