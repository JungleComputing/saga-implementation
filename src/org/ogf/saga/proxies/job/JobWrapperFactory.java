package org.ogf.saga.proxies.job;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.job.JobDescriptionImpl;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class JobWrapperFactory extends JobFactory {

    public JobWrapperFactory() {
    }

    protected JobDescriptionImpl doCreateJobDescription()
            throws NotImplementedException, NoSuccessException {
        return new JobDescriptionImpl();
    }

    public JobService doCreateJobService(Session session, URL rm)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException,
            BadParameterException {
        return new JobServiceWrapper(session, rm);
    }

    protected Task<JobFactory, JobService> doCreateJobService(TaskMode mode,
            Session session, URL rm) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<JobFactory, JobService>(
                this, session, mode, "doCreateJobService", new Class[] {
                        Session.class, URL.class }, session, rm);
    }
}
