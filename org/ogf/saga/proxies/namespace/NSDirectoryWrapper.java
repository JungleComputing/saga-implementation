package org.ogf.saga.proxies.namespace;

import java.util.Iterator;
import java.util.List;

import org.ogf.saga.URL;
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
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.namespace.NSDirectorySpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class NSDirectoryWrapper extends NSEntryWrapper implements NSDirectory {

    private NSDirectorySpiInterface proxy;
    private boolean inheritedProxy = false;

    protected NSDirectoryWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, AlreadyExistsException, TimeoutException,
            NoSuccessException {
        super(session);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (NSDirectorySpiInterface) SAGAEngine.createAdaptorProxy(
                    NSDirectorySpiInterface.class, new Class[] {
                            NSDirectoryWrapper.class,
                            org.ogf.saga.impl.session.Session.class, URL.class,
                            Integer.TYPE }, parameters);
            super.setProxy(proxy);
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

    protected NSDirectoryWrapper(Session session) {
        super(session);
    }

    protected void setProxy(NSDirectorySpiInterface proxy) {
        this.proxy = proxy;
        super.setProxy(proxy);
        inheritedProxy = true;
    }

    public Task<NSDirectory, Void> changeDir(TaskMode mode, URL dir)
            throws NotImplementedException {
        return proxy.changeDir(mode, dir);
    }

    public void changeDir(URL dir) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.changeDir(dir);
    }

    public Object clone() throws CloneNotSupportedException {
        NSDirectoryWrapper clone = (NSDirectoryWrapper) super.clone();
        if (!inheritedProxy) {
            clone.proxy = (NSDirectorySpiInterface) SAGAEngine
                    .createAdaptorCopy(NSDirectorySpiInterface.class, proxy,
                            clone);
        }
        return clone;
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, URL source, URL target, int flags)
            throws NotImplementedException {
        return proxy.copy(mode, source, target, flags);
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, URL source, URL target)
            throws NotImplementedException {
        return copy(mode, source, target, Flags.NONE.getValue());
    }

    public void copy(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.copy(source, target, flags);
    }

    public void copy(URL source, URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        copy(source, target, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Boolean> exists(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.exists(mode, name);
    }

    public boolean exists(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return proxy.exists(name);
    }

    public List<URL> find(String pattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return proxy.find(pattern, flags);
    }

    public List<URL> find(String pattern) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return find(pattern, Flags.RECURSIVE.getValue());
    }

    public Task<NSDirectory, List<URL>> find(TaskMode mode, String pattern, int flags)
            throws NotImplementedException {
        return proxy.find(mode, pattern, flags);
    }

    public Task<NSDirectory, List<URL>> find(TaskMode mode, String pattern)
            throws NotImplementedException {
        return find(mode, pattern, Flags.RECURSIVE.getValue());
    }

    public URL getEntry(int entry) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.getEntry(entry);
    }

    public Task<NSDirectory, URL> getEntry(TaskMode mode, int entry)
            throws NotImplementedException {
        return proxy.getEntry(mode, entry);
    }

    public int getNumEntries() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.getNumEntries();
    }

    public Task<NSDirectory, Integer> getNumEntries(TaskMode mode)
            throws NotImplementedException {
        return proxy.getNumEntries(mode);
    }

    public Task<NSDirectory, Boolean> isDir(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.isDir(mode, name);
    }

    public boolean isDir(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.isDir(name);
    }

    public Task<NSDirectory, Boolean> isEntry(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.isEntry(mode, name);
    }

    public boolean isEntry(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.isEntry(name);
    }

    public Task<NSDirectory, Boolean> isLink(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.isLink(mode, name);
    }

    public boolean isLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.isLink(name);
    }

    public Task<NSDirectory, Void> link(TaskMode mode, URL source, URL target, int flags)
            throws NotImplementedException {
        return proxy.link(mode, source, target, flags);
    }

    public Task<NSDirectory, Void> link(TaskMode mode, URL source, URL target)
            throws NotImplementedException {
        return link(mode, source, target, Flags.NONE.getValue());
    }

    public void link(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.link(source, target, flags);
    }

    public void link(URL source, URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        link(source, target, Flags.NONE.getValue());
    }

    public List<URL> list() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        return list(Flags.NONE.getValue());
    }

    public List<URL> list(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        return list("", flags);
    }

    public List<URL> list(String pattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        return proxy.list(pattern, flags);
    }

    public List<URL> list(String pattern) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        return list(pattern, Flags.NONE.getValue());
    }

    public Task<NSDirectory, List<URL>> list(TaskMode mode, int flags)
            throws NotImplementedException {
        return list(mode, "", flags);
    }

    public Task<NSDirectory, List<URL>> list(TaskMode mode, String pattern, int flags)
            throws NotImplementedException {
        return proxy.list(mode, pattern, flags);
    }

    public Task<NSDirectory, List<URL>> list(TaskMode mode, String pattern)
            throws NotImplementedException {
        return list(mode, pattern, Flags.NONE.getValue());
    }

    public Task<NSDirectory, List<URL>> list(TaskMode mode) throws NotImplementedException {
        return list(mode, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> makeDir(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return proxy.makeDir(mode, target, flags);
    }

    public Task<NSDirectory, Void> makeDir(TaskMode mode, URL target)
            throws NotImplementedException {
        return makeDir(mode, target, Flags.NONE.getValue());
    }

    public void makeDir(URL target, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        proxy.makeDir(target, flags);
    }

    public void makeDir(URL target) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        makeDir(target, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> move(TaskMode mode, URL source, URL target, int flags)
            throws NotImplementedException {
        return proxy.move(mode, source, target, flags);
    }

    public Task<NSDirectory, Void> move(TaskMode mode, URL source, URL target)
            throws NotImplementedException {
        return move(mode, source, target, Flags.NONE.getValue());
    }

    public void move(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.move(source, target, flags);
    }

    public void move(URL source, URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        move(source, target, Flags.NONE.getValue());
    }

    public Task<NSDirectory, NSEntry> open(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return proxy.open(mode, name, flags);
    }

    public Task<NSDirectory, NSEntry> open(TaskMode mode, URL name)
            throws NotImplementedException {
        return open(mode, name, Flags.NONE.getValue());
    }

    public NSEntry open(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return proxy.open(name, flags);
    }

    public NSEntry open(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return open(name, Flags.NONE.getValue());
    }

    public Task<NSDirectory, NSDirectory> openDir(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return proxy.openDir(mode, name, flags);
    }

    public Task<NSDirectory, NSDirectory> openDir(TaskMode mode, URL name)
            throws NotImplementedException {
        return openDir(mode, name, Flags.NONE.getValue());
    }

    public NSDirectory openDir(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.openDir(name, flags);
    }

    public NSDirectory openDir(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return openDir(name, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, URL target, String id,
            int permissions, int flags) throws NotImplementedException {
        return proxy.permissionsAllow(mode, target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, URL target, String id,
            int permissions) throws NotImplementedException {
        return permissionsAllow(mode, target, id, permissions, Flags.NONE
                .getValue());
    }

    public void permissionsAllow(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsAllow(target, id, permissions, flags);
    }

    public void permissionsAllow(URL target, String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        permissionsAllow(target, id, permissions, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, URL target, String id,
            int permissions, int flags) throws NotImplementedException {
        return proxy.permissionsDeny(mode, target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, URL target, String id,
            int permissions) throws NotImplementedException {
        return permissionsDeny(mode, target, id, permissions, Flags.NONE
                .getValue());
    }

    public void permissionsDeny(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        proxy.permissionsDeny(target, id, permissions, flags);
    }

    public void permissionsDeny(URL target, String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        permissionsDeny(target, id, permissions, Flags.NONE.getValue());
    }

    public Task<NSDirectory, URL> readLink(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.readLink(mode, name);
    }

    public URL readLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.readLink(name);
    }

    public Task<NSDirectory, Void> remove(TaskMode mode, URL target, int flags)
            throws NotImplementedException {
        return proxy.remove(mode, target, flags);
    }

    public Task<NSDirectory, Void> remove(TaskMode mode, URL target)
            throws NotImplementedException {
        return remove(mode, target, Flags.NONE.getValue());
    }

    public void remove(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.remove(target, flags);
    }

    public void remove(URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        remove(target, Flags.NONE.getValue());
    }

    public void copy(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.copy(source, target, flags);
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, String source, URL target, int flags)
            throws NotImplementedException {
        return proxy.copy(mode, source, target, flags);
    }

    public void link(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.link(source, target, flags);
    }

    public Task<NSDirectory, Void> link(TaskMode mode, String source, URL target, int flags)
            throws NotImplementedException {
        return proxy.link(mode, source, target, flags);
    }

    public void move(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.move(source, target, flags);
    }

    public Task<NSDirectory, Void> move(TaskMode mode, String source, URL target, int flags)
            throws NotImplementedException {
        return proxy.move(mode, source, target, flags);
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

    public void permissionsAllow(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsAllow(target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, String target, String id,
            int permissions, int flags) throws NotImplementedException {
        return proxy.permissionsAllow(mode, target, id, permissions, flags);
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

    public void permissionsDeny(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        proxy.permissionsDeny(target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, String target, String id,
            int permissions, int flags) throws NotImplementedException {
        return proxy.permissionsDeny(mode, target, id, permissions, flags);
    }

    public void remove(String target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        proxy.remove(target, flags);
    }

    public Task<NSDirectory, Void> remove(TaskMode mode, String target, int flags)
            throws NotImplementedException {
        return proxy.remove(mode, target, flags);
    }

    public void copy(String source, URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        copy(source, target, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, String source, URL target)
            throws NotImplementedException {
        return copy(mode, source, target, Flags.NONE.getValue());
    }

    public void link(String source, URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        link(source, target, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> link(TaskMode mode, String source, URL target)
            throws NotImplementedException {
        return link(mode, source, target, Flags.NONE.getValue());
    }

    public void move(String source, URL target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        move(source, target, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> move(TaskMode mode, String source, URL target)
            throws NotImplementedException {
        return move(mode, source, target, Flags.NONE.getValue());
    }

    public void permissionsAllow(String target, String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        permissionsAllow(target, id, permissions, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, String target, String id,
            int permissions) throws NotImplementedException {
        return permissionsAllow(mode, target, id, permissions, Flags.NONE
                .getValue());

    }

    public void permissionsDeny(String target, String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        permissionsDeny(target, id, permissions, Flags.NONE.getValue());

    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, String target, String id,
            int permissions) throws NotImplementedException {
        return permissionsDeny(mode, target, id, permissions, Flags.NONE
                .getValue());
    }

    public void remove(String target) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        remove(target, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> remove(TaskMode mode, String target)
            throws NotImplementedException {
        return remove(mode, target, Flags.NONE.getValue());
    }

    public Iterator<URL> iterator() {
        return proxy.iterator();
    }

}
