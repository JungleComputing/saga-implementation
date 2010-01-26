package org.ogf.saga.spi.namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public abstract class NSEntryAdaptorBase extends AdaptorBase<NSEntryWrapper>
        implements NSEntrySPI {

    private static Logger logger = LoggerFactory
            .getLogger(NSEntryAdaptorBase.class);
    
    private boolean closed = false;
    
    private URL nameURL;

    public NSEntryAdaptorBase(NSEntryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {

        super(sessionImpl, wrapper);
        
        if (wrapper == null) {
            nameURL = name;
        } else {
            nameURL = wrapper.getWrapperURL();
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("Creating NSEntrySpi: " + nameURL);
        }

    }
      
    protected URL getEntryURL() {
        if (wrapper == null) {
            return nameURL;
        }
        return wrapper.getWrapperURL();
    }
    
    protected void setEntryURL(URL url) {
        nameURL = url;
        if (wrapper != null) {
            wrapper.setWrapperURL(url);
        }
    }
    
    protected boolean isClosed() {
        return closed;
    }
    
    
    protected void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Object clone() throws CloneNotSupportedException {
        NSEntryAdaptorBase clone = (NSEntryAdaptorBase) super.clone();
        return clone;
    }

    protected URL resolve(URL url) throws NoSuccessException {
        return getEntryURL().resolve(url);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        setClosed(true);
    }

    public Task<NSEntry, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "close", new Class[] { Float.TYPE },
                timeoutInSeconds);
    }

    public URL getCWD() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        URL url = getEntryURL();
        String path = url.getPath();
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
            newURL = URLFactory.createURL(url.toString());
            newURL.setPath(path);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e);
        }
        return newURL;
    }

    public Task<NSEntry, URL> getCWD(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, URL>(wrapper,
                sessionImpl, mode, "getCWD", new Class[] {});
    }

    public URL getName() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        String path = getEntryURL().getPath();
        String[] s = path.split("/");

        try {
            return URLFactory.createURL(s[s.length - 1]);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e);
        }
    }

    public Task<NSEntry, URL> getName(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, URL>(wrapper,
                sessionImpl, mode, "getName", new Class[] {});
    }

    public URL getURL() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        try {
            return URLFactory.createURL(getEntryURL().normalize().toString());
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e);
        }
    }

    public Task<NSEntry, URL> getURL(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, URL>(wrapper,
                sessionImpl, mode, "getURL", new Class[] {});
    }

    public Task<NSEntry, Void> copy(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "copy", new Class[] { URL.class,
                        Integer.TYPE }, target, flags);
    }

    public Task<NSEntry, Boolean> isDir(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Boolean>(wrapper,
                sessionImpl, mode, "isDir", new Class[] {});
    }

    public Task<NSEntry, Boolean> isEntry(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Boolean>(wrapper,
                sessionImpl, mode, "isEntry", new Class[] {});
    }

    public Task<NSEntry, Boolean> isLink(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Boolean>(wrapper,
                sessionImpl, mode, "isLink", new Class[] {});
    }

    public Task<NSEntry, Void> link(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "link", new Class[] { URL.class,
                        Integer.TYPE }, target, flags);
    }

    public Task<NSEntry, Void> move(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "move", new Class[] { URL.class,
                        Integer.TYPE }, target, flags);
    }

    public Task<NSEntry, Void> permissionsAllow(TaskMode mode, String id,
            int permissions, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "permissionsAllow", new Class[] {
                        String.class, Integer.TYPE, Integer.TYPE }, id,
                permissions, flags);
    }

    public Task<NSEntry, Void> permissionsDeny(TaskMode mode, String id,
            int permissions, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "permissionsDeny", new Class[] {
                        String.class, Integer.TYPE, Integer.TYPE }, id,
                permissions, flags);
    }

    public Task<NSEntry, URL> readLink(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, URL>(wrapper,
                sessionImpl, mode, "readLink", new Class[] {});
    }

    public Task<NSEntry, Void> remove(TaskMode mode, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "remove", new Class[] { Integer.TYPE },
                flags);
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
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "permissionsAllow", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<NSEntry, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Boolean>(wrapper,
                sessionImpl, mode, "permissionsCheck", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
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
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, Void>(wrapper,
                sessionImpl, mode, "permissionsDeny", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<NSEntry, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, String>(wrapper,
                sessionImpl, mode, "getGroup", new Class[] {});
    }

    public Task<NSEntry, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSEntry, String>(wrapper,
                sessionImpl, mode, "getOwner", new Class[] {});
    }
}
