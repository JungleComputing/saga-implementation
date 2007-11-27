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
import org.ogf.saga.spi.namespace.NSDirectoryInterface;
import org.ogf.saga.spi.namespace.NSEntryInterface;

public abstract class NSWrapperFactory extends NSFactory {

    protected NSDirectory doCreateNSDirectory(Session session, URL name,
            int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, AlreadyExists, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        NSDirectoryInterface proxy = (NSDirectoryInterface) getAdaptorProxy(
                "org.ogf.saga.spi.namespace.NSDirectorySpi",
                NSDirectoryInterface.class, parameters);
        return new NSDirectoryWrapper(session, proxy);
    }

    protected NSEntry doCreateNSEntry(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter, DoesNotExist,
            AlreadyExists, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        NSEntryInterface proxy = (NSEntryInterface) getAdaptorProxy(
                "org.ogf.saga.spi.namespace.NSEntrySpi",
                NSEntryInterface.class, parameters);
        return new NSEntryWrapper(session, proxy);
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
