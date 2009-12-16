package org.ogf.saga.proxies.stream;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.session.Session;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamServer;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class StreamWrapperFactory extends StreamFactory {

    @Override
    public Stream doCreateStream(Session session, URL name)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return new StreamWrapper(session, name);
    }

    @Override
    protected Task<StreamFactory, Stream> doCreateStream(TaskMode mode,
            Session session, URL name) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamFactory, Stream>(this,
                session, mode, "doCreateStream", new Class[] { Session.class,
                        URL.class }, session, name);
    }

    @Override
    public StreamServer doCreateStreamServer(Session session, URL name)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return new StreamServerWrapper(session, name);
    }


    @Override
    protected Task<StreamFactory, StreamServer> doCreateStreamServer(
            TaskMode mode, Session session, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamFactory, StreamServer>(
                this, session, mode, "doCreateStreamService", new Class[] {
                        Session.class, URL.class }, session, name);
    }
}
