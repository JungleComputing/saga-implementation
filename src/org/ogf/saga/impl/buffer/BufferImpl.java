package org.ogf.saga.impl.buffer;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;

public class BufferImpl extends SagaObjectBase implements org.ogf.saga.buffer.Buffer, Cloneable {

    protected byte[] buf;
    protected boolean implementationManaged;
    protected int size;
    protected boolean closed = false;

    protected BufferImpl() throws NotImplementedException, BadParameterException {
        this(-1);
    }

    protected BufferImpl(int size) throws NotImplementedException, BadParameterException {
        super((Session) null);
        try {
            setSize(size);
        } catch (IncorrectStateException e) {
            // Cannot happen.
        }
    }

    protected BufferImpl(byte[] buf) throws BadParameterException, NotImplementedException {
        super((Session) null);
        try {
            setData(buf);
        } catch(IncorrectStateException e) {
            // Cannot happen.
        }
    }
    
    protected BufferImpl(BufferImpl orig) {
        super(orig);
        implementationManaged = orig.implementationManaged;
        size = orig.size;
        closed = orig.closed;
        if (orig.buf != null) {
            buf = orig.buf.clone();
        } else {
            buf = null;
        }       
    }

    public void close() throws NotImplementedException {
        buf = null;
        closed = true;
    }

    public void close(float timeoutInSeconds) throws NotImplementedException {
        close();
    }

    public byte[] getData() throws NotImplementedException, DoesNotExistException, IncorrectStateException {
        if (closed) {
            throw new IncorrectStateException("Buffer is closed");
        }
        if (buf == null) {
            throw new DoesNotExistException("Implementation-managed buffer not allocated yet");
        }
        return buf;             // findbugs complains about this ...
    }
    
    public byte[] getBuf() {
        return buf;             // findbugs complains about this ...
    }
    
    public boolean isImplementationManaged() {
        return implementationManaged;
    }

    public int getSize() throws NotImplementedException, IncorrectStateException {
        if (closed) {
            throw new IncorrectStateException("Buffer is closed");
        }
        return size;
    }

    public void setData(byte[] data) throws NotImplementedException, BadParameterException, IncorrectStateException {
        if (closed) {
            throw new IncorrectStateException("Buffer is closed");
        }
        if (data == null) {
            throw new BadParameterException("null buffer specified");
        }
        this.buf = data;        // findbugs complains about this ...
        size = buf.length;
        implementationManaged = false;
    }

    public void setSize(int size) throws NotImplementedException, BadParameterException, IncorrectStateException {
        if (closed) {
            throw new IncorrectStateException("Buffer is closed");
        }
        buf = null;
        implementationManaged = true;
        if (size < 0) {
            this.size = -1;
        } else {
            this.size = size;
            buf = new byte[size];
        }
    }

    public void setSize() throws NotImplementedException, BadParameterException, IncorrectStateException {
        setSize(-1);
    }

    public Object clone() throws CloneNotSupportedException {
        BufferImpl o = (BufferImpl) super.clone();
        if (buf != null) {
            o.buf = buf.clone();
        }      
        return o;
    }
}
