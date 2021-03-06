package org.ogf.saga.proxies.file;

import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
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
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class FileWrapperFactory extends FileFactory {

    public Directory doCreateDirectory(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return new DirectoryWrapper(session, name, flags);
    }

    public File doCreateFile(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return new FileWrapper(session, name, flags);
    }

    public FileInputStream doCreateFileInputStream(Session session, URL name)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return new FileInputStreamWrapper(session, name);
    }

    public FileOutputStream doCreateFileOutputStream(Session session, URL name,
            boolean append) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {

        return new FileOutputStreamWrapper(session, name, append);
    }

    protected Task<FileFactory, Directory> doCreateDirectory(TaskMode mode,
            Session session, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileFactory, Directory>(
                this, session, mode, "doCreateDirectory", new Class[] {
                        Session.class, URL.class, Integer.TYPE }, session,
                name, flags);
    }

    protected Task<FileFactory, File> doCreateFile(TaskMode mode,
            Session session, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileFactory, File>(this,
                session, mode, "doCreateFile", new Class[] { Session.class,
                        URL.class, Integer.TYPE }, session, name, flags);
    }

    protected Task<FileFactory, FileInputStream> doCreateFileInputStream(
            TaskMode mode, Session session, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileFactory, FileInputStream>(
                this, session, mode, "doCreateFileInputStream", new Class[] {
                        Session.class, URL.class }, session, name);
    }

    protected Task<FileFactory, FileOutputStream> doCreateFileOutputStream(
            TaskMode mode, Session session, URL name, boolean append)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileFactory, FileOutputStream>(
                this, session, mode, "doCreateFileOutputStream", new Class[] {
                        Session.class, URL.class, Boolean.TYPE }, session,
                name, append);
    }

    protected org.ogf.saga.file.IOVec doCreateIOVec(byte[] data, int lenIn)
            throws BadParameterException, NoSuccessException {
        return new IOVecImpl(data, lenIn);
    }

    protected org.ogf.saga.file.IOVec doCreateIOVec(int size, int lenIn)
            throws BadParameterException, NoSuccessException {
        return new IOVecImpl(size, lenIn);
    }
}
