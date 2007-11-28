package org.ogf.saga.spi.namespace;

import org.apache.log4j.Logger;
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
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class NSEntrySpi implements NSEntrySpiInterface {

    private static Logger logger = Logger.getLogger(NSEntrySpi.class);

    protected Session session;
    protected boolean closed = false;
    protected URL nameUrl;

    public NSEntrySpi(Session session, URL name, int flags)
        throws NotImplemented, IncorrectURL, BadParameter, DoesNotExist,
            PermissionDenied, AuthorizationFailed, AuthenticationFailed,
            Timeout, NoSuccess, AlreadyExists {
        this.session = session;
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
            throw new BadParameter(
                    "Flags not allowed for NSEntry constructor: " + flags);
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        NSEntrySpi clone = (NSEntrySpi) super.clone();
        try {
            clone.nameUrl = new URL(nameUrl.toString());
        } catch (Throwable e) {
            throw new SagaError("Should not happen", e);
        }
        return clone;
    }

    protected void checkClosed() throws IncorrectState {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Entry already closed!");
            }
            throw new IncorrectState("NSEntry already closed");
        }
    }

    protected URL resolve(URL url) throws NoSuccess {
        return nameUrl.resolve(url);
    }

    public void close(float timeoutInSeconds) throws NotImplemented,
            IncorrectState, NoSuccess {
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

    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "close", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public URL getCWD() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        checkClosed();
        String path = nameUrl.getPath();
        boolean dir = false;
        try {
            dir = isDir();
        } catch(Throwable e) {
            // ignore?
        }
        if (! dir) {
            int i = path.lastIndexOf('/');
            if (i != -1) {
                path = path.substring(0, i);
            }
        }
        URL newURL = null;
        try {
            newURL = new URL(nameUrl.toString());
            newURL.setPath(path);
        } catch (BadParameter e) {
            throw new NoSuccess("Unexpected error", e);
        }
        return newURL;
    }

    public Task<URL> getCWD(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(this, session,
                mode, "getCWD", new Class[] {});
    }

    public URL getName() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        checkClosed();
        String path = nameUrl.getPath();
        String[] s = path.split("/");

        try {
            return new URL(s[s.length-1]);
        } catch (BadParameter e) {
            throw new NoSuccess("Unexpected error", e);
        }
    }

    public Task<URL> getName(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(this, session,
                mode, "getName", new Class[] {});
    }

    public URL getURL() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        checkClosed();
        try {
            return new URL(nameUrl.normalize().toString());
        } catch (BadParameter e) {
            throw new NoSuccess("Unexpected error", e);
        }
    }

    public Task<URL> getURL(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(this, session, mode,
                "getURL", new Class[] {});
    }

    public void copy(URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess,
            IncorrectURL, DoesNotExist {
        checkClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.RECURSIVE
                .or(Flags.OVERWRITE));
        // allowed flags: RECURSIVE, CREATE_PARENTS, OVERWRITE
        // otherwise: BadParameter();
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameter("Flags not allowed for NSEntry copy: "
                    + flags);
        }
        unresolvedCopy(resolve(target), flags);
    }
    
    // Copy, without resolving target.
    // It has already been resolved, either with respect to this NSEntry or
    // with respect to the NSDirectory object that invoked this method.
    // SPI implementations should override this method.
    protected abstract void unresolvedCopy(URL target, int flags)
            throws IncorrectState, NoSuccess, BadParameter, AlreadyExists,
                IncorrectURL, NotImplemented;

    public Task copy(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "copy", new Class[] { URL.class, Integer.TYPE }, target, flags);
    }

    public Task<Boolean> isDir(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "isDir", new Class[] { });
    }

    public Task<Boolean> isEntry(TaskMode mode)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "isEntry", new Class[] { });
    }

    public Task<Boolean> isLink(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "isLink", new Class[] { });
    }
  
    public Task link(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "link", new Class[] { URL.class, Integer.TYPE }, target, flags);
    }

    public void move(URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess,
            IncorrectURL, DoesNotExist {
        checkClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.RECURSIVE
                .or(Flags.OVERWRITE));
        // allowed flags: RECURSIVE, CREATE_PARENTS, OVERWRITE
        // otherwise: BadParameter();
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameter("Flags not allowed for NSEntry copy: "
                    + flags);
        }
        unresolvedMove(resolve(target), flags);
    }
    
    protected abstract void unresolvedMove(URL target, int flags) throws IncorrectState,
            NoSuccess, BadParameter, AlreadyExists, NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, IncorrectURL;

    public Task move(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "move", new Class[] { URL.class, Integer.TYPE }, target, flags);
    }
    
    public Task permissionsAllow(TaskMode mode, String id, int permissions,
            int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsAllow", new Class[] { String.class, Integer.TYPE,
                        Integer.TYPE }, id, permissions, flags);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions,
            int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsDeny", new Class[] { String.class, Integer.TYPE,
                        Integer.TYPE }, id, permissions, flags);
    }

    public Task<URL> readLink(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(this, session, mode,
                "readLink", new Class[] {});
    }

    public Task remove(TaskMode mode, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(this, session, mode,
                "remove", new Class[] { Integer.TYPE }, flags);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        try {
            permissionsAllow(id, permissions, Flags.NONE.getValue());
        } catch (IncorrectState e) {
            // This method cannot throw this, because it implements the method
            // as specified in org.ogf.saga.permissions.Permissions.
            throw new NoSuccess("Incorrect state", e);
        }
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsAllow", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "permissionsCheck", new Class[] { String.class,
                        Integer.TYPE }, id, permissions);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        try {
            permissionsDeny(id, permissions, Flags.NONE.getValue());
        } catch (IncorrectState e) {
            // This method cannot throw this, because it implements the method
            // as specified in org.ogf.saga.permissions.Permissions.
            throw new NoSuccess("Incorrect state", e);
        }
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsDeny", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(this, session,
                mode, "getGroup", new Class[] {});
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(this, session,
                mode, "getOwner", new Class[] {});
    }
}
