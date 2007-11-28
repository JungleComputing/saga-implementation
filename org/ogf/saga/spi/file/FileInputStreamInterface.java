package org.ogf.saga.spi.file;

import java.io.IOException;

/**
 * Interface describing the methods in the SAGA FileInputStream.
 * This interface is used to create a proxy. The SPI should
 * implement this interface.
 */
public interface FileInputStreamInterface {
    
    Object clone() throws CloneNotSupportedException;
    
    int read() throws IOException;
    
    int read(byte[] b) throws IOException;
    
    int read(byte[] b, int offset, int len) throws IOException;
    
    long skip(long cnt) throws IOException;
    
    int available() throws IOException;
    
    void close() throws IOException;
    
    void mark(int readLimit);
    
    void reset() throws IOException;
    
    boolean markSupported();
    

}
