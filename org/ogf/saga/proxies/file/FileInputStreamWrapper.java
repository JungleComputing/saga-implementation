package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.ObjectType;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.session.Session;

class FileInputStreamWrapper extends FileInputStream {
    
    private FileInputStreamInterface proxy;
    
    FileInputStreamWrapper(FileInputStreamInterface proxy) {
        this.proxy = proxy;
    }

    public int available() throws IOException {
        return proxy.available();
    }

    public Object clone() throws CloneNotSupportedException {
        // TODO: fix this!
        return proxy.clone();
    }

    public void close() throws IOException {
        proxy.close();
    }

    public String getId() {
        return proxy.getId();
    }

    public Session getSession() throws DoesNotExist {
        return proxy.getSession();
    }

    public ObjectType getType() {
        return ObjectType.FILEINPUTSTREAM;
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
