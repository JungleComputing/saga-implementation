package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.URL;
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
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.FileInputStreamSpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class FileInputStreamWrapper extends FileInputStream {
    
    private static class InputSagaObject extends SagaObjectBase {
        
        InputSagaObject(Session session) {
            super(session);
        }
    }
    
    private InputSagaObject sagaObject;
    private FileInputStreamSpiInterface proxy;
       
    FileInputStreamWrapper(Session session, URL name)
            throws NotImplementedException, IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException, NoSuccessException {
        Object[] parameters = { this, session, name };
        try {
            proxy = (FileInputStreamSpiInterface) SAGAEngine.createAdaptorProxy(
                    FileInputStreamSpiInterface.class,
                    new Class[] { FileInputStreamWrapper.class,
                        org.ogf.saga.impl.session.Session.class, URL.class },
                    parameters);
            sagaObject = new InputSagaObject(session);
        } catch(org.ogf.saga.error.SagaException e) {
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
            if (e instanceof  AlreadyExistsException) {
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

    public int available() throws IOException {
        return proxy.available();
    }

    public Object clone() throws CloneNotSupportedException {
        FileInputStreamWrapper clone = (FileInputStreamWrapper) super.clone();
        clone.sagaObject = (InputSagaObject) sagaObject.clone();
        clone.proxy = (FileInputStreamSpiInterface) SAGAEngine.createAdaptorCopy(
                    FileInputStreamSpiInterface.class, proxy, clone);
        return clone;
    }

    public void close() throws IOException {
        proxy.close();
    }

    public String getId() {
        return sagaObject.getId();
    }

    public Session getSession() throws DoesNotExistException {
        return sagaObject.getSession();
    }

    public void mark(int readLimit) {
        proxy.mark(readLimit);
    }

    public boolean markSupported() {
        return proxy.markSupported();
    }

    public int read() throws IOException {
        return proxy.read();
    }

    public int read(byte[] b, int offset, int len) throws IOException {
        return proxy.read(b, offset, len);
    }

    public int read(byte[] b) throws IOException {
        return proxy.read(b);
    }

    public void reset() throws IOException {
        proxy.reset();
    }

    public long skip(long cnt) throws IOException {
        return proxy.skip(cnt);
    }

    public Task<Integer> available(TaskMode mode)
            throws NotImplementedException {
        return proxy.available(mode);
    }

    public Task close(TaskMode mode) throws NotImplementedException {
        return proxy.close(mode);
    }

    public Task mark(TaskMode mode, int readlimit)
            throws NotImplementedException {
        return proxy.mark(mode, readlimit);
    }

    public Task<Boolean> markSupported(TaskMode mode)
            throws NotImplementedException {
        return proxy.markSupported(mode);
    }

    public Task<Integer> read(TaskMode mode) throws NotImplementedException {
        return proxy.read(mode);
    }

    public Task<Integer> read(TaskMode mode, byte[] buf, int off, int len)
            throws NotImplementedException {
        return proxy.read(mode, buf, off, len);
    }

    public Task reset(TaskMode mode) throws NotImplementedException {
        return proxy.reset(mode);
    }

    public Task<Long> skip(TaskMode mode, long n)
            throws NotImplementedException {
        return proxy.skip(mode, n);
    }

}
