package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/**
 * Interface describing the methods in the SAGA FileInputStream.
 * This interface is used to create a proxy. The SPI should
 * implement this interface.
 */
public interface FileInputStreamSpiInterface {
    
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
       
    public Task<Integer> read(TaskMode mode) throws NotImplementedException;
    
    public Task<Integer> read(TaskMode mode, byte[] buf, int off, int len)
            throws NotImplementedException;
    
    public Task<Integer> read(TaskMode mode, byte[] buf) throws NotImplementedException;
    
    public Task<Long> skip(TaskMode mode, long n) throws NotImplementedException;
    
    public Task<Integer> available(TaskMode mode) throws NotImplementedException;
    
    public Task close(TaskMode mode) throws NotImplementedException;
    
    public Task mark(TaskMode mode, int readlimit)
               throws NotImplementedException;
    
    public Task reset(TaskMode mode) throws NotImplementedException;
    
    public Task<Boolean> markSupported(TaskMode mode)
            throws NotImplementedException;

}
