package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

/**
 * Interface describing the methods in the SAGA FileInputStream. This interface
 * is used to create a proxy. The SPI should implement this interface.
 */
public interface FileInputStreamSPI {

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

    public Task<FileInputStream, Integer> read(TaskMode mode)
            throws NotImplementedException;

    public Task<FileInputStream, Integer> read(TaskMode mode, byte[] buf,
            int off, int len) throws NotImplementedException;

    public Task<FileInputStream, Integer> read(TaskMode mode, byte[] buf)
            throws NotImplementedException;

    public Task<FileInputStream, Long> skip(TaskMode mode, long n)
            throws NotImplementedException;

    public Task<FileInputStream, Integer> available(TaskMode mode)
            throws NotImplementedException;

    public Task<FileInputStream, Void> close(TaskMode mode)
            throws NotImplementedException;

    public Task<FileInputStream, Void> mark(TaskMode mode, int readlimit)
            throws NotImplementedException;

    public Task<FileInputStream, Void> reset(TaskMode mode)
            throws NotImplementedException;

    public Task<FileInputStream, Boolean> markSupported(TaskMode mode)
            throws NotImplementedException;

}
