package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.FileOutputStreamSPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class FileOutputStreamWrapper extends FileOutputStream {

    // FileOutputStreamWrapper cannot extend SagaObjectBase, since it already
    // extends FileOutputStream. So, we create the base object here.
    private static class OutputSagaObject extends SagaObjectBase {
        OutputSagaObject(Session session) {
            super(session);
        }
    }

    private OutputSagaObject sagaObject;
    private FileOutputStreamSPI proxy;

    FileOutputStreamWrapper(Session session, URL name, boolean append)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {	
        Object[] parameters = { this, session, name, append };
        try {
            sagaObject = new OutputSagaObject(session);
            sagaObject.checkURLType(name);
            proxy = (FileOutputStreamSPI) SAGAEngine.createAdaptorProxy(
                    FileOutputStreamSPI.class, new Class[] {
                            FileOutputStreamWrapper.class,
                            org.ogf.saga.impl.session.SessionImpl.class,
                            URL.class, Boolean.TYPE }, parameters);
            
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

    public Object clone() throws CloneNotSupportedException {
        FileOutputStreamWrapper clone = (FileOutputStreamWrapper) super.clone();
        clone.sagaObject = (OutputSagaObject) sagaObject.clone();
        clone.proxy = (FileOutputStreamSPI) SAGAEngine.createAdaptorCopy(
                FileOutputStreamSPI.class, proxy, clone);
        return clone;
    }
    
    protected void finalize() {
        try {
            close();
        } catch(Throwable e) {
            // ignored
        }
    }

    public void close() throws IOException {
        proxy.close();
    }

    public void flush() throws IOException {
        proxy.flush();
    }

    public String getId() {
        return sagaObject.getId();
    }

    public Session getSession() throws DoesNotExistException {
        return sagaObject.getSession();
    }

    public void write(byte[] b, int offset, int len) throws IOException {
        proxy.write(b, offset, len);
    }

    public void write(byte[] b) throws IOException {
        proxy.write(b);
    }

    public void write(int b) throws IOException {
        proxy.write(b);
    }

    public Task<FileOutputStream, Void> close(TaskMode mode)
            throws NotImplementedException {
        return proxy.close(mode);
    }

    public Task<FileOutputStream, Void> flush(TaskMode mode)
            throws NotImplementedException {
        return proxy.flush(mode);
    }

    public Task<FileOutputStream, Void> write(TaskMode mode, int b)
            throws NotImplementedException {
        return proxy.write(mode, b);
    }

    public Task<FileOutputStream, Void> write(TaskMode mode, byte[] buf,
            int off, int len) throws NotImplementedException {
        return proxy.write(mode, buf, off, len);
    }

}
