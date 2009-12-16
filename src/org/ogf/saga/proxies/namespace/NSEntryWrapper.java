package org.ogf.saga.proxies.namespace;

import java.io.IOException;

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
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.impl.url.URLUtil;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.namespace.NSEntrySPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

/**
 * Wrapper class: wraps the NSEntry proxy.
 */
public class NSEntryWrapper extends SagaObjectBase implements NSEntry {
    
    private static final int COPY_FLAGS =
        Flags.CREATEPARENTS.or(Flags.RECURSIVE.or(Flags.OVERWRITE));
    private static final int REMOVE_FLAGS =
        Flags.DEREFERENCE.or(Flags.RECURSIVE);

    private NSEntrySPI proxy;
    private boolean inheritedProxy = false;
    private URL url;
    private boolean closed = false;
    private final boolean isDirectory;
    
    protected int includeImpliedFlags(int flags) {
        if (Flags.CREATEPARENTS.isSet(flags)) {
            flags = Flags.CREATE.or(flags);
        }
        if (Flags.APPEND.isSet(flags)) {
            flags = Flags.WRITE.or(flags);
        }
        if (Flags.TRUNCATE.isSet(flags)) {
            flags = Flags.WRITE.or(flags);
        }
        if (Flags.CREATE.isSet(flags)) {
            flags = Flags.WRITE.or(flags);
        }
        return flags;
    }
    
    public URL getWrapperURL() {
        return url;
    }
    
    public void setWrapperURL(URL url) {
        this.url = url;
    }
    
    public boolean isClosed() {
        return closed;
    }
    
    public void setClosed(boolean closed) {
        this.closed = closed;
    }
    
    protected void finalize() {
        if (! isClosed()) {
            try {
                close(0.0F);
            } catch (Throwable e) {
                // ignored
            }
        }
    }

    protected NSEntryWrapper(Session session, URL name, boolean isDir)
            throws BadParameterException, NoSuccessException, NotImplementedException {
        super(session);
        
        url = name.normalize();
        String path = url.getPath();

        if (name == url) {
            url = URLFactory.createURL(name.toString());
        }
        
        if (! path.equals("/") && path.endsWith("/")) {
            if (this instanceof NSDirectoryWrapper) {
                url.setPath(path.substring(0, path.length() - 1));
            } else {
                throw new BadParameterException("Bad parameter: " + name + " cannot indicate a non-directory");
            }
        }
        this.isDirectory = isDir;
    }

    protected NSEntryWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, AlreadyExistsException, TimeoutException,
            NoSuccessException {
        
        this(session, name, false);
        
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.CREATE.or(Flags.EXCL));
        if ((allowedFlags | flags) != allowedFlags) {
            throw new BadParameterException( 
                    "Flags not allowed for NSEntry constructor: " + flags);
        }

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
    
    protected void checkNotClosed() throws IncorrectStateException {
        if (isClosed()) {
            throw new IncorrectStateException("Entry already closed", this);
        }
    }
    
    protected void checkCopyFlags(int flags) throws BadParameterException {
        if ((COPY_FLAGS | flags) != COPY_FLAGS) {
            throw new BadParameterException(
                    "Flags not allowed: " + flags, this);
        }
    }
    
    protected void checkRemoveFlags(int flags)
    throws BadParameterException {
        if ((REMOVE_FLAGS | flags) != REMOVE_FLAGS) {
            throw new BadParameterException(
                    "Flags not allowed: " + flags, this);
        }
    }
    
    protected void checkDirectoryFlags(int flags, boolean isDir)
    throws BadParameterException {
        if (isDir && !Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException(
                    "'Recursive' flag is not set for directory", this);
        }
        if (!isDir && Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException(
                    "'Recursive' flag is set for non-directory", this);
        }
    }

    protected void setProxy(NSEntrySPI proxy) {
        this.proxy = proxy;
        inheritedProxy = true;
    }

