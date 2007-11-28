package org.ogf.saga.proxies.namespace;

import java.util.List;

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
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.namespace.NSDirectorySpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class NSDirectoryWrapper extends NSEntryWrapper implements NSDirectory {
    
    private NSDirectorySpiInterface proxy;
    
    protected NSDirectoryWrapper(Session session, NSDirectorySpiInterface proxy) {
        super(session, proxy);
        this.proxy = proxy;
    }

    public Task changeDir(TaskMode mode, URL dir) throws NotImplemented {
        return proxy.changeDir(mode, dir);
    }

    public void changeDir(URL dir) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        proxy.changeDir(dir);
    }

    public Object clone() throws CloneNotSupportedException {
        NSDirectoryWrapper clone = (NSDirectoryWrapper) super.clone();
        clone.proxy = (NSDirectorySpiInterface) proxy.clone();
        return clone();
    }

    public Task copy(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return proxy.copy(mode, target, flags);
    }

    public Task copy(TaskMode mode, URL source, URL target, int flags)
            throws NotImplemented {
        return proxy.copy(mode, source, target, flags);
    }

    public Task copy(TaskMode mode, URL source, URL target)
            throws NotImplemented {
        return copy(mode, source, target, Flags.NONE.getValue());
    }

    public void copy(URL source, URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectURL, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        proxy.copy(source, target, flags);
    }

    public void copy(URL source, URL target) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectURL, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        copy(source, target, Flags.NONE.getValue());
    }

    public Task<Boolean> exists(TaskMode mode, URL name) throws NotImplemented {
        return proxy.exists(mode, name);
    }

    public boolean exists(URL name) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.exists(name);
    }

    public List<URL> find(String pattern, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.find(pattern, flags);
    }

    public List<URL> find(String pattern) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess {
        return find(pattern, Flags.RECURSIVE.getValue());
    }

    public Task<List<URL>> find(TaskMode mode, String pattern, int flags)
            throws NotImplemented {
        return proxy.find(mode, pattern, flags);
    }

    public Task<List<URL>> find(TaskMode mode, String pattern)
            throws NotImplemented {
        return find(mode, pattern, Flags.RECURSIVE.getValue());
    }

    public URL getEntry(int entry) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, IncorrectState,
            DoesNotExist, Timeout, NoSuccess {
        return proxy.getEntry(entry);
    }

    public Task<URL> getEntry(TaskMode mode, int entry) throws NotImplemented {
        return proxy.getEntry(mode, entry);
    }

    public int getNumEntries() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, IncorrectState, Timeout,
            NoSuccess {
        return proxy.getNumEntries();
    }

    public Task<Integer> getNumEntries(TaskMode mode) throws NotImplemented {
        return proxy.getNumEntries(mode);
    }

    public ObjectType getType() {
        return ObjectType.NSDIRECTORY;
    }

    public Task<Boolean> isDir(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isDir(mode, name);
    }

    public boolean isDir(URL name) throws NotImplemented, IncorrectURL,
            DoesNotExist, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isDir(name);
    }

    public Task<Boolean> isEntry(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isEntry(mode, name);
    }

    public boolean isEntry(URL name) throws NotImplemented, IncorrectURL,
            DoesNotExist, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isEntry(name);
    }

    public Task<Boolean> isLink(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isLink(mode, name);
    }

    public boolean isLink(URL name) throws NotImplemented, IncorrectURL,
            DoesNotExist, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isLink(name);
    }

    public Task link(TaskMode mode, URL source, URL target, int flags)
            throws NotImplemented {
        return proxy.link(mode, source, target, flags);
    }

    public Task link(TaskMode mode, URL source, URL target)
            throws NotImplemented {
        return link(mode, source, target, Flags.NONE.getValue());
    }

    public void link(URL source, URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectURL, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        proxy.link(source, target, flags);
    }

    public void link(URL source, URL target) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectURL, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        link(source, target, Flags.NONE.getValue());
    }

    public List<URL> list() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return list(Flags.NONE.getValue());
    }

    public List<URL> list(int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return list("", flags);
    }

    public List<URL> list(String pattern, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return proxy.list(pattern, flags);
    }

    public List<URL> list(String pattern) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return list(pattern, Flags.NONE.getValue());
    }

    public Task<List<URL>> list(TaskMode mode, int flags) throws NotImplemented {
        return list(mode, "", flags);
    }

    public Task<List<URL>> list(TaskMode mode, String pattern, int flags)
            throws NotImplemented {
        return proxy.list(mode, pattern, flags);
    }

    public Task<List<URL>> list(TaskMode mode, String pattern)
            throws NotImplemented {
        return list(mode, pattern, Flags.NONE.getValue());
    }

    public Task<List<URL>> list(TaskMode mode) throws NotImplemented {
        return list(mode, Flags.NONE.getValue());
    }

    public Task makeDir(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return proxy.makeDir(mode, target, flags);
    }

    public Task makeDir(TaskMode mode, URL target) throws NotImplemented {
        return makeDir(mode, target, Flags.NONE.getValue());
    }

    public void makeDir(URL target, int flags) throws NotImplemented,
            IncorrectURL, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        proxy.makeDir(target, flags);
    }

    public void makeDir(URL target) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        makeDir(target, Flags.NONE.getValue());
    }

    public Task move(TaskMode mode, URL source, URL target, int flags)
            throws NotImplemented {
        return proxy.move(mode, source, target, flags);
    }

    public Task move(TaskMode mode, URL source, URL target)
            throws NotImplemented {
        return move(mode, source, target, Flags.NONE.getValue());
    }

    public void move(URL source, URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectURL, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        proxy.move(source, target, flags);
    }

    public void move(URL source, URL target) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectURL, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        move(source, target, Flags.NONE.getValue());
    }

    public Task<NSEntry> open(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return proxy.open(mode, name, flags);
    }

    public Task<NSEntry> open(TaskMode mode, URL name) throws NotImplemented {
        return open(mode, name, Flags.NONE.getValue());
    }

    public NSEntry open(URL name, int flags) throws NotImplemented,
            IncorrectURL, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        return proxy.open(name, flags);
    }

    public NSEntry open(URL name) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        return open(name, Flags.NONE.getValue());
    }

    public Task<NSDirectory> openDir(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return proxy.openDir(mode, name, flags);
    }

    public Task<NSDirectory> openDir(TaskMode mode, URL name) throws NotImplemented {
        return openDir(mode, name, Flags.NONE.getValue());
    }

    public NSDirectory openDir(URL name, int flags) throws NotImplemented,
            IncorrectURL, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        return proxy.openDir(name, flags);
    }

    public NSDirectory openDir(URL name) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        return openDir(name, Flags.NONE.getValue());
    }

    public Task permissionsAllow(TaskMode mode, URL target, String id,
            int permissions, int flags) throws NotImplemented {
        return proxy.permissionsAllow(mode, target, id, permissions, flags);
    }

    public Task permissionsAllow(TaskMode mode, URL target, String id,
            int permissions) throws NotImplemented {
        return permissionsAllow(mode, target, id, permissions, Flags.NONE.getValue());
    }

    public void permissionsAllow(URL target, String id, int permissions,
            int flags) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, IncorrectState,
            BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(target, id, permissions, flags);
    }

    public void permissionsAllow(URL target, String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, Timeout, NoSuccess {
        permissionsAllow(target, id, permissions, Flags.NONE.getValue());
    }

    public Task permissionsDeny(TaskMode mode, URL target, String id,
            int permissions, int flags) throws NotImplemented {
        return proxy.permissionsDeny(mode, target, id, permissions, flags);
    }

    public Task permissionsDeny(TaskMode mode, URL target, String id,
            int permissions) throws NotImplemented {
        return permissionsDeny(mode, target, id, permissions, Flags.NONE.getValue());
    }

    public void permissionsDeny(URL target, String id, int permissions,
            int flags) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter, Timeout,
            NoSuccess {
        proxy.permissionsDeny(target, id, permissions, flags);
    }

    public void permissionsDeny(URL target, String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        permissionsDeny(target, id, permissions, Flags.NONE.getValue());
    }

    public Task<URL> readLink(TaskMode mode, URL name) throws NotImplemented {
        return proxy.readLink(mode, name);
    }

    public URL readLink(URL name) throws NotImplemented, IncorrectURL,
            DoesNotExist, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.readLink(name);
    }

    public Task remove(TaskMode mode, URL target, int flags)
            throws NotImplemented {
        return proxy.remove(mode, target, flags);
    }

    public Task remove(TaskMode mode, URL target) throws NotImplemented {
        return remove(mode, target, Flags.NONE.getValue());
    }

    public void remove(URL target, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectURL, BadParameter, IncorrectState, DoesNotExist, Timeout,
            NoSuccess {
        proxy.remove(target, flags);
    }

    public void remove(URL target) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        remove(target, Flags.NONE.getValue());
    }

}
