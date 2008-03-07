package org.ogf.saga.impl.stream;

import java.io.IOException;

import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class OutputStream extends org.ogf.saga.stream.StreamOutputStream {

    protected java.io.OutputStream streamBase;
    protected SagaObjectBase sagaBase;
    protected Session session;

    public OutputStream(Session session, java.io.OutputStream base) {
        streamBase = base;
        sagaBase = new SagaObjectBase(session);
        this.session = session;
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);        
    }
    
    public Task<org.ogf.saga.stream.StreamOutputStream, Void> write(TaskMode mode, int b) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.stream.StreamOutputStream, Void>(this, session, mode, "write",
            new Class[] { Integer.TYPE }, b);
    }
    
    public Task<org.ogf.saga.stream.StreamOutputStream, Void> write(TaskMode mode, byte[] buf, int off, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.stream.StreamOutputStream, Void>(this, session, mode, "write",
                new Class[] { byte[].class, Integer.TYPE, Integer.TYPE },
                buf, off, len);
    }
    
    public Task<org.ogf.saga.stream.StreamOutputStream, Void> write(TaskMode mode, byte[] buf) throws NotImplementedException {
        return write(mode, buf, 0, buf.length);
    }
    
    public Task<org.ogf.saga.stream.StreamOutputStream, Void> flush(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.stream.StreamOutputStream, Void>(this, session, mode, "flush",
                new Class[] { });
    }
    
    public Task<org.ogf.saga.stream.StreamOutputStream, Void> close(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.stream.StreamOutputStream, Void>(this, session, mode, "close",
                new Class[] { });
    }

    public String getId() {
        return sagaBase.getId();
    }

    public Session getSession() throws DoesNotExistException {
        return sagaBase.getSession();
    }

    public void close() throws IOException {
        streamBase.close();
    }

    public void flush() throws IOException {
        streamBase.flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        streamBase.write(b, off, len);
    }

    public void write(int b) throws IOException {
        streamBase.write(b);
    }
}
