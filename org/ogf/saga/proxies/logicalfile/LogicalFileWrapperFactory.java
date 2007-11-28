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

public abstract class LogicalFileWrapperFactory extends LogicalFileFactory {

    protected LogicalDirectory doCreateLogicalDirectory(Session session,
            URL name, int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        LogicalDirectorySpiInterface proxy = (LogicalDirectorySpiInterface) getAdaptorProxy(
                    "org.ogf.saga.spi.logicalfile.LogicalDirectorySpi",
                    LogicalDirectorySpiInterface.class, parameters);
            return new LogicalDirectoryWrapper(session, proxy);
    }

    protected LogicalFile doCreateLogicalFile(Session session, URL name,
            int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        Object[] parameters = { session, name, flags };
        LogicalFileSpiInterface proxy = (LogicalFileSpiInterface) getAdaptorProxy(
                    "org.ogf.saga.spi.logicalfile.LogicalFileSpi",
                    LogicalFileSpiInterface.class, parameters);
            return new LogicalFileWrapper(session, proxy);
    }
    
    protected static Object getAdaptorProxy(String spiClassName,
            Class<?> interfaceClass, Object[] parameters) {

        try {
            return SAGAEngine.createAdaptorProxy(
                    spiClassName, interfaceClass, parameters);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
