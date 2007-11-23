package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.SagaObject;

/**
 * Interface describing the methods in the SAGA FileInputStream.
 * This interface is used to create a proxy. The SPI should
 * implement this interface.
 */
public interface FileInputStreamInterface extends SagaObject {
    
    public int read() throws IOException;
    
    public int read(byte[] b) throws IOException;
    
    public int read(byte[] b, int offset, int len) throws IOException;
    
    public long skip(long cnt) throws IOException;
    
    public int available() throws IOException;
    
    public void close() throws IOException;
    
    public void mark(int readLimit);
    
    public void reset() throws IOException;
    
    public boolean markSupported();
    

}
