package org.ogf.saga.spi.namespace;

import org.apache.log4j.Logger;
import org.ogf.saga.URL;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class NSEntryAdaptorBase extends AdaptorBase<NSEntryWrapper>
        implements NSEntrySPI {

    private static Logger logger = Logger.getLogger(NSEntryAdaptorBase.class);
    private static final int COPY_FLAGS = Flags.CREATEPARENTS.or(Flags.RECURSIVE
            .or(Flags.OVERWRITE));

    protected boolean closed = false;
    protected URL nameUrl;

    public NSEntryAdaptorBase(NSEntryWrapper wrapper, Session session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(session, wrapper);
        nameUrl = name.normalize();
        if (name == nameUrl) {
            nameUrl = new URL(name.toString());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Creating NSEntrySpi: " + nameUrl);
        }

        int allowedFlags = Flags.CREATEPARENTS.or(Flags.CREATE.or(Flags.EXCL));
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for NSEntry constructor: " + flags);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        NSEntryAdaptorBase clone = (NSEntryAdaptorBase) super.clone();
        try {
            clone.nameUrl = new URL(nameUrl.toString());
        } catch (Throwable e) {
            throw new SagaRuntimeException("Should not happen", e);
        }
        return clone;
    }

    protected void checkNotClosed() throws IncorrectStateException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Entry already closed!");
            }
            throw new IncorrectStateException("NSEntry already closed");
        }
    }

    protected URL resolve(URL url) throws NoSuccessException {
        return nameUrl.resolve(url);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        closed = true;
    }

    protected void finalize() {
        if (!closed) {
            try {
                close(0.0F);
            } catch (Throwable e) {
                // ignored
            }
        }
    }

    public Task<NSEntry, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "close", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public URL getCWD() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        checkNotClosed();
        String path = nameUrl.getPath();
        boolean dir = false;
        try {
            dir = isDir();
        } catch (Throwable e) {
            // ignore?
        }
        if (!dir) {
            int i = path.lastIndexOf('/');
            if (i != -1) {
                path = path.substring(0, i);
            }
        }
        URL newURL = null;
        try {
            newURL = new URL(nameUrl.toString());
            newURL.setPath(path);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e);
        }
        return newURL;
    }

    public Task<NSEntry, URL> getCWD(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, URL>(wrapper, session,
                mode, "getCWD", new Class[] {});
    }

    public URL getName() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        checkNotClosed();
        String path = nameUrl.getPath();
        String[] s = path.split("/");

        try {
            return new URL(s[s.length - 1]);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e);
        }
    }

    public Task<NSEntry, URL> getName(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, URL>(wrapper, session,
                mode, "getName", new Class[] {});
    }

    public URL getURL() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        checkNotClosed();
        try {
            return new URL(nameUrl.normalize().toString());
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e);
        }
    }

    public Task<NSEntry, URL> getURL(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, URL>(wrapper, session,
                mode, "getURL", new Class[] {});
    }
    
    protected void checkCopyFlags(int flags) throws BadParameterException {
        if ((COPY_FLAGS | flags) != COPY_FLAGS) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for NSEntry copy: " + flags);
        }
    }

    public Task<NSEntry, Void> copy(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "copy", new Class[] { URL.class, Integer.TYPE }, target,
                flags);
    }

    public Task<NSEntry, Boolean> isDir(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Boolean>(wrapper,
                session, mode, "isDir", new Class[] {});
    }

    public Task<NSEntry, Boolean> isEntry(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Boolean>(wrapper,
                session, mode, "isEntry", new Class[] {});
    }

    public Task<NSEntry, Boolean> isLink(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Boolean>(wrapper,
                session, mode, "isLink", new Class[] {});
    }

    public Task<NSEntry, Void> link(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "link", new Class[] { URL.class, Integer.TYPE }, target,
                flags);
    }

    public Task<NSEntry, Void> move(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "move", new Class[] { URL.class, Integer.TYPE }, target,
                flags);
    }

    public Task<NSEntry, Void> permissionsAllow(TaskMode mode, String id,
            int permissions, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "permissionsAllow", new Class[] { String.class,
                        Integer.TYPE, Integer.TYPE }, id, permissions, flags);
    }

    public Task<NSEntry, Void> permissionsDeny(TaskMode mode, String id,
            int permissions, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "permissionsDeny", new Class[] { String.class,
                        Integer.TYPE, Integer.TYPE }, id, permissions, flags);
    }

    public Task<NSEntry, URL> readLink(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, URL>(wrapper, session,
                mode, "readLink", new Class[] {});
    }

    public Task<NSEntry, Void> remove(TaskMode mode, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "remove", new Class[] { Integer.TYPE }, flags);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        try {
            permissionsAllow(id, permissions, Flags.NONE.getValue());
        } catch (IncorrectStateException e) {
            // This method cannot throw this, because it implements the method
            // as specified in org.ogf.saga.permissions.Permissions.
            throw new NoSuccessException("Incorrect state", e);
        }
    }

    public Task<NSEntry, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "permissionsAllow", new Class[] { String.class,
                        Integer.TYPE }, id, permissions);
    }

    public Task<NSEntry, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Boolean>(wrapper,
                session, mode, "permissionsCheck", new Class[] { String.class,
                        Integer.TYPE }, id, permissions);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        try {
            permissionsDeny(id, permissions, Flags.NONE.getValue());
        } catch (IncorrectStateException e) {
            // This method cannot throw this, because it implements the method
            // as specified in org.ogf.saga.permissions.Permissions.
            throw new NoSuccessException("Incorrect state", e);
        }
    }

    public Task<NSEntry, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, Void>(wrapper, session,
                mode, "permissionsDeny", new Class[] { String.class,
                        Integer.TYPE }, id, permissions);
    }

    public Task<NSEntry, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, String>(wrapper,
                session, mode, "getGroup", new Class[] {});
    }

    public Task<NSEntry, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<NSEntry, String>(wrapper,
                session, mode, "getOwner", new Class[] {});
    }
}