    public Object clone() throws CloneNotSupportedException {
        NSEntryWrapper clone = (NSEntryWrapper) super.clone();
        clone.url = (URL) url.clone();
        if (!inheritedProxy) {
            // subclasses should call setProxy again.
            clone.proxy = (NSEntrySPI) SAGAEngine.createAdaptorCopy(
                    NSEntrySPI.class, proxy, clone);
        }
        return clone;
    }

    public void close() throws NotImplementedException, NoSuccessException {
        close(NO_WAIT);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        if (! isClosed()) {
            proxy.close(timeoutInSeconds);
        }
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
    
    boolean checkURL(URL url) {
        String scheme = url.getScheme();
        if (("any".equals(scheme) || "file".equals(scheme))
            && URLUtil.refersToLocalHost(url)) {
            return false;
        }
        return true;
    }
    
    private boolean viaLocalCopy(URL target, int flags) {
        NSEntryWrapper temp = null;
        if (! checkURL(url)) {
            return false;
        }
        try {
            target = url.resolve(target);        
            if (checkURL(target)) {
                java.io.File tmp = null;
                // use a local tmp file. Create it, and then delete it, so that
                // we have a unique name.
                try {
                    tmp = java.io.File.createTempFile("SagaTmp", ".tmp");
                } catch(IOException e) {
                    throw new NoSuccessException("Could not create temporary", e);
                }
                tmp.delete();
                URL url = URLFactory.createURL(tmp.toURI().toString());                              
                // Copy through the proxy, to prevent recursion here.
                proxy.copy(url, flags);

                temp = isDirectory ? 
                        new NSDirectoryWrapper(sessionImpl, url, 0) :
                            new NSEntryWrapper(sessionImpl, url, 0);
                temp.proxy.copy(target, flags);
                // Or wrap exceptions coming from this?
            }
        } catch(Throwable e) {
            return false;
        } finally {
            try {
                temp.remove(isDirectory ? Flags.RECURSIVE.getValue() : 0);
            } catch(Throwable ex) {
                // ignored
            }
        }
        return true;
    }

    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        checkNotClosed();
        checkCopyFlags(flags);
        checkDirectoryFlags(flags, isDirectory);
        
        try {
            proxy.copy(target, flags);
        } catch(SagaException e) {
            // None of the adaptors can apparently do this,
            // Last resort: try to copy via a local file/directory.
            if (viaLocalCopy(target, flags)) {
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
        checkNotClosed();
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
        checkNotClosed();
        return proxy.getName();
    }

    public Task<NSEntry, URL> getName(TaskMode mode)
            throws NotImplementedException {
        return proxy.getName(mode);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        // checkNotClosed();
        // Cannot throw IncorrectState!
        return proxy.getOwner();
    }

    public Task<NSEntry, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return proxy.getOwner(mode);
    }

    public URL getURL() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        checkNotClosed();
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
        checkNotClosed();
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
        checkNotClosed();
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
        checkNotClosed();
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
        checkNotClosed();
        checkCopyFlags(flags);
        checkDirectoryFlags(flags, isDirectory);
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
        checkNotClosed();
        checkCopyFlags(flags);
        checkDirectoryFlags(flags, isDirectory);
        try {
            proxy.move(target, flags);
        } catch(SagaException e) {
            // Try to copy via local, and then remove ...
            if (viaLocalCopy(target, flags)) {
                remove(flags);
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
        checkNotClosed();
        checkRemoveFlags(flags);
        checkDirectoryFlags(flags, isDirectory);
        proxy.permissionsAllow(id, permissions, flags);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        // checkNotClosed();
        // Cannot throw IncorrectState!
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
        // checkNotClosed();
        // Cannot throw IncorrectState!
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
        checkNotClosed();
        checkRemoveFlags(flags);
        checkDirectoryFlags(flags, isDirectory);
        proxy.permissionsDeny(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        // checkNotClosed();
        // Cannot throw IncorrectState!
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
        checkNotClosed();
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
        checkNotClosed();
        checkRemoveFlags(flags);
        checkDirectoryFlags(flags, isDirectory);
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
