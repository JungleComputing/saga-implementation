package org.ogf.saga.proxies.file;

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
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

class DirectoryWrapper implements Directory {

    private Directory proxy;
    
    DirectoryWrapper(Directory proxy) {
        this.proxy = proxy;
    }

    public Task changeDir(TaskMode mode, URL dir) throws NotImplemented {
        return proxy.changeDir(mode, dir);
    }

    public void changeDir(URL dir) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        proxy.changeDir(dir);
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

    public Task copy(TaskMode mode, URL source, URL target, int flags) throws NotImplemented {
        return proxy.copy(mode, source, target, flags);
    }

    public Task copy(TaskMode mode, URL source, URL target) throws NotImplemented {
        return proxy.copy(mode, source, target);
    }

    public Task copy(TaskMode mode, URL target) throws NotImplemented {
        return proxy.copy(mode, target);
    }

    public void copy(URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.copy(target, flags);
    }

    public void copy(URL source, URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.copy(source, target, flags);
    }

    public void copy(URL source, URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.copy(source, target);
    }

    public void copy(URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.copy(target);
    }

    public Task<Boolean> exists(TaskMode mode, URL name) throws NotImplemented {
        return proxy.exists(mode, name);
    }

    public boolean exists(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.exists(name);
    }

    public List<URL> find(String pattern, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.find(pattern, flags);
    }

