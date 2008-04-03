package org.ogf.saga.proxies.job;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class JobWrapperFactory extends JobFactory {

    public JobWrapperFactory() {
    }

    protected JobDescription doCreateJobDescription()
            throws NotImplementedException, NoSuccessException {
        return new JobDescription();
    }

    protected JobService doCreateJobService(Session session, URL rm)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return new JobServiceWrapper(session, rm);
    }

    protected Task<JobFactory, JobService> doCreateJobService(TaskMode mode,
            Session session, URL rm) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<JobFactory, JobService>(this,
                session, mode, "doCreateJobService", new Class[] {
                        Session.class, URL.class }, session, rm);
    }
}
