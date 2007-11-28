package org.ogf.saga.spi.file;

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
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.spi.namespace.NSDirectorySpi;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class DirectorySpi extends NSDirectorySpi implements
        DirectorySpiInterface {
    
    protected int directoryFlags;

    public DirectorySpi(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, BadParameter, DoesNotExist,
            PermissionDenied, AuthorizationFailed, AuthenticationFailed,
            Timeout, NoSuccess, AlreadyExists {
        super(session, name, flags & Flags.ALLNAMESPACEFLAGS.getValue());
        directoryFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
        if ((directoryFlags | Flags.ALLFILEFLAGS.getValue())
                != Flags.ALLFILEFLAGS.getValue()) {
            throw new BadParameter("Illegal flags for Direectory constructor: " + flags);
        }
    }

    public Task<Long> getSize(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Long>(this, session, mode,
                "getSize", new Class[] { URL.class, Integer.TYPE }, name, flags);
    }

    public Task<Boolean> isFile(TaskMode mode, URL arg1) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session, mode,
                "isFile", new Class[] { URL.class }, arg1);
    }
    
    public Directory openDirectory(URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        name = resolve(name);
        return FileFactory.createDirectory(session, name, flags);
    }

    public Task<Directory> openDirectory(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Directory>(this, session, mode,
                "openDirectory", new Class[] { URL.class, Integer.TYPE }, name,
                flags);
    }
    
    public File openFile(URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        name = resolve(name);
        return FileFactory.createFile(session, name, flags);
    }


    public Task<File> openFile(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<File>(this, session, mode,
                "openFile", new Class[] { URL.class, Integer.TYPE }, name,
                flags);
    }
    
    public FileInputStream openFileInputStream(URL name) throws NotImplemented,
            IncorrectURL, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        name = resolve(name);
        return FileFactory.createFileInputStream(session, name);
    }

    public FileOutputStream openFileOutputStream(URL name, boolean append)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        name = resolve(name);
        return FileFactory.createFileOutputStream(session, name, append);
    }


    public Task<FileInputStream> openFileInputStream(TaskMode mode, URL name)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<FileInputStream>(this, session,
                mode, "openFileInputStream", new Class[] { URL.class }, name);
    }

    public Task<FileOutputStream> openFileOutputStream(TaskMode mode, URL name,
            boolean append) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<FileOutputStream>(this, session,
                mode, "openFileOutputStream", new Class[] { URL.class,
                        Boolean.TYPE }, name, append);
    }

}
