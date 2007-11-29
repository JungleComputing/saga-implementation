package org.ogf.saga.proxies.logicalfile;

import org.ogf.saga.URL;
import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.spi.logicalfile.LogicalDirectorySpiInterface;
import org.ogf.saga.spi.logicalfile.LogicalFileSpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class LogicalFileWrapperFactory extends LogicalFileFactory {

    protected LogicalDirectory doCreateLogicalDirectory(Session session,
            URL name, int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        LogicalDirectorySpiInterface proxy = (LogicalDirectorySpiInterface) getAdaptorProxy(
                    LogicalDirectorySpiInterface.class,
                    new Class[] { org.ogf.saga.impl.session.Session.class, URL.class,
                        Integer.TYPE },
                    parameters);
            return new LogicalDirectoryWrapper(session, proxy);
    }

    protected LogicalFile doCreateLogicalFile(Session session, URL name,
            int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        Object[] parameters = { session, name, flags };
        LogicalFileSpiInterface proxy = (LogicalFileSpiInterface) getAdaptorProxy(
                    LogicalFileSpiInterface.class,
                    new Class[] { org.ogf.saga.impl.session.Session.class, URL.class,
                        Integer.TYPE }, 
                    parameters);
            return new LogicalFileWrapper(session, proxy);
    }
    
    protected static Object getAdaptorProxy(
            Class<?> interfaceClass, Class[] types, Object[] parameters) {

        try {
            return SAGAEngine.createAdaptorProxy(
                    interfaceClass, types, parameters);
        } catch (Exception e) {
            throw new Error(e);
        }
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
