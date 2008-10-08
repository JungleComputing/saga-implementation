package org.ogf.saga.impl.stream;

import java.io.IOException;

import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;
import org.ogf.saga.stream.StreamInputStream;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class InputStream extends StreamInputStream {

    protected java.io.InputStream streamBase;
    protected SagaObjectBase sagaBase;
    protected Session session;

    public InputStream(Session session, java.io.InputStream base) {
        this.streamBase = base;
        this.session = session;
        sagaBase = new SagaObjectBase(session);
    }
    
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
        
    public Task<StreamInputStream, Integer> read(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Integer>(this, session, mode, "read",
                new Class[] { });
    }
    
    public Task<StreamInputStream, Integer> read(TaskMode mode, byte[] buf, int off, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Integer>(this, session, mode, "read",
                new Class[] { byte[].class, Integer.TYPE, Integer.TYPE },
                buf, off, len);
    }
    
    public Task<StreamInputStream, Integer> read(TaskMode mode, byte[] buf) throws NotImplementedException {
        return read(mode, buf, 0, buf.length);
    }
    
    public Task<StreamInputStream, Long> skip(TaskMode mode, long n) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Long>(this, session, mode, "skip",
                new Class[] {Long.TYPE}, n);
    }
    
    public Task<StreamInputStream, Integer> available(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Integer>(this, session, mode, "available",
                new Class[]{});
    }
    
    public Task<StreamInputStream, Void> close(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Void>(this, session, mode, "close",
                new Class[]{});            
    }
    
    public Task<StreamInputStream, Void> mark(TaskMode mode, int readlimit)
               throws NotImplementedException {        
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Void>(this, session, mode, "mark",
                new Class[]{Integer.TYPE}, readlimit); 
    }
    
    public Task<StreamInputStream, Void> reset(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Void>(this, session, mode, "reset",
                new Class[]{});        
    }
    
    public Task<StreamInputStream, Boolean> markSupported(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamInputStream, Boolean>(this, session, mode,
                "markSupported", new Class[]{});       
    }

    public int available() throws IOException {
        return streamBase.available();
    }

    public void close() throws IOException {
        streamBase.close();
    }

    public void mark(int readlimit) {
        streamBase.mark(readlimit);
    }

    public boolean markSupported() {
        return streamBase.markSupported();
    }

    public int read() throws IOException {
        return streamBase.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return streamBase.read(b, off, len);
    }

    public void reset() throws IOException {
        streamBase.reset();
    }

    public long skip(long n) throws IOException {
        return streamBase.skip(n);
    }

    public String getId() {
        return sagaBase.getId();
    }

    public org.ogf.saga.session.Session getSession() throws DoesNotExistException {
        return sagaBase.getSession();
    }
}
