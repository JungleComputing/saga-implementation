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
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.FileOutputStreamSpiInterface;

public class FileOutputStreamWrapper extends FileOutputStream {
  
    // FileOutputStreamWrapper cannot extend SagaObjectBase, since it already extends
    // FileOutputStream. So, we create the base object here.
    private static class OutputSagaObject extends SagaObjectBase {
        
        OutputSagaObject(Session session) {
            super(session);
        }

        public ObjectType getType() {
            return ObjectType.FILEOUTPUTSTREAM;
        }
    }
    
    private OutputSagaObject sagaObject;
    private FileOutputStreamSpiInterface proxy;
    
    FileOutputStreamWrapper(Session session, URL name, boolean append)
            throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        Object[] parameters = { this, session, name, append };
        try {
            proxy = (FileOutputStreamSpiInterface) SAGAEngine.createAdaptorProxy(
                    FileOutputStreamSpiInterface.class,
                    new Class[] { FileOutputStreamWrapper.class,
                        org.ogf.saga.impl.session.Session.class,
                        URL.class, Boolean.TYPE },
                    parameters);
            sagaObject = new OutputSagaObject(session);
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
   
    public Object clone() throws CloneNotSupportedException {
        FileOutputStreamWrapper clone = (FileOutputStreamWrapper) super.clone();
        clone.sagaObject = (OutputSagaObject) sagaObject.clone();
        clone.proxy = (FileOutputStreamSpiInterface) SAGAEngine.createAdaptorCopy(
                    FileOutputStreamSpiInterface.class, proxy, clone);
        return clone;
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

    public Session getSession() throws DoesNotExist {
        return sagaObject.getSession();
    }

    public ObjectType getType() {
        return sagaObject.getType();
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

}
