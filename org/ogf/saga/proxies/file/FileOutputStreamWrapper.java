package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.ObjectType;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.session.Session;

class FileOutputStreamWrapper extends FileOutputStream {
    
    private FileOutputStreamInterface proxy;
    
    FileOutputStreamWrapper(FileOutputStreamInterface proxy) {
        this.proxy = proxy;
    }

    public Object clone() throws CloneNotSupportedException {
        // TODO: fix this!
        return proxy.clone();
    }

    public void close() throws IOException {
        proxy.close();
    }

    public void flush() throws IOException {
        proxy.flush();
    }

    public String getId() {
        return proxy.getId();
    }

    public Session getSession() throws DoesNotExist {
        return proxy.getSession();
    }

    public ObjectType getType() {
        return ObjectType.FILEOUTPUTSTREAM;
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
