package org.ogf.saga.proxies.logicalfile;

import org.ogf.saga.URL;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.logicalfile.LogicalDirectory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class LogicalFileWrapperFactory extends LogicalFileFactory {

    protected LogicalDirectory doCreateLogicalDirectory(Session session,
            URL name, int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, Timeout, NoSuccess {
            return new LogicalDirectoryWrapper(session, name, flags);
    }

    protected LogicalFile doCreateLogicalFile(Session session, URL name,
            int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
            return new LogicalFileWrapper(session, name, flags);
    }
    
    @Override
    protected Task<LogicalDirectory> doCreateLogicalDirectory(TaskMode mode,
            Session session, URL name, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<LogicalDirectory>(this, session, mode,
                "doCreateLogicalDirectory",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);
    }

    @Override
    protected Task<LogicalFile> doCreateLogicalFile(TaskMode mode,
            Session session, URL name, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<LogicalFile>(this, session, mode,
                "doCreateLogicalFile",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);
    }
}
