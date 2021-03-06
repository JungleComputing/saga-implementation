package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/**
 * Interface describing the methods in the SAGA FileOutputStream. This interface
 * is used to create a proxy. The SPI should implement this interface.
 */
public interface FileOutputStreamSPI {

    Object clone() throws CloneNotSupportedException;

    void write(int b) throws IOException;

    void write(byte[] b) throws IOException;

    void write(byte[] b, int offset, int len) throws IOException;

    void close() throws IOException;

    void flush() throws IOException;

    public Task<FileOutputStream, Void> write(TaskMode mode, int b)
            throws NotImplementedException;

    public Task<FileOutputStream, Void> write(TaskMode mode, byte[] buf,
            int off, int len) throws NotImplementedException;

    public Task<FileOutputStream, Void> write(TaskMode mode, byte[] buf)
            throws NotImplementedException;

    public Task<FileOutputStream, Void> flush(TaskMode mode)
            throws NotImplementedException;

    public Task<FileOutputStream, Void> close(TaskMode mode)
            throws NotImplementedException;
}
