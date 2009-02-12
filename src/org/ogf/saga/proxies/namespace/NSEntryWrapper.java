package org.ogf.saga.proxies.namespace;

import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.namespace.NSEntrySPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

/**
 * Wrapper class: wraps the NSEntry proxy.
 */
public class NSEntryWrapper extends SagaObjectBase implements NSEntry {

    private NSEntrySPI proxy;
    private boolean inheritedProxy = false;

    protected NSEntryWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, AlreadyExistsException, TimeoutException,
            NoSuccessException {
        super(session);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (NSEntrySPI) SAGAEngine.createAdaptorProxy(
                    NSEntrySPI.class, new Class[] { NSEntryWrapper.class,
                            org.ogf.saga.impl.session.SessionImpl.class,
                            URL.class, Integer.TYPE }, parameters);
        } catch (org.ogf.saga.error.SagaException e) {
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
            }
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            if (e instanceof AuthenticationFailedException) {
                throw (AuthenticationFailedException) e;
            }
            if (e instanceof AuthorizationFailedException) {
                throw (AuthorizationFailedException) e;
            }
            if (e instanceof PermissionDeniedException) {
                throw (PermissionDeniedException) e;
            }
            if (e instanceof BadParameterException) {
                throw (BadParameterException) e;
            }
            if (e instanceof AlreadyExistsException) {
                throw (AlreadyExistsException) e;
            }
            if (e instanceof DoesNotExistException) {
                throw (DoesNotExistException) e;
            }
            if (e instanceof TimeoutException) {
                throw (TimeoutException) e;
            }
            if (e instanceof NoSuccessException) {
                throw (NoSuccessException) e;
            }
            throw new NoSuccessException("Constructor failed", e);
        }
    }

    protected NSEntryWrapper(Session session) {
        super(session);
    }

    protected void setProxy(NSEntrySPI proxy) {
        this.proxy = proxy;
        inheritedProxy = true;
    }

    public Object clone() throws CloneNotSupportedException {
        NSEntryWrapper clone = (NSEntryWrapper) super.clone();
        if (!inheritedProxy) {
            // subclasses should call setProxy again.
            clone.proxy = (NSEntrySPI) SAGAEngine.createAdaptorCopy(
                    NSEntrySPI.class, proxy, clone);
        }
        return clone;
    }

    public void close() throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        close(NO_WAIT);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        proxy.close(timeoutInSeconds);
    }

    public Task<NSEntry, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return proxy.close(mode, timeoutInSeconds);
    }

    public Task<NSEntry, Void> close(TaskMode mode)
            throws NotImplementedException {
        return close(mode, NO_WAIT);
    }

    public Task<NSEntry, Void> copy(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return proxy.copy(mode, target, flags);
    }

    public Task<NSEntry, Void> copy(TaskMode mode, URL target)
            throws NotImplementedException {
        return copy(mode, target, Flags.NONE.getValue());
    }

    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        proxy.copy(target, flags);
    }

    public void copy(URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        copy(target, Flags.NONE.getValue());
    }

    public URL getCWD() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.getCWD();
    }

    public Task<NSEntry, URL> getCWD(TaskMode mode)
            throws NotImplementedException {
        return proxy.getCWD(mode);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getGroup();
    }

    public Task<NSEntry, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return proxy.getGroup(mode);
    }

    public URL getName() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.getName();
    }

    public Task<NSEntry, URL> getName(TaskMode mode)
            throws NotImplementedException {
        return proxy.getName(mode);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getOwner();
    }

    public Task<NSEntry, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return proxy.getOwner(mode);
    }

    public URL getURL() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.getURL();
    }

    public Task<NSEntry, URL> getURL(TaskMode mode)
            throws NotImplementedException {
        return proxy.getURL(mode);
    }

    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return proxy.isDir();
    }

    public Task<NSEntry, Boolean> isDir(TaskMode mode)
            throws NotImplementedException {
        return proxy.isDir(mode);
    }

    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.isEntry();
    }

    public Task<NSEntry, Boolean> isEntry(TaskMode mode)
            throws NotImplementedException {
        return proxy.isEntry(mode);
    }

    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.isLink();
    }

    public Task<NSEntry, Boolean> isLink(TaskMode mode)
            throws NotImplementedException {
        return proxy.isLink(mode);
    }

    public Task<NSEntry, Void> link(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return proxy.link(mode, target, flags);
    }

    public Task<NSEntry, Void> link(TaskMode mode, URL target)
            throws NotImplementedException {
        return link(mode, target, Flags.NONE.getValue());
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        proxy.link(target, flags);
    }

    public void link(URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        link(target, Flags.NONE.getValue());
    }

    public Task<NSEntry, Void> move(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return proxy.move(mode, target, flags);
    }

    public Task<NSEntry, Void> move(TaskMode mode, URL target)
            throws NotImplementedException {
        return move(mode, target, Flags.NONE.getValue());
    }

    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        proxy.move(target, flags);
    }

    public void move(URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        move(target, Flags.NONE.getValue());
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        proxy.permissionsAllow(id, permissions, flags);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsAllow(id, permissions);
    }

    public Task<NSEntry, Void> permissionsAllow(TaskMode mode, String id,
            int permissions, int flags) throws NotImplementedException {
        return proxy.permissionsAllow(mode, id, permissions, flags);
    }

    public Task<NSEntry, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<NSEntry, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        proxy.permissionsDeny(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsDeny(id, permissions);
    }

    public Task<NSEntry, Void> permissionsDeny(TaskMode mode, String id,
            int permissions, int flags) throws NotImplementedException {
        return proxy.permissionsDeny(mode, id, permissions, flags);
    }

    public Task<NSEntry, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.readLink();
    }

    public Task<NSEntry, URL> readLink(TaskMode mode)
            throws NotImplementedException {
        return proxy.readLink(mode);
    }

    public void remove() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        remove(Flags.NONE.getValue());
    }

    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        proxy.remove(flags);
    }

    public Task<NSEntry, Void> remove(TaskMode mode, int flags)
            throws NotImplementedException {
        return proxy.remove(mode, flags);
    }

    public Task<NSEntry, Void> remove(TaskMode mode)
            throws NotImplementedException {
        return remove(mode, Flags.NONE.getValue());
    }

}
