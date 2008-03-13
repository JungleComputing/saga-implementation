package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.proxies.file.FileInputStreamWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class FileInputStreamAdaptorBase extends AdaptorBase<FileInputStreamWrapper> implements
        FileInputStreamSPI {
   
    public FileInputStreamAdaptorBase(Session session, FileInputStreamWrapper wrapper) {
        super(session, wrapper);
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
        
    public Task<FileInputStream, Integer> read(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<FileInputStream, Integer>(wrapper, session, mode, "read",
                new Class[] { });
    }
    
    public Task<FileInputStream, Integer> read(TaskMode mode, byte[] buf, int off, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<FileInputStream, Integer>(wrapper, session, mode, "read",
                new Class[] { byte[].class, Integer.TYPE, Integer.TYPE },
                buf, off, len);
    }
    
    public Task<FileInputStream, Integer> read(TaskMode mode, byte[] buf) throws NotImplementedException {
        return read(mode, buf, 0, buf.length);
    }
    
    public Task<FileInputStream, Long> skip(TaskMode mode, long n) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<FileInputStream, Long>(wrapper, session, mode, "skip",
                new Class[] {Long.TYPE}, n);
    }
    
    public Task<FileInputStream, Integer> available(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<FileInputStream, Integer>(wrapper, session, mode, "available",
                new Class[]{});
    }
    
    public Task<FileInputStream, Void> close(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<FileInputStream, Void>(wrapper, session, mode, "close",
                new Class[]{});            
    }
    
    public Task<FileInputStream, Void> mark(TaskMode mode, int readlimit)
               throws NotImplementedException {        
        return new org.ogf.saga.impl.task.Task<FileInputStream, Void>(wrapper, session, mode, "mark",
                new Class[]{Integer.TYPE}, readlimit); 
    }
    
    public Task<FileInputStream, Void> reset(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<FileInputStream, Void>(wrapper, session, mode, "reset",
                new Class[]{});        
    }
    
    public Task<FileInputStream, Boolean> markSupported(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<FileInputStream, Boolean>(wrapper, session, mode,
                "markSupported", new Class[]{});       
    }
}
