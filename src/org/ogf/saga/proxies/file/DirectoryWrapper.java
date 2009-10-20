package org.ogf.saga.proxies.file;

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
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class DirectoryWrapper extends NSDirectoryWrapper implements Directory {

    private DirectorySPI proxy;
    
    private final int directoryFlags;
    
    private static final int GET_SIZE_FLAGS = Flags.DEREFERENCE.getValue();

    DirectoryWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        super(session, name);
        directoryFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
        if ((directoryFlags | Flags.ALLFILEFLAGS.getValue()) != Flags.ALLFILEFLAGS
                .getValue()) {
            throw new BadParameterException(
                    "Illegal flags for Directory constructor: " + flags);
        }
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (DirectorySPI) SAGAEngine.createAdaptorProxy(
                    DirectorySPI.class, new Class[] { DirectoryWrapper.class,
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
                proxy = (DirectorySPI) SAGAEngine.createAdaptorProxy(
                        DirectorySPI.class, new Class[] {
                            DirectoryWrapper.class,
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

    public long getSize(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        checkNotClosed();
        if ((GET_SIZE_FLAGS | flags) != GET_SIZE_FLAGS) {
            String msg = "Flags not allowed for getSize: " + flags;
            throw new BadParameterException(msg, this);
        }
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
        checkNotClosed();
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
        checkNotClosed();
        if (Flags.CREATE.isSet(flags) && !Flags.WRITE.isSet(directoryFlags)) {
            throw new PermissionDeniedException(
                    "openDirirectory with CREATE flag "
                            + "on Directory not opened for writing", this);
        }
        name = resolveToDir(name);
        return FileWrapperFactory.createDirectory(sessionImpl, name, flags);
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
        if (Flags.CREATE.isSet(flags) && !Flags.WRITE.isSet(directoryFlags)) {
            throw new PermissionDeniedException(
                    "openFile with CREATE flag "
                            + "on Directory not opened for writing", this);
        }
        name = resolveToDir(name);
        return FileWrapperFactory.createFile(sessionImpl, name, flags);
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
        if (!Flags.WRITE.isSet(directoryFlags)) {
            throw new PermissionDeniedException(
                    "openFileOutputStream "
                            + "on directory not opened for writing", this);
        }
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
