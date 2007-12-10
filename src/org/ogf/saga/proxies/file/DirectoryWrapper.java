package org.ogf.saga.proxies.file;

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
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.DirectorySpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class DirectoryWrapper extends NSDirectoryWrapper implements Directory {

    private DirectorySpiInterface proxy;
    
    DirectoryWrapper(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
                AuthorizationFailed, PermissionDenied, BadParameter, AlreadyExists,
                DoesNotExist, Timeout, NoSuccess {
        super(session);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (DirectorySpiInterface) SAGAEngine.createAdaptorProxy(
                    DirectorySpiInterface.class,
                    new Class[] { DirectoryWrapper.class,
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
    
    public long getSize(URL name, int flags)
        throws NotImplemented, IncorrectURL, AuthenticationFailed,
                AuthorizationFailed, PermissionDenied, BadParameter,
                IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getSize(name, flags);
    }
    
    public long getSize(URL name)
        throws NotImplemented, IncorrectURL, AuthenticationFailed,
                AuthorizationFailed, PermissionDenied, BadParameter,
                IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return getSize(name, Flags.NONE.getValue());
    }

    public Task<Long> getSize(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return proxy.getSize(mode, name, flags);
    }
    
    public Task<Long> getSize(TaskMode mode, URL name)
            throws NotImplemented {
        return getSize(mode, name, Flags.NONE.getValue());
    }
    
    public Object clone() throws CloneNotSupportedException {
        DirectoryWrapper clone = (DirectoryWrapper) super.clone();
        clone.proxy = (DirectorySpiInterface) SAGAEngine.createAdaptorCopy(
                DirectorySpiInterface.class, proxy, clone);
        clone.setProxy(proxy);
        return clone;
    }

    public ObjectType getType() {
        return ObjectType.DIRECTORY;
    }

    public boolean isFile(URL name)
            throws NotImplemented, IncorrectURL, DoesNotExist, AuthenticationFailed,
                AuthorizationFailed, PermissionDenied, BadParameter,
                IncorrectState, Timeout, NoSuccess {
        return proxy.isFile(name);
    }
        
    public Task<Boolean> isFile(TaskMode mode, URL name)
            throws NotImplemented {
        return proxy.isFile(mode, name);
    }
    
    public Task<Directory> openDirectory(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.openDirectory(mode, name, flags);
    }

    public Task<Directory> openDirectory(TaskMode mode, URL name) throws NotImplemented {
        return openDirectory(mode, name, Flags.READ.getValue());
    }

    public Directory openDirectory(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openDirectory(name, flags);
    }

    public Directory openDirectory(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return openDirectory(name, Flags.READ.getValue());
    }

    public Task<File> openFile(TaskMode mode, URL name, int flags) throws NotImplemented {
        return proxy.openFile(mode, name, flags);
    }

    public Task<File> openFile(TaskMode mode, URL name) throws NotImplemented {
        return openFile(mode, name, Flags.READ.getValue());
    }

    public File openFile(URL name, int flags) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openFile(name, flags);
    }

    public File openFile(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return openFile(name, Flags.READ.getValue());
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
        return openFileOutputStream(mode, name, false);
    }

    public FileOutputStream openFileOutputStream(URL name, boolean append) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return proxy.openFileOutputStream(name, append);
    }

    public FileOutputStream openFileOutputStream(URL name) throws NotImplemented, IncorrectURL, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return openFileOutputStream(name, false);
    }
}
