package org.ogf.saga.spi.file;

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
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.DirectoryWrapper;
import org.ogf.saga.spi.namespace.NSDirectoryAdaptorBase;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public abstract class DirectoryAdaptorBase extends NSDirectoryAdaptorBase
        implements DirectorySPI {
   
    protected int directoryFlags;
    protected DirectoryWrapper directoryWrapper;

    public DirectoryAdaptorBase(DirectoryWrapper wrapper,
            SessionImpl sessionImpl, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, sessionImpl, name, flags
                & Flags.ALLNAMESPACEFLAGS.getValue());
        this.directoryWrapper = wrapper;
        directoryFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
    }

    public void setWrapper(DirectoryWrapper wrapper) {
        this.directoryWrapper = wrapper;
        super.setWrapper(wrapper);
    }

    public Task<Directory, Long> getSize(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<Directory, Long>(
                directoryWrapper, sessionImpl, mode, "getSize", new Class[] {
                        URL.class, Integer.TYPE }, name, flags);
    }

    public Task<Directory, Boolean> isFile(TaskMode mode, URL arg1)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<Directory, Boolean>(
                directoryWrapper, sessionImpl, mode, "isFile",
                new Class[] { URL.class }, arg1);
    }

    public Directory openDirectory(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolveToDir(name);
        return FileFactory.createDirectory(sessionImpl, name, flags);
    }

    public Task<Directory, Directory> openDirectory(TaskMode mode, URL name,
            int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<Directory, Directory>(
                directoryWrapper, sessionImpl, mode, "openDirectory",
                new Class[] { URL.class, Integer.TYPE }, name, flags);
    }

    public File openFile(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        name = resolveToDir(name);
        return FileFactory.createFile(sessionImpl, name, flags);
    }

    public Task<Directory, File> openFile(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<Directory, File>(
                directoryWrapper, sessionImpl, mode, "openFile", new Class[] {
                        URL.class, Integer.TYPE }, name, flags);
    }

    public FileInputStream openFileInputStream(URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolveToDir(name);
        return FileFactory.createFileInputStream(sessionImpl, name);
    }

    public FileOutputStream openFileOutputStream(URL name, boolean append)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolveToDir(name);
        return FileFactory.createFileOutputStream(sessionImpl, name, append);
    }

    public Task<Directory, FileInputStream> openFileInputStream(TaskMode mode,
            URL name) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<Directory, FileInputStream>(
                directoryWrapper, sessionImpl, mode, "openFileInputStream",
                new Class[] { URL.class }, name);
    }

    public Task<Directory, FileOutputStream> openFileOutputStream(
            TaskMode mode, URL name, boolean append)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<Directory, FileOutputStream>(
                directoryWrapper, sessionImpl, mode, "openFileOutputStream",
                new Class[] { URL.class, Boolean.TYPE }, name, append);
    }

}
