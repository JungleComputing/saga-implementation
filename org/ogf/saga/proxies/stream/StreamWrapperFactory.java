package org.ogf.saga.proxies.stream;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.session.Session;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamService;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class StreamWrapperFactory extends StreamFactory {

    @Override
    protected Stream doCreateStream(Session session, URL name)
            throws NotImplemented, IncorrectURL, BadParameter,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return new StreamWrapper(session, name);
    }

    @Override
    protected Task<Stream> doCreateStream(TaskMode mode, Session session,
            URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Stream>(this, session, mode,
                "doCreateStream", new Class[] {Session.class, URL.class},
                session, name);
    }

    @Override
    protected StreamService doCreateStreamService(Session session, URL name)
            throws NotImplemented, IncorrectURL, BadParameter,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return new StreamServiceWrapper(session, name);
    }

    @Override
    protected StreamService doCreateStreamService(Session session)
            throws NotImplemented, IncorrectURL, BadParameter,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        URL name;
        try {
            name = new URL("");
        } catch(Throwable e) {
            throw new SagaError("Should not happen!", e);
        }
        return doCreateStreamService(session, name);
    }

    @Override
    protected Task<StreamService> doCreateStreamService(TaskMode mode,
            Session session, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<StreamService>(this, session, mode,
                "doCreateStreamService", new Class[] {Session.class, URL.class},
                session, name);
    }

    @Override
    protected Task<StreamService> doCreateStreamService(TaskMode mode,
            Session session) throws NotImplemented {
        URL name;
        try {
            name = new URL("");
        } catch(Throwable e) {
            throw new SagaError("Should not happen!", e);
        }
        return doCreateStreamService(mode, session, name);      
    }
}
