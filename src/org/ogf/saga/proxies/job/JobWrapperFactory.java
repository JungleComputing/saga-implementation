package org.ogf.saga.proxies.job;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class JobWrapperFactory extends JobFactory {

    public JobWrapperFactory() {
    }

    protected JobDescription doCreateJobDescription() throws NotImplemented,
            NoSuccess {
        return new JobDescription();
    }

    protected JobService doCreateJobService(Session session, URL rm)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return new JobServiceWrapper(session, rm);
    }

    protected Task<JobService> doCreateJobService(TaskMode mode,
            Session session, URL rm) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<JobService>(this, session, mode,
                "doCreateJobService",
                new Class[] { Session.class, URL.class } ,
                session, rm);
    }
}
