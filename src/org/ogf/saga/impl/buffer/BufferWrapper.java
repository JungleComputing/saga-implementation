package org.ogf.saga.impl.buffer;

import org.ogf.saga.ObjectType;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.impl.SagaObjectBase;

final class BufferWrapper extends SagaObjectBase implements org.ogf.saga.buffer.Buffer {

    protected byte[] buf;
    protected boolean implementationManaged;
    protected int size;
    protected boolean closed = false;

    BufferWrapper() throws NotImplemented, BadParameter {
        this(-1);
    }

    BufferWrapper(int size) throws NotImplemented, BadParameter {
        super(null);
        try {
            setSize(size);
        } catch (IncorrectState e) {
            // Cannot happen.
        }
    }

    BufferWrapper(byte[] buf) throws BadParameter, NotImplemented {
        super(null);
        try {
            setData(buf);
        } catch(IncorrectState e) {
            // Cannot happen.
        }
    }

    public void close() throws NotImplemented {
        buf = null;
        closed = true;
    }

    public void close(float timeoutInSeconds) throws NotImplemented {
        close();
    }

    public byte[] getData() throws NotImplemented, DoesNotExist, IncorrectState {
        if (closed) {
            throw new IncorrectState("Buffer is closed");
        }
        if (buf == null) {
            throw new DoesNotExist("Implementation-managed buffer not allocated yet");
        }
        return buf;
    }

    public int getSize() throws NotImplemented, IncorrectState {
        if (closed) {
            throw new IncorrectState("Buffer is closed");
        }
        return size;
    }

    public void setData(byte[] data) throws NotImplemented, BadParameter, IncorrectState {
        if (closed) {
            throw new IncorrectState("Buffer is closed");
        }
        if (data == null) {
            throw new BadParameter("null buffer specified");
        }
        this.buf = data;
        size = buf.length;
        implementationManaged = false;
    }

    public void setSize(int size) throws NotImplemented, BadParameter, IncorrectState {
        if (closed) {
            throw new IncorrectState("Buffer is closed");
        }
        buf = null;
        implementationManaged = true;
        if (size < 0) {
            this.size = -1;
        } else {
            this.size = size;
        }
    }

    public void setSize() throws NotImplemented, BadParameter, IncorrectState {
        setSize(-1);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BufferWrapper b = (BufferWrapper) super.clone();
        if (buf != null) {
            b.buf = buf.clone();
        }
        return b;
    }

    @Override
    public ObjectType getType() {
        return ObjectType.BUFFER;
    }
}

