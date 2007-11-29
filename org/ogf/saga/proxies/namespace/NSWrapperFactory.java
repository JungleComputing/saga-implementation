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
import org.ogf.saga.spi.namespace.NSDirectorySpiInterface;
import org.ogf.saga.spi.namespace.NSEntrySpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class NSWrapperFactory extends NSFactory {

    protected NSDirectory doCreateNSDirectory(Session session, URL name,
            int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, AlreadyExists, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        NSDirectorySpiInterface proxy = (NSDirectorySpiInterface) getAdaptorProxy(
                NSDirectorySpiInterface.class,
                new Class[] { org.ogf.saga.impl.session.Session.class, URL.class,
                    Integer.TYPE },
                parameters);
        return new NSDirectoryWrapper(session, proxy);
    }

    protected NSEntry doCreateNSEntry(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter, DoesNotExist,
            AlreadyExists, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        NSEntrySpiInterface proxy = (NSEntrySpiInterface) getAdaptorProxy(
                NSEntrySpiInterface.class,
                new Class[] { org.ogf.saga.impl.session.Session.class, URL.class,
                    Integer.TYPE },
                parameters);
        return new NSEntryWrapper(session, proxy);
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

    protected Task<NSDirectory> doCreateNSDirectory(TaskMode mode,
            Session session, URL name, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<NSDirectory>(this, session, mode,
                "doCreateNSDirectory",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);
    }

    protected Task<NSEntry> doCreateNSEntry(TaskMode mode, Session session,
            URL name, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<NSEntry>(this, session, mode,
                "doCreateNSEntry",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);

    }

}
