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
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.Session;
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

    public JobServiceAdaptor(JobServiceWrapper wrapper, Session session, URL rm)
            throws NoSuccessException, NotImplementedException {
        super(wrapper, session, rm);
        String scheme;
        try {
            scheme = rm.getScheme();
        } catch (NotImplementedException e) {
            throw new NoSuccessException("Should not happen", e);
        }
        if (scheme.equals("any") || scheme.equals("gridsam")) {
            scheme = "https";
            try {
                this.rm.setScheme(scheme);
            } catch (Exception e) {
                throw new NoSuccessException("Should not happen", e);
            }
        }
        if ("http".equals(scheme) || "https".equals(scheme)) {
            // this is OK.
        } else {
            throw new NotImplementedException(
                    "Wrong scheme for gridsam adaptor");
        }
        url = rm.toString();
        try {
            jobManager = new ClientSideJobManager(new String[] { "-s", url },
                    ClientSideJobManager.getStandardOptions());
        } catch (ConfigurationException e) {
            throw new NoSuccessException("Could not create job service", e);
        }
    }

    public Job createJob(JobDescription jd) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        SagaJob job = new SagaJob(this,
                (org.ogf.saga.impl.job.JobDescription) jd, session);
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
            throw new DoesNotExistException("Job " + jobId + " does not exist");
        }
        return job;
    }

    public JobSelf getSelf() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        // TODO Implement this!
        throw new NotImplementedException("getSelf");
    }

    public List<String> list() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return new ArrayList<String>(jobs.keySet());
    }
}
