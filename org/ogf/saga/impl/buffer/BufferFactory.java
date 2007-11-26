package org.ogf.saga.impl.buffer;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;

public class BufferFactory extends org.ogf.saga.buffer.BufferFactory {
    @Override
    protected org.ogf.saga.buffer.Buffer doCreateBuffer(byte[] data) throws NotImplemented,
            BadParameter, NoSuccess {       
        return new Buffer(data);
    }

    @Override
    protected org.ogf.saga.buffer.Buffer doCreateBuffer() throws NotImplemented, BadParameter,
            NoSuccess {
        return new Buffer();
    }

    @Override
    protected org.ogf.saga.buffer.Buffer doCreateBuffer(int size) throws NotImplemented,
            BadParameter, NoSuccess {
        return new Buffer(size);
    }
}
