package org.ogf.saga.proxies.file;

import java.io.IOException;

import org.ogf.saga.SagaObject;

/**
 * Interface describing the methods in the SAGA FileOutputStream.
 * This interface is used to create a proxy. The SPI should
 * implement this interface.
 */
public interface FileOutputStreamInterface extends SagaObject {
    
    void write(int b) throws IOException;
    
    void write(byte[] b) throws IOException;
    
    void write(byte[] b, int offset, int len) throws IOException;
    
    void close() throws IOException;
    
    void flush() throws IOException;
    
}
