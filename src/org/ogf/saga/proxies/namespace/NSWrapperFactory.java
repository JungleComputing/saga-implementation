package org.ogf.saga.proxies.namespace;

import org.ogf.saga.URL;
import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;

public abstract class NSWrapperFactory extends NSFactory {

    protected NSDirectory doCreateNSDirectory(Session session, URL name,
            int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, AlreadyExists, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        NSDirectory proxy = (NSDirectory) getAdaptorProxy(
                "org.ogf.saga.spi.io.NSDirectoryCpi",
                NSDirectory.class, parameters);
        return new NSDirectoryWrapper(proxy);
    }

    protected NSEntry doCreateNSEntry(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter, DoesNotExist,
            AlreadyExists, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        NSEntry proxy = (NSEntry) getAdaptorProxy(
                "org.ogf.saga.spi.io.NSEntryCpi",
                NSEntry.class, parameters);
        return new NSEntryWrapper(proxy);
    }
    
    protected static Object getAdaptorProxy(String cpiClassName,
            Class<?> interfaceClass, Object[] parameters) {

        try {
            return SAGAEngine.createAdaptorProxy(
                    cpiClassName, interfaceClass, parameters);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