    public List<URL> find(String pattern) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.find(pattern);
    }

    public Task<List<URL>> find(TaskMode mode, String pattern, int flags) throws NotImplemented {
        return proxy.find(mode, pattern, flags);
    }

    public Task<List<URL>> find(TaskMode mode, String pattern) throws NotImplemented {
        return proxy.find(mode, pattern);
    }

    public URL getCWD() throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        return proxy.getCWD();
    }

    public Task<URL> getCWD(TaskMode mode) throws NotImplemented {
        return proxy.getCWD(mode);
    }

    public URL getEntry(int entry) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getEntry(entry);
    }

    public Task<URL> getEntry(TaskMode mode, int entry) throws NotImplemented {
        return proxy.getEntry(mode, entry);
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

    public int getNumEntries() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return proxy.getNumEntries();
    }

    public Task<Integer> getNumEntries(TaskMode mode) throws NotImplemented {
        return proxy.getNumEntries(mode);
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

    public Task<Long> getSize(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.getSize(mode, name, flags);
    }

    public Task<Long> getSize(TaskMode mode, URL name) throws NotImplemented {
        return proxy.getSize(mode, name);
    }

    public long getSize(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getSize(name, flags);
    }

    public long getSize(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getSize(name);
    }

    public ObjectType getType() {
        return ObjectType.DIRECTORY;
    }

    public URL getURL() throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        return proxy.getURL();
    }

    public Task<URL> getURL(TaskMode mode) throws NotImplemented {
        return proxy.getURL(mode);
    }

    public boolean isDir() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isDir();
    }

    public Task<Boolean> isDir(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isDir(mode, name);
    }

    public Task<Boolean> isDir(TaskMode mode) throws NotImplemented {
        return proxy.isDir(mode);
    }

    public boolean isDir(URL name) throws NotImplemented, IncorrectURL, DoesNotExist, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isDir(name);
    }

    public boolean isEntry() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isEntry();
    }

    public Task<Boolean> isEntry(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isEntry(mode, name);
    }

    public Task<Boolean> isEntry(TaskMode mode) throws NotImplemented {
        return proxy.isEntry(mode);
    }

    public boolean isEntry(URL name) throws NotImplemented, IncorrectURL, DoesNotExist, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isEntry(name);
    }

    public Task<Boolean> isFile(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isFile(mode, name);
    }

    public boolean isFile(URL name) throws NotImplemented, IncorrectURL, DoesNotExist, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isFile(name);
    }

    public boolean isLink() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isLink();
    }

    public Task<Boolean> isLink(TaskMode mode, URL name) throws NotImplemented {
        return proxy.isLink(mode, name);
    }

    public Task<Boolean> isLink(TaskMode mode) throws NotImplemented {
        return proxy.isLink(mode);
    }

    public boolean isLink(URL name) throws NotImplemented, IncorrectURL, DoesNotExist, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.isLink(name);
    }

    public Task link(TaskMode mode, URL target, int flags) throws NotImplemented {
        return proxy.link(mode, target, flags);
    }

    public Task link(TaskMode mode, URL source, URL target, int flags) throws NotImplemented {
        return proxy.link(mode, source, target, flags);
    }

    public Task link(TaskMode mode, URL source, URL target) throws NotImplemented {
        return proxy.link(mode, source, target);
    }

    public Task link(TaskMode mode, URL target) throws NotImplemented {
        return proxy.link(mode, target);
    }

    public void link(URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        proxy.link(target, flags);
    }

    public void link(URL source, URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.link(source, target, flags);
    }

    public void link(URL source, URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.link(source, target);
    }

    public void link(URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, Timeout, NoSuccess, IncorrectURL {
        proxy.link(target);
    }

    public List<URL> list() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return proxy.list();
    }

    public List<URL> list(int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return proxy.list(flags);
    }

    public List<URL> list(String pattern, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return proxy.list(pattern, flags);
    }

    public List<URL> list(String pattern) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        return proxy.list(pattern);
    }

    public Task<List<URL>> list(TaskMode mode, int flags) throws NotImplemented {
        return proxy.list(mode, flags);
    }

    public Task<List<URL>> list(TaskMode mode, String pattern, int flags) throws NotImplemented {
        return proxy.list(mode, pattern, flags);
    }

    public Task<List<URL>> list(TaskMode mode, String pattern) throws NotImplemented {
        return proxy.list(mode, pattern);
    }

    public Task<List<URL>> list(TaskMode mode) throws NotImplemented {
        return proxy.list(mode);
    }

    public Task makeDir(TaskMode mode, URL target, int flags) throws NotImplemented {
        return proxy.makeDir(mode, target, flags);
    }

    public Task makeDir(TaskMode mode, URL target) throws NotImplemented {
        return proxy.makeDir(mode, target);
    }

    public void makeDir(URL target, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.makeDir(target, flags);
    }

    public void makeDir(URL target) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.makeDir(target);
    }

    public Task move(TaskMode mode, URL target, int flags) throws NotImplemented {
        return proxy.move(mode, target, flags);
    }

    public Task move(TaskMode mode, URL source, URL target, int flags) throws NotImplemented {
        return proxy.move(mode, source, target, flags);
    }

    public Task move(TaskMode mode, URL source, URL target) throws NotImplemented {
        return proxy.move(mode, source, target);
    }

    public Task move(TaskMode mode, URL target) throws NotImplemented {
        return proxy.move(mode, target);
    }

    public void move(URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.move(target, flags);
    }

    public void move(URL source, URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.move(source, target, flags);
    }

    public void move(URL source, URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        proxy.move(source, target);
    }

    public void move(URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess, IncorrectURL {
        proxy.move(target);
    }

    public Task<NSEntry> open(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.open(mode, name, flags);
    }

    public Task<NSEntry> open(TaskMode mode, URL name) throws NotImplemented {
        return proxy.open(mode, name);
    }

    public NSEntry open(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.open(name, flags);
    }

    public NSEntry open(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.open(name);
    }

    public Task<NSDirectory> openDir(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.openDir(mode, name, flags);
    }

    public Task<NSDirectory> openDir(TaskMode mode, URL name) throws NotImplemented {
        return proxy.openDir(mode, name);
    }

    public NSDirectory openDir(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openDir(name, flags);
    }

    public NSDirectory openDir(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openDir(name);
    }

    public Task<Directory> openDirectory(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.openDirectory(mode, name, flags);
    }

    public Task<Directory> openDirectory(TaskMode mode, URL name) throws NotImplemented {
        return proxy.openDirectory(mode, name);
    }

    public Directory openDirectory(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openDirectory(name, flags);
    }

    public Directory openDirectory(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openDirectory(name);
    }

    public Task<File> openFile(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.openFile(mode, name, flags);
    }

    public Task<File> openFile(TaskMode mode, URL name) throws NotImplemented {
        return proxy.openFile(mode, name);
    }

    public File openFile(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openFile(name, flags);
    }

    public File openFile(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openFile(name);
    }

    public Task<FileInputStream> openFileInputStream(TaskMode mode, URL name) throws NotImplemented {
        return proxy.openFileInputStream(mode, name);
    }

    public FileInputStream openFileInputStream(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openFileInputStream(name);
    }

    public Task<FileOutputStream> openFileOutputStream(TaskMode mode, URL name, boolean append) throws NotImplemented {
        return proxy.openFileOutputStream(mode, name, append);
    }

    public Task<FileOutputStream> openFileOutputStream(TaskMode mode, URL name) throws NotImplemented {
        return proxy.openFileOutputStream(mode, name);
    }

    public FileOutputStream openFileOutputStream(URL name, boolean append) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openFileOutputStream(name, append);
    }

    public FileOutputStream openFileOutputStream(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openFileOutputStream(name);
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

    public Task permissionsAllow(TaskMode mode, URL target, String id, int permissions, int flags) throws NotImplemented {
        return proxy.permissionsAllow(mode, target, id, permissions, flags);
    }

    public Task permissionsAllow(TaskMode mode, URL target, String id, int permissions) throws NotImplemented {
        return proxy.permissionsAllow(mode, target, id, permissions);
    }

    public void permissionsAllow(URL target, String id, int permissions, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(target, id, permissions, flags);
    }

    public void permissionsAllow(URL target, String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(target, id, permissions);
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

    public Task permissionsDeny(TaskMode mode, URL target, String id, int permissions, int flags) throws NotImplemented {
        return proxy.permissionsDeny(mode, target, id, permissions, flags);
    }

    public Task permissionsDeny(TaskMode mode, URL target, String id, int permissions) throws NotImplemented {
        return proxy.permissionsDeny(mode, target, id, permissions);
    }

    public void permissionsDeny(URL target, String id, int permissions, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(target, id, permissions, flags);
    }

    public void permissionsDeny(URL target, String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(target, id, permissions);
    }

    public URL readLink() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.readLink();
    }

    public Task<URL> readLink(TaskMode mode, URL name) throws NotImplemented {
        return proxy.readLink(mode, name);
    }

    public Task<URL> readLink(TaskMode mode) throws NotImplemented {
        return proxy.readLink(mode);
    }

    public URL readLink(URL name) throws NotImplemented, IncorrectURL, DoesNotExist, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {
        return proxy.readLink(name);
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

    public Task remove(TaskMode mode, URL target, int flags) throws NotImplemented {
        return proxy.remove(mode, target, flags);
    }

    public Task remove(TaskMode mode, URL target) throws NotImplemented {
        return proxy.remove(mode, target);
    }

    public Task remove(TaskMode mode) throws NotImplemented {
        return proxy.remove(mode);
    }

    public void remove(URL target, int flags) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        proxy.remove(target, flags);
    }

    public void remove(URL target) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectURL, BadParameter, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        proxy.remove(target);
    }
}
