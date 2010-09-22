package org.ogf.saga.proxies.namespace;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.namespace.NSDirectorySPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class NSDirectoryWrapper extends NSEntryWrapper implements NSDirectory {

    private NSDirectorySPI proxy;
    private boolean inheritedProxy = false;

    protected NSDirectoryWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, AlreadyExistsException, TimeoutException,
            NoSuccessException {
        super(session, name, true);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (NSDirectorySPI) SAGAEngine.createAdaptorProxy(
                    NSDirectorySPI.class, new Class[] {
                            NSDirectoryWrapper.class,
                            org.ogf.saga.impl.session.SessionImpl.class,
                            URL.class, Integer.TYPE }, parameters);
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

    protected NSDirectoryWrapper(Session session, URL name) throws BadParameterException, NoSuccessException, NotImplementedException {
        super(session, name, true);
    }

    protected void setProxy(NSDirectorySPI proxy) {
        this.proxy = proxy;
        super.setProxy(proxy);
        inheritedProxy = true;
    }
    
    @Override
    protected void checkNotClosed() throws IncorrectStateException {
        if (isClosed()) {
            throw new IncorrectStateException("NSDirectory already closed", this);
        }
    }
    
    protected void checkDirCopyFlags(int flags) throws IncorrectStateException,
    BadParameterException {
        checkNotClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.RECURSIVE
                .or(Flags.OVERWRITE));
        if ((allowedFlags | flags) != allowedFlags) {
            throw new BadParameterException(
                    "Flags not allowed for copy method: " + flags, this);
        }
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
        checkNotClosed();
        if (dir.isAbsolute()) {
            
            URL url = dir.normalize();
            String path = url.getPath();

            if (dir == url) {
                url = URLFactory.createURL(dir.toString());
            }
            
            if (! path.equals("/") && path.endsWith("/")) {
                url.setPath(path.substring(0, path.length() - 1));
            }
 
            Object[] parameters = { this, getSession(), url, 0};
            
            try {
                proxy = (NSDirectorySPI) SAGAEngine.createAdaptorProxy(
                        NSDirectorySPI.class, new Class[] {
                                NSDirectoryWrapper.class,
                                org.ogf.saga.impl.session.SessionImpl.class,
                                URL.class, Integer.TYPE }, parameters);
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
                if (e instanceof DoesNotExistException) {
                    throw (DoesNotExistException) e;
                }
                if (e instanceof TimeoutException) {
                    throw (TimeoutException) e;
                }
                if (e instanceof NoSuccessException) {
                    throw (NoSuccessException) e;
                }
                throw new NoSuccessException("chdir", e, this);
            }
            super.setProxy(proxy);
            setWrapperURL(url);            
        } else {
            proxy.changeDir(dir);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        NSDirectoryWrapper clone = (NSDirectoryWrapper) super.clone();
        if (!inheritedProxy) {
            clone.proxy = (NSDirectorySPI) SAGAEngine.createAdaptorCopy(
                    NSDirectorySPI.class, proxy, clone);
        }
        return clone;
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, URL source, URL target,
            int flags) throws NotImplementedException {
        return proxy.copy(mode, source, target, flags);
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, URL source, URL target)
            throws NotImplementedException {
        return copy(mode, source, target, Flags.NONE.getValue());
    }
    
    private boolean viaLocalCopy(URL source, URL target, int flags) {        
        java.io.File tmp = null;
        NSDirectoryWrapper temp = null;
        
        try {
            URL resolvedSource = getWrapperURL().resolve(source);
            target = getWrapperURL().resolve(target);

            if (! checkURL(target) || ! checkURL(resolvedSource)) {
                return false;
            }
            try {
                // use a local tmp file. Create it, and then delete it, so that
                // we have a unique name.
                tmp = java.io.File.createTempFile("SagaTmp", ".tmp");
            } catch(IOException e) {
                return false;
            }
            tmp.delete();
            if (! tmp.mkdir()) {
                return false;
            }
            URL url = URLFactory.createURL(tmp.toURI().toString());                              
            // copy through the proxy to prevent recursion here.
            proxy.copy(source, url, flags);

            temp = new NSDirectoryWrapper(sessionImpl, url, 0);
            java.io.File tmp1 = new java.io.File(source.getPath());
            temp.proxy.copy(URLFactory.createURL(tmp1.getName()), target, flags);
            return true;
        } catch(Throwable e) {
            return false;
        } finally {
            try {
                if (temp != null) {
                    temp.remove(Flags.RECURSIVE.getValue());
                }
            } catch(Throwable ex) {
                // ignored
            }
            if (tmp != null) {
                tmp.delete();
            }
        }
    }

    
    private boolean viaLocalCopy(String source, URL target, int flags) {        
        java.io.File tmp = null;
        NSDirectoryWrapper temp = null;
        
        try {
            target = getWrapperURL().resolve(target);

            if (! checkURL(target)) {
                return false;
            }
            try {
                // use a local tmp file. Create it, and then delete it, so that
                // we have a unique name.
                tmp = java.io.File.createTempFile("SagaTmp", ".tmp");
            } catch(IOException e) {
                return false;
            }
            tmp.delete();
            if (! tmp.mkdir()) {
                return false;
            }
            URL url = URLFactory.createURL(tmp.toURI().toString());                              
            // Copy through the proxy, to prevent recursion.
            proxy.copy(source, url, flags);
            temp = new NSDirectoryWrapper(sessionImpl, url, 0);
            temp.proxy.copy("*", target, flags);
            return true;
        } catch(Throwable e) {
            return false;
        } finally {
            try {
                if (temp != null) {
                    temp.remove(Flags.RECURSIVE.getValue());
                }
            } catch(Throwable ex) {
                // ignored
            }
            if (tmp != null) {
                tmp.delete();
            }
        }
    }

    public void copy(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        checkNotClosed();
        checkCopyFlags(flags);
        // checkDirectoryFlags(flags, isDir(source));
        try {
            proxy.copy(source, target, flags);
        } catch(SagaException e) {
            // None of the adaptors can apparently do this,
            // Last resort: try to copy via a local file/directory.
            if (viaLocalCopy(source, target, flags)) {
                // success!
                return;
            }
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
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
            if (e instanceof IncorrectStateException) {
                throw (IncorrectStateException) e;
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
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            // If we get here, something is wrong ...
            throw new NoSuccessException("INTERNAL ERROR: wrong exception from copy", e);
        }
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
        checkNotClosed();
        return proxy.exists(name);
    }

    public List<URL> find(String pattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        checkNotClosed();
        int allowedFlags = Flags.DEREFERENCE.or(Flags.RECURSIVE);
        if ((allowedFlags | flags) != allowedFlags) {
            throw new BadParameterException(
                    "Flags not allowed for find method: " + flags, this);
        }
        return proxy.find(pattern, flags);
    }

    public List<URL> find(String pattern) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return find(pattern, Flags.RECURSIVE.getValue());
    }

    public Task<NSDirectory, List<URL>> find(TaskMode mode, String pattern,
            int flags) throws NotImplementedException {
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
        checkNotClosed();
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
        checkNotClosed();
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
        checkNotClosed();
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
        checkNotClosed();
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
        checkNotClosed();
        return proxy.isLink(name);
    }

    public Task<NSDirectory, Void> link(TaskMode mode, URL source, URL target,
            int flags) throws NotImplementedException {
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
        checkNotClosed();
        checkCopyFlags(flags);
        // checkDirectoryFlags(flags, isDir(source));
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
        checkNotClosed();
        int allowedFlags = Flags.DEREFERENCE.getValue();
        if ((allowedFlags | flags) != allowedFlags) {
            throw new BadParameterException(
                    "Flags not allowed for list method: " + flags, this);
        }
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

    public Task<NSDirectory, List<URL>> list(TaskMode mode, String pattern,
            int flags) throws NotImplementedException {
        return proxy.list(mode, pattern, flags);
    }

    public Task<NSDirectory, List<URL>> list(TaskMode mode, String pattern)
            throws NotImplementedException {
        return list(mode, pattern, Flags.NONE.getValue());
    }

    public Task<NSDirectory, List<URL>> list(TaskMode mode)
            throws NotImplementedException {
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
        checkNotClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.EXCL);
        if ((allowedFlags | flags) != allowedFlags) {
            throw new BadParameterException(
                    "Flags not allowed for makeDir method: " + flags, this);
        }
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

    public Task<NSDirectory, Void> move(TaskMode mode, URL source, URL target,
            int flags) throws NotImplementedException {
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
        checkNotClosed();
        checkCopyFlags(flags);
        // checkDirectoryFlags(flags, isDir(source));
        try {
            proxy.move(source, target, flags);
        } catch(SagaException e) {
            // Try to copy via local, and then remove ...
            if (viaLocalCopy(source, target, flags)) {
                remove(source, flags);
                return;
            }
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
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
            if (e instanceof IncorrectStateException) {
                throw (IncorrectStateException) e;
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
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            // If we get here, something is wrong ...
            throw new NoSuccessException("INTERNAL ERROR: wrong exception from move", e);
        }
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
        return open(mode, name, Flags.READ.getValue());
    }

    public NSEntry open(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        checkNotClosed();
        name = resolveToDir(name);
        return NSFactory.createNSEntry(sessionImpl, name, flags);
    }

    public NSEntry open(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return open(name, Flags.READ.getValue());
    }
    
    /**
     * Resolves the specified URL with respect to the current directory.
     */
    protected URL resolveToDir(URL url) throws NotImplementedException,
            NoSuccessException, BadParameterException {
        if (url.isAbsolute()) {
            return url;
        }
        String path = url.getPath();
        URL myURL = getWrapperURL();
        if (path.startsWith("/")) {
            // Relative URL, absolute path. Resolve.
            URL u = myURL.resolve(url);
            return u;
        }

        URL u = URLFactory.createURL(myURL.toString());
        path = u.getPath();

        // If there is no path, and the URL has a host part, the path
        // should start with a '/'.
        if (path.equals("")) {
            if (myURL.getHost() == null) {
                path = ".";
            }
        }
        u.setPath(path + "/DUMMY");
        u = u.resolve(url);

        return u;
    }


    public Task<NSDirectory, NSDirectory> openDir(TaskMode mode, URL name,
            int flags) throws NotImplementedException {
        return proxy.openDir(mode, name, flags);
    }

    public Task<NSDirectory, NSDirectory> openDir(TaskMode mode, URL name)
            throws NotImplementedException {
        return openDir(mode, name, Flags.READ.getValue());
    }

    public NSDirectory openDir(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        checkNotClosed();
        name = resolveToDir(name);
        return NSFactory.createNSDirectory(sessionImpl, name, flags);
    }

    public NSDirectory openDir(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return openDir(name, Flags.READ.getValue());
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, URL target,
            String id, int permissions, int flags)
            throws NotImplementedException {
        return proxy.permissionsAllow(mode, target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, URL target,
            String id, int permissions) throws NotImplementedException {
        return permissionsAllow(mode, target, id, permissions, Flags.NONE
                .getValue());
    }

    public void permissionsAllow(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        checkNotClosed();
        proxy.permissionsAllow(target, id, permissions, flags);
    }

    public void permissionsAllow(URL target, String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        permissionsAllow(target, id, permissions, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, URL target,
            String id, int permissions, int flags)
            throws NotImplementedException {
        return proxy.permissionsDeny(mode, target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, URL target,
            String id, int permissions) throws NotImplementedException {
        return permissionsDeny(mode, target, id, permissions, Flags.NONE
                .getValue());
    }

    public void permissionsDeny(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException, IncorrectStateException {
        checkNotClosed();
        proxy.permissionsDeny(target, id, permissions, flags);
    }

    public void permissionsDeny(URL target, String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException, IncorrectStateException {
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
        checkNotClosed();
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
        checkNotClosed();
        checkRemoveFlags(flags);
        // checkDirectoryFlags(flags, isDir(target));
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
        checkNotClosed();
        checkCopyFlags(flags);
        try {
            proxy.copy(source, target, flags);
        } catch(SagaException e) {
            // None of the adaptors can apparently do this,
            // Last resort: try to copy via a local file/directory.
            if (viaLocalCopy(source, target, flags)) {
                // success!
                return;
            }
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
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
            if (e instanceof IncorrectStateException) {
                throw (IncorrectStateException) e;
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
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            // If we get here, something is wrong ...
            throw new NoSuccessException("INTERNAL ERROR: wrong exception from copy", e);
        }
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, String source,
            URL target, int flags) throws NotImplementedException {
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

    public Task<NSDirectory, Void> link(TaskMode mode, String source,
            URL target, int flags) throws NotImplementedException {
        return proxy.link(mode, source, target, flags);
    }

    public void move(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        checkNotClosed();
        checkCopyFlags(flags);
        try {
            proxy.move(source, target, flags);
        } catch(SagaException e) {
            // Try to copy via local, and then remove ...
            if (viaLocalCopy(source, target, flags)) {
                remove(source, flags);
                return;
            }
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
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
            if (e instanceof IncorrectStateException) {
                throw (IncorrectStateException) e;
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
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            // If we get here, something is wrong ...
            throw new NoSuccessException("INTERNAL ERROR: wrong exception from move", e);
        }
    }

    public Task<NSDirectory, Void> move(TaskMode mode, String source,
            URL target, int flags) throws NotImplementedException {
        return proxy.move(mode, source, target, flags);
    }

    public void permissionsAllow(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        checkNotClosed();
        proxy.permissionsAllow(target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode,
            String target, String id, int permissions, int flags)
            throws NotImplementedException {
        return proxy.permissionsAllow(mode, target, id, permissions, flags);
    }

    public void permissionsDeny(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException, IncorrectStateException {
        checkNotClosed();
        proxy.permissionsDeny(target, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode,
            String target, String id, int permissions, int flags)
            throws NotImplementedException {
        return proxy.permissionsDeny(mode, target, id, permissions, flags);
    }

    public void remove(String target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        checkNotClosed();
        checkRemoveFlags(flags);
        proxy.remove(target, flags);
    }

    public Task<NSDirectory, Void> remove(TaskMode mode, String target,
            int flags) throws NotImplementedException {
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
            NoSuccessException, IncorrectURLException {
        permissionsAllow(target, id, permissions, Flags.NONE.getValue());
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode,
            String target, String id, int permissions)
            throws NotImplementedException {
        return permissionsAllow(mode, target, id, permissions, Flags.NONE
                .getValue());

    }

    public void permissionsDeny(String target, String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException, IncorrectStateException {
        permissionsDeny(target, id, permissions, Flags.NONE.getValue());

    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode,
            String target, String id, int permissions)
            throws NotImplementedException {
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

    public long getMTime(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.getMTime(name);
    }

    public Task<NSDirectory, Long> getMTime(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.getMTime(mode, name);
    }

}
