package org.ogf.saga.spi.file;

import java.io.IOException;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class FileOutputStreamSpi extends AdaptorBase implements
        FileOutputStreamSpiInterface {

    public FileOutputStreamSpi(Session session, Object wrapper) {
        super(session, wrapper);
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);        
    }
    
    public Task write(TaskMode mode, int b) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "write",
            new Class[] { Integer.TYPE }, b);
    }
    
    public Task write(TaskMode mode, byte[] buf, int off, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "write",
                new Class[] { byte[].class, Integer.TYPE, Integer.TYPE },
                buf, off, len);
    }
    
    public Task write(TaskMode mode, byte[] buf) throws NotImplementedException {
        return write(mode, buf, 0, buf.length);
    }
    
    public Task flush(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "flush",
                new Class[] { });
    }
    
    public Task close(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "close",
                new Class[] { });
    }
}
