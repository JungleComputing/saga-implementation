package org.ogf.saga.spi.file;

import org.ogf.saga.URL;
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
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.DirectoryWrapper;
import org.ogf.saga.spi.namespace.NSDirectorySpi;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class DirectorySpi extends NSDirectorySpi implements
        DirectorySpiInterface {
    
    protected int directoryFlags;
    protected DirectoryWrapper wrapper;

    public DirectorySpi(DirectoryWrapper wrapper, Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException, BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags & Flags.ALLNAMESPACEFLAGS.getValue());
        this.wrapper = wrapper;
        directoryFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
        if ((directoryFlags | Flags.ALLFILEFLAGS.getValue())
                != Flags.ALLFILEFLAGS.getValue()) {
            throw new BadParameterException("Illegal flags for Direectory constructor: " + flags);
        }
    }
    
    public void setWrapper(DirectoryWrapper wrapper) {
        this.wrapper = wrapper;
        super.setWrapper(wrapper);
    }

    public Task<Directory, Long> getSize(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Directory, Long>(wrapper, session, mode,
                "getSize", new Class[] { URL.class, Integer.TYPE }, name, flags);
    }

    public Task<Directory, Boolean> isFile(TaskMode mode, URL arg1) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Directory, Boolean>(wrapper, session, mode,
                "isFile", new Class[] { URL.class }, arg1);
    }
    
    public Directory openDirectory(URL name, int flags)
            throws NotImplementedException, IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolve(name);
        return FileFactory.createDirectory(session, name, flags);
    }

    public Task<Directory, Directory> openDirectory(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Directory, Directory>(wrapper, session, mode,
                "openDirectory", new Class[] { URL.class, Integer.TYPE }, name,
                flags);
    }
    
    public File openFile(URL name, int flags)
            throws NotImplementedException, IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolve(name);
        return FileFactory.createFile(session, name, flags);
    }


    public Task<Directory, File> openFile(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Directory, File>(wrapper, session, mode,
                "openFile", new Class[] { URL.class, Integer.TYPE }, name,
                flags);
    }
    
    public FileInputStream openFileInputStream(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolve(name);
        return FileFactory.createFileInputStream(session, name);
    }

    public FileOutputStream openFileOutputStream(URL name, boolean append)
            throws NotImplementedException, IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolve(name);
        return FileFactory.createFileOutputStream(session, name, append);
    }


    public Task<Directory, FileInputStream> openFileInputStream(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Directory, FileInputStream>(wrapper, session,
                mode, "openFileInputStream", new Class[] { URL.class }, name);
    }

    public Task<Directory, FileOutputStream> openFileOutputStream(TaskMode mode, URL name,
            boolean append) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Directory, FileOutputStream>(wrapper, session,
                mode, "openFileOutputStream", new Class[] { URL.class,
                        Boolean.TYPE }, name, append);
    }

}
