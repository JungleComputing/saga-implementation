package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.file.FileOutputStreamWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class FileOutputStreamAdaptorBase extends
        AdaptorBase<FileOutputStreamWrapper> implements FileOutputStreamSPI {

    public FileOutputStreamAdaptorBase(SessionImpl sessionImpl,
            FileOutputStreamWrapper wrapper) {
        super(sessionImpl, wrapper);
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public Task<FileOutputStream, Void> write(TaskMode mode, int b)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileOutputStream, Void>(wrapper,
                sessionImpl, mode, "write", new Class[] { Integer.TYPE }, b);
    }

    public Task<FileOutputStream, Void> write(TaskMode mode, byte[] buf,
            int off, int len) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileOutputStream, Void>(wrapper,
                sessionImpl, mode, "write", new Class[] { byte[].class,
                        Integer.TYPE, Integer.TYPE }, buf, off, len);
    }

    public Task<FileOutputStream, Void> write(TaskMode mode, byte[] buf)
            throws NotImplementedException {
        return write(mode, buf, 0, buf.length);
    }

    public Task<FileOutputStream, Void> flush(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileOutputStream, Void>(wrapper,
                sessionImpl, mode, "flush", new Class[] {});
    }

    public Task<FileOutputStream, Void> close(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileOutputStream, Void>(wrapper,
                sessionImpl, mode, "close", new Class[] {});
    }
}
