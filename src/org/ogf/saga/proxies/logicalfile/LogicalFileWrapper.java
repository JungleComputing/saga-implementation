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
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

final class LogicalFileWrapper implements LogicalFile {
    
    private LogicalFile proxy;
    
    LogicalFileWrapper(LogicalFile proxy) {
        this.proxy = proxy;
    }

    public Task addLocation(TaskMode mode, URL name) throws NotImplemented {
        return proxy.addLocation(mode, name);
    }

    public void addLocation(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        proxy.addLocation(name);
    }

    public Object clone() throws CloneNotSupportedException {
        // TODO: fix this!
        return proxy.clone();
    }

    public void close() throws NotImplemented, IncorrectState, NoSuccess {
        proxy.close();
    }

    public void close(float timeoutInSeconds) throws NotImplemented, IncorrectState, NoSuccess {
        proxy.close(timeoutInSeconds);
    }

    public Task close(TaskMode mode, float timeoutInSeconds) throws NotImplemented {
        return proxy.close(mode, timeoutInSeconds);
    }

    public Task close(TaskMode mode) throws NotImplemented {
        return proxy.close(mode);
    }

    public Task copy(TaskMode mode, URL target, int flags) throws NotImplemented {
        return proxy.copy(mode, target, flags);
    }

    public Task copy(TaskMode mode, URL target) throws NotImplemented {
        return proxy.copy(mode, target);
    }

    public void copy(URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.copy(target, flags);
    }

    public void copy(URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.copy(target);
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

    public URL getCWD() throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        return proxy.getCWD();
    }

    public Task<URL> getCWD(TaskMode mode) throws NotImplemented {
        return proxy.getCWD(mode);
    }

    public String getGroup() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getGroup();
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return proxy.getGroup(mode);
    }

    public String getId() {
        return proxy.getId();
    }

    public URL getName() throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        return proxy.getName();
    }

    public Task<URL> getName(TaskMode mode) throws NotImplemented {
        return proxy.getName(mode);
    }

    public String getOwner() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getOwner();
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return proxy.getOwner(mode);
    }

    public Session getSession() throws DoesNotExist {
        return proxy.getSession();
    }

    public ObjectType getType() {
        return ObjectType.LOGICALFILE;
    }

    public URL getURL() throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        return proxy.getURL();
    }

    public Task<URL> getURL(TaskMode mode) throws NotImplemented {
        return proxy.getURL(mode);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.getVectorAttribute(mode, key);
    }

    public boolean isDir() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isDir();
    }

    public Task<Boolean> isDir(TaskMode mode) throws NotImplemented {
        return proxy.isDir(mode);
    }

    public boolean isEntry() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isEntry();
    }

    public Task<Boolean> isEntry(TaskMode mode) throws NotImplemented {
        return proxy.isEntry(mode);
    }

    public boolean isLink() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isLink();
    }

    public Task<Boolean> isLink(TaskMode mode) throws NotImplemented {
        return proxy.isLink(mode);
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

    public Task link(TaskMode mode, URL target, int flags) throws NotImplemented {
        return proxy.link(mode, target, flags);
    }

    public Task link(TaskMode mode, URL target) throws NotImplemented {
        return proxy.link(mode, target);
    }

    public void link(URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        proxy.link(target, flags);
    }

    public void link(URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        proxy.link(target);
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

    public Task move(TaskMode mode, URL target, int flags) throws NotImplemented {
        return proxy.move(mode, target, flags);
    }

    public Task move(TaskMode mode, URL target) throws NotImplemented {
        return proxy.move(mode, target);
    }

    public void move(URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.move(target, flags);
    }

    public void move(URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.move(target);
    }

    public void permissionsAllow(String id, int permissions, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(id, permissions, flags);
    }

    public void permissionsAllow(String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(id, permissions);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions, int flags) throws NotImplemented {
        return proxy.permissionsAllow(mode, id, permissions, flags);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions) throws NotImplemented {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id, int permissions) throws NotImplemented {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, IncorrectState, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions, int flags) throws NotImplemented {
        return proxy.permissionsDeny(mode, id, permissions, flags);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions) throws NotImplemented {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public URL readLink() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.readLink();
    }

    public Task<URL> readLink(TaskMode mode) throws NotImplemented {
        return proxy.readLink(mode);
    }

    public void remove() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        proxy.remove();
    }

    public void remove(int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        proxy.remove(flags);
    }

    public Task remove(TaskMode mode, int flags) throws NotImplemented {
        return proxy.remove(mode, flags);
    }

    public Task remove(TaskMode mode) throws NotImplemented {
        return proxy.remove(mode);
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
        return proxy.replicate(mode, name);
    }

    public void replicate(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.replicate(name, flags);
    }

    public void replicate(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.replicate(name);
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
