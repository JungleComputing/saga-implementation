package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.ObjectType;
import org.ogf.saga.URL;
import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.FileInputStreamSpiInterface;

class FileInputStreamWrapper extends FileInputStream {
    
    private static class InputSagaObject extends SagaObjectBase {
        
        InputSagaObject(Session session) {
            super(session);
        }

        public ObjectType getType() {
            return ObjectType.FILEINPUTSTREAM;
        }
    }
    
    private InputSagaObject sagaObject;
    private FileInputStreamSpiInterface proxy;
       
    FileInputStreamWrapper(Session session, URL name)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        Object[] parameters = { session, name };
        try {
            proxy = (FileInputStreamSpiInterface) SAGAEngine.createAdaptorProxy(
                    FileInputStreamSpiInterface.class,
                    new Class[] { URL.class, Integer.TYPE },
                    parameters);
            sagaObject = new InputSagaObject(session);
        } catch(org.ogf.saga.error.Exception e) {
            if (e instanceof NotImplemented) {
                throw (NotImplemented) e;
            }
            if (e instanceof IncorrectURL) {
                throw (IncorrectURL) e;
            }
            if (e instanceof AuthenticationFailed) {
                throw (AuthenticationFailed) e;
            }
            if (e instanceof AuthorizationFailed) {
                throw (AuthorizationFailed) e;
            }
            if (e instanceof PermissionDenied) {
                throw (PermissionDenied) e;
            }
            if (e instanceof BadParameter) {
                throw (BadParameter) e;
            }
            if (e instanceof  AlreadyExists) {
                throw (AlreadyExists) e;
            }
            if (e instanceof DoesNotExist) {
                throw (DoesNotExist) e;
            }
            if (e instanceof Timeout) {
                throw (Timeout) e;
            }
            if (e instanceof NoSuccess) {
                throw (NoSuccess) e;
            }
            throw new NoSuccess("Constructor failed", e);
        }
    }

    public int available() throws IOException {
        return proxy.available();
    }

    public Object clone() throws CloneNotSupportedException {
        FileInputStreamWrapper clone = (FileInputStreamWrapper) super.clone();
        clone.sagaObject = (InputSagaObject) sagaObject.clone();
        clone.proxy = (FileInputStreamSpiInterface) proxy.clone();
        return clone;
    }

    public void close() throws IOException {
        proxy.close();
    }

    public String getId() {
        return sagaObject.getId();
    }

    public Session getSession() throws DoesNotExist {
        return sagaObject.getSession();
    }

    public ObjectType getType() {
        return sagaObject.getType();
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

}
