package org.ogf.saga.proxies.logicalfile;

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
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;

final class LogicalFileWrapper extends NSEntryWrapper implements LogicalFile {
    
    private LogicalFileInterface proxy;
    
    LogicalFileWrapper(Session session, LogicalFileInterface proxy) {
        super(session, proxy);
        this.proxy = proxy;
    }

    public Task addLocation(TaskMode mode, URL name) throws NotImplemented {
        return proxy.addLocation(mode, name);
    }

    public void addLocation(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        proxy.addLocation(name);
    }

    public Object clone() throws CloneNotSupportedException {
        LogicalFileWrapper clone = (LogicalFileWrapper) super.clone();
        clone.proxy = (LogicalFileInterface) proxy.clone();
        return clone();
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
        return ObjectType.LOGICALFILE;
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

    public List<URL> listLocations() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return proxy.listLocations();
    }

    public Task<List<URL>> listLocations(TaskMode mode) throws NotImplemented {
        return proxy.listLocations(mode);
    }

    public void removeAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        proxy.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.removeAttribute(mode, key);
    }

    public Task removeLocation(TaskMode mode, URL name) throws NotImplemented {
        return proxy.removeLocation(mode, name);
    }

    public void removeLocation(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        proxy.removeLocation(name);
    }

    public Task replicate(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.replicate(mode, name, flags);
    }

    public Task replicate(TaskMode mode, URL name) throws NotImplemented {
        return replicate(mode, name, Flags.NONE.getValue());
    }

    public void replicate(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.replicate(name, flags);
    }

    public void replicate(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        replicate(name, Flags.NONE.getValue());
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

    public Task updateLocation(TaskMode mode, URL nameOld, URL nameNew) throws NotImplemented {
        return proxy.updateLocation(mode, nameOld, nameNew);
    }

    public void updateLocation(URL nameOld, URL nameNew) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.updateLocation(nameOld, nameNew);
    }
}
