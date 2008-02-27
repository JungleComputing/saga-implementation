package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class FileInputStreamSpi extends AdaptorBase implements
        FileInputStreamSpiInterface {

    public FileInputStreamSpi(Session session, Object wrapper) {
        super(session, wrapper);
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
        
    public Task<Integer> read(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode, "read",
                new Class[] { });
    }
    
    public Task<Integer> read(TaskMode mode, byte[] buf, int off, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode, "read",
                new Class[] { byte[].class, Integer.TYPE, Integer.TYPE },
                buf, off, len);
    }
    
    public Task<Integer> read(TaskMode mode, byte[] buf) throws NotImplementedException {
        return read(mode, buf, 0, buf.length);
    }
    
    public Task<Long> skip(TaskMode mode, long n) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Long>(wrapper, session, mode, "skip",
                new Class[] {Long.TYPE}, n);
    }
    
    public Task<Integer> available(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode, "available",
                new Class[]{});
    }
    
    public Task close(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "close",
                new Class[]{});            
    }
    
    public Task mark(TaskMode mode, int readlimit)
               throws NotImplementedException {        
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "mark",
                new Class[]{Integer.TYPE}, readlimit); 
    }
    
    public Task reset(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "reset",
                new Class[]{});        
    }
    
    public Task<Boolean> markSupported(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session, mode,
                "markSupported", new Class[]{});       
    }
}
