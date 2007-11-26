package org.ogf.saga.proxies.namespace;

import org.ogf.saga.ObjectType;
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
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/**
 * Wrapper class: wraps the NSEntry proxy.
 */
public class NSEntryWrapper extends SagaObjectBase implements NSEntry {
    
    private NSEntryInterface proxy;
    
    protected NSEntryWrapper(Session session, NSEntryInterface proxy) {
        super(session);
        this.proxy = proxy;
    }

    public Object clone() throws CloneNotSupportedException {
        NSEntryWrapper clone = (NSEntryWrapper) super.clone();
        clone.proxy = (NSEntryInterface) proxy.clone();
        return clone();
    }

    public void close() throws NotImplemented, IncorrectState, NoSuccess {
        close(NO_WAIT);
    }

    public void close(float timeoutInSeconds) throws NotImplemented,
            IncorrectState, NoSuccess {
        proxy.close(timeoutInSeconds);
    }

    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplemented {
        return proxy.close(mode, timeoutInSeconds);
    }

    public Task close(TaskMode mode) throws NotImplemented {
        return close(mode, NO_WAIT);
    }

    public Task copy(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return proxy.copy(mode, target, flags);
    }

    public Task copy(TaskMode mode, URL target) throws NotImplemented {
        return copy(mode, target, Flags.NONE.getValue());
    }

    public void copy(URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess, IncorrectURL {
        proxy.copy(target, flags);
    }

    public void copy(URL target) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess,
            IncorrectURL {
        copy(target, Flags.NONE.getValue());
    }

    public URL getCWD() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        return proxy.getCWD();
    }

    public Task<URL> getCWD(TaskMode mode) throws NotImplemented {
        return proxy.getCWD(mode);
    }

    public String getGroup() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getGroup();
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return proxy.getGroup(mode);
    }

    public URL getName() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        return proxy.getName();
    }

    public Task<URL> getName(TaskMode mode) throws NotImplemented {
        return proxy.getName(mode);
    }

    public String getOwner() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getOwner();
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return proxy.getOwner(mode);
    }

    public ObjectType getType() {
        return ObjectType.NSENTRY;
    }

    public URL getURL() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        return proxy.getURL();
    }

    public Task<URL> getURL(TaskMode mode) throws NotImplemented {
        return proxy.getURL(mode);
    }

    public boolean isDir() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess {
        return proxy.isDir();
    }

    public Task<Boolean> isDir(TaskMode mode) throws NotImplemented {
        return proxy.isDir(mode);
    }

    public boolean isEntry() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess {
        return proxy.isEntry();
    }

    public Task<Boolean> isEntry(TaskMode mode) throws NotImplemented {
        return proxy.isEntry(mode);
    }

    public boolean isLink() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess {
        return proxy.isLink();
    }

    public Task<Boolean> isLink(TaskMode mode) throws NotImplemented {
        return proxy.isLink(mode);
    }

    public Task link(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return proxy.link(mode, target, flags);
    }

    public Task link(TaskMode mode, URL target) throws NotImplemented {
        return link(mode, target, Flags.NONE.getValue());
    }

    public void link(URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess,
            IncorrectURL {
        proxy.link(target, flags);
    }

    public void link(URL target) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        link(target, Flags.NONE.getValue());
    }

    public Task move(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return proxy.move(mode, target, flags);
    }

    public Task move(TaskMode mode, URL target) throws NotImplemented {
        return move(mode, target, Flags.NONE.getValue());
    }

    public void move(URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess, IncorrectURL {
        proxy.move(target, flags);
    }

    public void move(URL target) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess,
            IncorrectURL {
        move(target, Flags.NONE.getValue());
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(id, permissions, flags);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(id, permissions);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions,
            int flags) throws NotImplemented {
        return proxy.permissionsAllow(mode, id, permissions, flags);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplemented {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            IncorrectState, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions,
            int flags) throws NotImplemented {
        return proxy.permissionsDeny(mode, id, permissions, flags);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public URL readLink() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess {
        return proxy.readLink();
    }

    public Task<URL> readLink(TaskMode mode) throws NotImplemented {
        return proxy.readLink(mode);
    }

    public void remove() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess {
        remove(Flags.NONE.getValue());
    }

    public void remove(int flags) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess {
        proxy.remove(flags);
    }

    public Task remove(TaskMode mode, int flags) throws NotImplemented {
        return proxy.remove(mode, flags);
    }

    public Task remove(TaskMode mode) throws NotImplemented {
        return remove(mode, Flags.NONE.getValue());
    }

}
