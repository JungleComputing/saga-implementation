package org.ogf.saga.proxies.file;

import org.ogf.saga.URL;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
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
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class FileWrapperFactory extends FileFactory {

    protected Directory doCreateDirectory(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess { 
            return new DirectoryWrapper(session, name, flags);
    }

    protected File doCreateFile(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return new FileWrapper(session, name, flags);
    }
   

    protected FileInputStream doCreateFileInputStream(Session session, URL name)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        return new FileInputStreamWrapper(session, name);
    }

    protected FileOutputStream doCreateFileOutputStream(Session session,
            URL name, boolean append) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {

        return new FileOutputStreamWrapper(session, name, append);
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

	@Override
	protected IOVec doCreateIOVec(int size, int lenIn) throws BadParameter, NoSuccess {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IOVec doCreateIOVec(byte[] data) throws BadParameter, NoSuccess {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IOVec doCreateIOVec(int size) throws BadParameter, NoSuccess {
		// TODO Auto-generated method stub
		return null;
	}
}
