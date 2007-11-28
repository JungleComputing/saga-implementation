package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.ObjectType;
import org.ogf.saga.URL;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.FileOutputStreamSpiInterface;

class FileOutputStreamWrapper extends FileOutputStream {
  
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
    
    FileOutputStreamWrapper(Session session, URL name, FileOutputStreamSpiInterface proxy) {
        this.proxy = proxy;
        sagaObject = new OutputSagaObject(session);
    }

    public Object clone() throws CloneNotSupportedException {
        FileOutputStreamWrapper clone = (FileOutputStreamWrapper) super.clone();
        clone.sagaObject = (OutputSagaObject) sagaObject.clone();
        clone.proxy = (FileOutputStreamSpiInterface) proxy.clone();
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
