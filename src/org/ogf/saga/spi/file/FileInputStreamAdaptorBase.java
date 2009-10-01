package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.file.FileInputStreamWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class FileInputStreamAdaptorBase extends
        AdaptorBase<FileInputStreamWrapper> implements FileInputStreamSPI {

    public FileInputStreamAdaptorBase(SessionImpl sessionImpl,
            FileInputStreamWrapper wrapper) {
        super(sessionImpl, wrapper);
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public Task<FileInputStream, Integer> read(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Integer>(
                wrapper, sessionImpl, mode, "read", new Class[] {});
    }

    public Task<FileInputStream, Integer> read(TaskMode mode, byte[] buf,
            int off, int len) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Integer>(
                wrapper, sessionImpl, mode, "read", new Class[] { byte[].class,
                        Integer.TYPE, Integer.TYPE }, buf, off, len);
    }

    public Task<FileInputStream, Integer> read(TaskMode mode, byte[] buf)
            throws NotImplementedException {
        return read(mode, buf, 0, buf.length);
    }

    public Task<FileInputStream, Long> skip(TaskMode mode, long n)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Long>(
                wrapper, sessionImpl, mode, "skip", new Class[] { Long.TYPE },
                n);
    }

    public Task<FileInputStream, Integer> available(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Integer>(
                wrapper, sessionImpl, mode, "available", new Class[] {});
    }

    public Task<FileInputStream, Void> close(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Void>(
                wrapper, sessionImpl, mode, "close", new Class[] {});
    }

    public Task<FileInputStream, Void> mark(TaskMode mode, int readlimit)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Void>(
                wrapper, sessionImpl, mode, "mark",
                new Class[] { Integer.TYPE }, readlimit);
    }

    public Task<FileInputStream, Void> reset(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Void>(
                wrapper, sessionImpl, mode, "reset", new Class[] {});
    }

    public Task<FileInputStream, Boolean> markSupported(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<FileInputStream, Boolean>(
                wrapper, sessionImpl, mode, "markSupported", new Class[] {});
    }
}
