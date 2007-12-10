package org.ogf.saga.proxies.logicalfile;

import java.util.List;

import org.ogf.saga.ObjectType;
import org.ogf.saga.URL;
import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.logicalfile.LogicalDirectory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.logicalfile.LogicalDirectorySpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public final class LogicalDirectoryWrapper extends NSDirectoryWrapper implements LogicalDirectory {
    
    private LogicalDirectorySpiInterface proxy;
    
    LogicalDirectoryWrapper(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        super(session);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (LogicalDirectorySpiInterface) SAGAEngine.createAdaptorProxy(
                    LogicalDirectorySpiInterface.class,
                    new Class[] { LogicalDirectoryWrapper.class,
                        org.ogf.saga.impl.session.Session.class, URL.class,
                        Integer.TYPE },
                        parameters);
            super.setProxy(proxy);
        } catch(org.ogf.saga.error.Exception e) {
            if (e instanceof NotImplemented) {
                throw (NotImplemented) e;
            }
            if (e instanceof IncorrectURL) {
                throw (IncorrectURL) e;
            }
            if (e instanceof AuthenticationFailed) {
                throw (AuthenticationFailed) e;
            }
            if (e instanceof AuthorizationFailed) {
                throw (AuthorizationFailed) e;
            }
            if (e instanceof PermissionDenied) {
                throw (PermissionDenied) e;
            }
            if (e instanceof BadParameter) {
                throw (BadParameter) e;
            }
            if (e instanceof  AlreadyExists) {
                throw (AlreadyExists) e;
            }
            if (e instanceof DoesNotExist) {
                throw (DoesNotExist) e;
            }
            if (e instanceof Timeout) {
                throw (Timeout) e;
            }
            if (e instanceof NoSuccess) {
                throw (NoSuccess) e;
            }
            throw new NoSuccess("Constructor failed", e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        LogicalDirectoryWrapper clone = (LogicalDirectoryWrapper) super.clone();
        clone.proxy = (LogicalDirectorySpiInterface)
                SAGAEngine.createAdaptorCopy(LogicalDirectorySpiInterface.class, proxy, clone);
        clone.setProxy(clone.proxy);
        return clone;
    }

    public List<URL> find(String namePattern, String[] attrPattern, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.find(namePattern, attrPattern, flags);
    }

    public List<URL> find(String namePattern, String[] attrPattern) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return find(namePattern, attrPattern, Flags.RECURSIVE.getValue());
    }

    public Task<List<URL>> find(TaskMode mode, String namePattern, String[] attrPattern, int flags) throws NotImplemented {
        return proxy.find(mode, namePattern, attrPattern, flags);
    }

    public Task<List<URL>> find(TaskMode mode, String namePattern, String[] attrPattern) throws NotImplemented {
        return find(mode, namePattern, attrPattern, Flags.RECURSIVE.getValue());
    }

    public String[] findAttributes(String... patterns) throws NotImplemented, BadParameter, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.findAttributes(patterns);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns) throws NotImplemented {
        return proxy.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getAttribute(key);
    }

    public Task<String> getAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.getAttribute(mode, key);
    }

    public ObjectType getType() {
        return ObjectType.LOGICALDIRECTORY;
    }

    public String[] getVectorAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isReadOnlyAttribute(key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isRemovableAttribute(key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isVectorAttribute(key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isWritableAttribute(key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.listAttributes();
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplemented {
        return proxy.listAttributes(mode);
    }
    public Task<LogicalDirectory> openLogicalDir(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.openLogicalDir(mode, name, flags);
    }

    public Task<LogicalDirectory> openLogicalDir(TaskMode mode, URL name) throws NotImplemented {
        return openLogicalDir(mode, name, Flags.NONE.getValue());
    }

    public LogicalDirectory openLogicalDir(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openLogicalDir(name, flags);
    }

    public LogicalDirectory openLogicalDir(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return openLogicalDir(name, Flags.NONE.getValue());
    }

    public Task<LogicalFile> openLogicalFile(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.openLogicalFile(mode, name, flags);
    }

    public Task<LogicalFile> openLogicalFile(TaskMode mode, URL name) throws NotImplemented {
        return openLogicalFile(mode, name, Flags.NONE.getValue());
    }

    public LogicalFile openLogicalFile(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openLogicalFile(name, flags);
    }

    public LogicalFile openLogicalFile(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return openLogicalFile(name, Flags.NONE.getValue());
    }

    public void removeAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        proxy.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        proxy.setAttribute(key, value);
    }

    public Task setAttribute(TaskMode mode, String key, String value) throws NotImplemented {
        return proxy.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        proxy.setVectorAttribute(key, values);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values) throws NotImplemented {
        return proxy.setVectorAttribute(mode, key, values);
    }

    public boolean isFile(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, DoesNotExist, IncorrectState, Timeout, NoSuccess {
        return proxy.isFile(name);
    }

    public Task<Boolean> isFile(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isFile(mode, name);
    }
    
    

}
