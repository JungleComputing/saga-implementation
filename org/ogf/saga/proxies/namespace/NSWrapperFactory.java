package org.ogf.saga.proxies.namespace;

import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class NSWrapperFactory extends NSFactory {

    protected NSDirectory doCreateNSDirectory(Session session, URL name,
            int flags) throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, AlreadyExistsException, TimeoutException,
            NoSuccessException {
        return new NSDirectoryWrapper(session, name, flags);
    }

    protected NSEntry doCreateNSEntry(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, AlreadyExistsException, TimeoutException,
            NoSuccessException {
        return new NSEntryWrapper(session, name, flags);
    }

    protected Task<NSFactory, NSDirectory> doCreateNSDirectory(TaskMode mode,
            Session session, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSFactory, NSDirectory>(this,
                session, mode, "doCreateNSDirectory", new Class[] {
                        Session.class, URL.class, Integer.TYPE }, session,
                name, flags);
    }

    protected Task<NSFactory, NSEntry> doCreateNSEntry(TaskMode mode,
            Session session, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSFactory, NSEntry>(this,
                session, mode, "doCreateNSEntry", new Class[] { Session.class,
                        URL.class, Integer.TYPE }, session, name, flags);
    }
}
