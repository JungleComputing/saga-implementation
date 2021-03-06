package org.ogf.saga.proxies.logicalfile;

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
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.logicalfile.LogicalDirectory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.logicalfile.LogicalDirectorySPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public final class LogicalDirectoryWrapper extends NSDirectoryWrapper implements
        LogicalDirectory {

    private LogicalDirectorySPI proxy;
    private final int logicalFlags;

    LogicalDirectoryWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        super(session, name);
        checkFlags(flags);
        this.logicalFlags = includeImpliedFlags(flags);
        Object[] parameters = { this, session, name, logicalFlags };
        try {
            proxy = (LogicalDirectorySPI) SAGAEngine.createAdaptorProxy(
                    LogicalDirectorySPI.class, new Class[] {
                            LogicalDirectoryWrapper.class,
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
    
    private void checkFlags(int flags) throws BadParameterException {
        int allowed = Flags.ALLLOGICALFILEFLAGS.getValue();
        if ((flags | allowed) != allowed) {
            throw new BadParameterException(
                    "Illegal flags for logical directory: " + flags);
        }
    }

    
    public void changeDir(URL dir) throws NotImplementedException,
    IncorrectURLException, AuthenticationFailedException,
    AuthorizationFailedException, PermissionDeniedException,
    BadParameterException, IncorrectStateException,
    DoesNotExistException, TimeoutException, NoSuccessException {
	checkURLType(dir);
        checkNotClosed();
        if (dir.isAbsolute()) {

            URL url = dir.normalize();
            String path = url.getPath();

            if (dir == url) {
                url = URLFactory.createURL(MY_FACTORY, dir.toString());
            }

            if (! path.equals("/") && path.endsWith("/")) {
                url.setPath(path.substring(0, path.length() - 1));
            }

            setWrapperURL(url);

            Object[] parameters = { this, getSession(), url, 0};

            try {
                proxy = (LogicalDirectorySPI) SAGAEngine.createAdaptorProxy(
                        LogicalDirectorySPI.class, new Class[] {
                            LogicalDirectoryWrapper.class,
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
                throw new NoSuccessException("changeDir", e);
            }
            super.setProxy(proxy);
            setWrapperURL(url);            
        } else {
            proxy.changeDir(dir);
        }
    }


    public Object clone() throws CloneNotSupportedException {
        LogicalDirectoryWrapper clone = (LogicalDirectoryWrapper) super.clone();
        clone.proxy = (LogicalDirectorySPI) SAGAEngine.createAdaptorCopy(
                LogicalDirectorySPI.class, proxy, clone);
        clone.setProxy(clone.proxy);
        return clone;
    }

    public List<URL> find(String namePattern, String[] attrPattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        checkNotClosed();
        if (!Flags.READ.isSet(logicalFlags)) {
            throw new PermissionDeniedException(
                    "find() on logicalDirectory not opened for reading", this);
        }

        return proxy.find(namePattern, attrPattern, flags);
    }

    public List<URL> find(String namePattern, String[] attrPattern)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return find(namePattern, attrPattern, Flags.RECURSIVE.getValue());
    }

    public Task<LogicalDirectory, List<URL>> find(TaskMode mode,
            String namePattern, String[] attrPattern, int flags)
            throws NotImplementedException {
        return proxy.find(mode, namePattern, attrPattern, flags);
    }

    public Task<LogicalDirectory, List<URL>> find(TaskMode mode,
            String namePattern, String[] attrPattern)
            throws NotImplementedException {
        return find(mode, namePattern, attrPattern, Flags.RECURSIVE.getValue());
    }

    public String[] findAttributes(String... patterns)
            throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.findAttributes(patterns);
    }

    public Task<LogicalDirectory, String[]> findAttributes(TaskMode mode,
            String... patterns) throws NotImplementedException {
        return proxy.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.getAttribute(key);
    }

    public Task<LogicalDirectory, String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return proxy.getVectorAttribute(key);
    }

    public Task<LogicalDirectory, String[]> getVectorAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isReadOnlyAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> isReadOnlyAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isReadOnlyAttribute(mode, key);
    }

    public boolean existsAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.existsAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> existsAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.existsAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isRemovableAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> isRemovableAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isVectorAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> isVectorAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isWritableAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> isWritableAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.listAttributes();
    }

    public Task<LogicalDirectory, String[]> listAttributes(TaskMode mode)
            throws NotImplementedException {
        return proxy.listAttributes(mode);
    }

    public Task<LogicalDirectory, LogicalDirectory> openLogicalDir(
            TaskMode mode, URL name, int flags) throws NotImplementedException {
	checkURLType(name);
        return proxy.openLogicalDir(mode, name, flags);
    }

    public Task<LogicalDirectory, LogicalDirectory> openLogicalDir(
            TaskMode mode, URL name) throws NotImplementedException {
        return openLogicalDir(mode, name, Flags.READ.getValue());
    }

    public LogicalDirectory openLogicalDir(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
	checkURLType(name);
        checkNotClosed();
        if (Flags.CREATE.isSet(flags) && !Flags.WRITE.isSet(logicalFlags)) {
            throw new PermissionDeniedException(
                    "openLogicalDir with CREATE flag "
                            + "on logicalDirectory not opened for writing", this);
        }
        name = resolveToDir(name);
        return LogicalFileWrapperFactory.createLogicalDirectory(sessionImpl, name, flags);
    }

    public LogicalDirectory openLogicalDir(URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return openLogicalDir(name, Flags.READ.getValue());
    }

    public Task<LogicalDirectory, LogicalFile> openLogicalFile(TaskMode mode,
            URL name, int flags) throws NotImplementedException {
	checkURLType(name);
        return proxy.openLogicalFile(mode, name, flags);
    }

    public Task<LogicalDirectory, LogicalFile> openLogicalFile(TaskMode mode,
            URL name) throws NotImplementedException {
        return openLogicalFile(mode, name, Flags.READ.getValue());
    }

    public LogicalFile openLogicalFile(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
	checkURLType(name);
        checkNotClosed();
        if (Flags.CREATE.isSet(flags) && !Flags.WRITE.isSet(logicalFlags)) {
            throw new PermissionDeniedException(
                    "openLogicalFile with CREATE flag on "
                            + "logicalDirectory not opened for writing", this);
        }
        name = resolveToDir(name);
        return LogicalFileWrapperFactory.createLogicalFile(sessionImpl, name, flags);
    }

    public LogicalFile openLogicalFile(URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return openLogicalFile(name, Flags.READ.getValue());
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        proxy.removeAttribute(key);
    }

    public Task<LogicalDirectory, Void> removeAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.setAttribute(key, value);
    }

    public Task<LogicalDirectory, Void> setAttribute(TaskMode mode, String key,
            String value) throws NotImplementedException {
        return proxy.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.setVectorAttribute(key, values);
    }

    public Task<LogicalDirectory, Void> setVectorAttribute(TaskMode mode,
            String key, String[] values) throws NotImplementedException {
        return proxy.setVectorAttribute(mode, key, values);
    }

    public boolean isFile(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException,
            IncorrectStateException, TimeoutException, NoSuccessException {
	checkURLType(name);
        checkNotClosed();
        return proxy.isFile(name);
    }

    public Task<NSDirectory, Boolean> isFile(TaskMode mode, URL name)
            throws NotImplementedException {
	checkURLType(name);
        return proxy.isFile(mode, name);
    }

}
