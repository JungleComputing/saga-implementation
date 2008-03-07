package org.ogf.saga.proxies.logicalfile;

import org.ogf.saga.URL;
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
import org.ogf.saga.logicalfile.LogicalDirectory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class LogicalFileWrapperFactory extends LogicalFileFactory {

    protected LogicalDirectory doCreateLogicalDirectory(Session session,
            URL name, int flags) throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException, AlreadyExistsException, TimeoutException, NoSuccessException {
            return new LogicalDirectoryWrapper(session, name, flags);
    }

    protected LogicalFile doCreateLogicalFile(Session session, URL name,
            int flags) throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
            return new LogicalFileWrapper(session, name, flags);
    }
    
    @Override
    protected Task<LogicalFileFactory, LogicalDirectory> doCreateLogicalDirectory(TaskMode mode,
            Session session, URL name, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<LogicalFileFactory, LogicalDirectory>(this, session, mode,
                "doCreateLogicalDirectory",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);
    }

    @Override
    protected Task<LogicalFileFactory, LogicalFile> doCreateLogicalFile(TaskMode mode,
            Session session, URL name, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<LogicalFileFactory, LogicalFile>(this, session, mode,
                "doCreateLogicalFile",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);
    }
}
