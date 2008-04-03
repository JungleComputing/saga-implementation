package org.ogf.saga.proxies.file;

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
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.DirectorySPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class DirectoryWrapper extends NSDirectoryWrapper implements Directory {

    private DirectorySPI proxy;

    DirectoryWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        super(session);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (DirectorySPI) SAGAEngine.createAdaptorProxy(
                    DirectorySPI.class, new Class[] { DirectoryWrapper.class,
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

    public long getSize(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.getSize(name, flags);
    }

    public long getSize(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return getSize(name, Flags.NONE.getValue());
    }

    public Task<Directory, Long> getSize(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return proxy.getSize(mode, name, flags);
    }

    public Task<Directory, Long> getSize(TaskMode mode, URL name)
            throws NotImplementedException {
        return getSize(mode, name, Flags.NONE.getValue());
    }

    public Object clone() throws CloneNotSupportedException {
        DirectoryWrapper clone = (DirectoryWrapper) super.clone();
        clone.proxy = (DirectorySPI) SAGAEngine.createAdaptorCopy(
                DirectorySPI.class, proxy, clone);
        clone.setProxy(proxy);
        return clone;
    }

    public boolean isFile(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return proxy.isFile(name);
    }

    public Task<Directory, Boolean> isFile(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.isFile(mode, name);
    }

    public Task<Directory, Directory> openDirectory(TaskMode mode, URL name,
            int flags) throws NotImplementedException {
        return proxy.openDirectory(mode, name, flags);
    }

    public Task<Directory, Directory> openDirectory(TaskMode mode, URL name)
            throws NotImplementedException {
        return openDirectory(mode, name, Flags.READ.getValue());
    }

    public Directory openDirectory(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.openDirectory(name, flags);
    }

    public Directory openDirectory(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return openDirectory(name, Flags.READ.getValue());
    }

    public Task<Directory, File> openFile(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return proxy.openFile(mode, name, flags);
    }

    public Task<Directory, File> openFile(TaskMode mode, URL name)
            throws NotImplementedException {
        return openFile(mode, name, Flags.READ.getValue());
    }

    public File openFile(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return proxy.openFile(name, flags);
    }

    public File openFile(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return openFile(name, Flags.READ.getValue());
    }

    public Task<Directory, FileInputStream> openFileInputStream(TaskMode mode,
            URL name) throws NotImplementedException {
        return proxy.openFileInputStream(mode, name);
    }

    public FileInputStream openFileInputStream(URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.openFileInputStream(name);
    }

    public Task<Directory, FileOutputStream> openFileOutputStream(
            TaskMode mode, URL name, boolean append)
            throws NotImplementedException {
        return proxy.openFileOutputStream(mode, name, append);
    }

    public Task<Directory, FileOutputStream> openFileOutputStream(
            TaskMode mode, URL name) throws NotImplementedException {
        return openFileOutputStream(mode, name, false);
    }

    public FileOutputStream openFileOutputStream(URL name, boolean append)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.openFileOutputStream(name, append);
    }

    public FileOutputStream openFileOutputStream(URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return openFileOutputStream(name, false);
    }
}
