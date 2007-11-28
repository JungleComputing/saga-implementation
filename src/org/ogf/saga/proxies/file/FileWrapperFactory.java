package org.ogf.saga.proxies.file;

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
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.file.IOVec;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.DirectorySpiInterface;
import org.ogf.saga.spi.file.FileInputStreamSpiInterface;
import org.ogf.saga.spi.file.FileSpiInterface;
import org.ogf.saga.spi.file.FileOutputStreamSpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class FileWrapperFactory extends FileFactory {

    protected Directory doCreateDirectory(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess { 
        Object[] parameters = { session, name, flags };
        DirectorySpiInterface proxy = (DirectorySpiInterface) getAdaptorProxy(
                    "org.ogf.saga.spi.file.DirectorySpi",
                    DirectorySpiInterface.class, parameters);
            return new DirectoryWrapper(session, proxy);
    }

    protected File doCreateFile(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        Object[] parameters = { session, name, flags };
        FileSpiInterface proxy = (FileSpiInterface) getAdaptorProxy(
                "org.ogf.saga.spi.file.FileSpi",
                FileSpiInterface.class, parameters);
        return new FileWrapper(session, proxy);
    }

    protected FileInputStream doCreateFileInputStream(Session session, URL name)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        Object[] parameters = { session, name };
        FileInputStreamSpiInterface proxy = (FileInputStreamSpiInterface)
            getAdaptorProxy(
                "org.ogf.saga.spi.file.FileInputStreamSpi",
                FileInputStreamSpiInterface.class, parameters);
        return new FileInputStreamWrapper(session, name, proxy);
    }

    protected FileOutputStream doCreateFileOutputStream(Session session,
            URL name, boolean append) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        Object[] parameters = { session, name, append };
        FileOutputStreamSpiInterface proxy = (FileOutputStreamSpiInterface)
            getAdaptorProxy(
                "org.ogf.saga.spi.file.FileOutputStreamSpi",
                FileOutputStreamSpiInterface.class, parameters);
        return new FileOutputStreamWrapper(session, name, proxy);
    }
    
    protected static Object getAdaptorProxy(String cpiClassName,
            Class<?> interfaceClass, Object[] parameters) {

        try {
            return SAGAEngine.createAdaptorProxy(
                    cpiClassName, interfaceClass, parameters);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Override
    protected Task<Directory> doCreateDirectory(TaskMode mode, Session session,
            URL name, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Directory>(this, session, mode,
                "doCreateDirectory",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);
    }

    @Override
    protected Task<File> doCreateFile(TaskMode mode, Session session, URL name,
            int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<File>(this, session, mode,
                "doCreateFile",
                new Class[] { Session.class, URL.class, Integer.TYPE},
                session, name, flags);
    }

    @Override
    protected Task<FileInputStream> doCreateFileInputStream(TaskMode mode,
            Session session, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<FileInputStream>(this, session, mode,
                "doCreateFileInputStream",
                new Class[] { Session.class, URL.class},
                session, name);
    }

    @Override
    protected Task<FileOutputStream> doCreateFileOutputStream(TaskMode mode,
            Session session, URL name, boolean append) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<FileOutputStream>(this, session, mode,
                "doCreateFileOutputStream",
                new Class[] { Session.class, URL.class, Boolean.TYPE},
                session, name, append);
    }

    @Override
    protected IOVec doCreateIOVec(byte[] data, int lenIn) throws BadParameter,
            NoSuccess {
        // TODO Auto-generated method stub
        return null;
    }
}
