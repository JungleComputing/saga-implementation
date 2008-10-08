package org.ogf.saga.impl.buffer;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;

public class BufferFactoryImpl extends org.ogf.saga.buffer.BufferFactory {
    @Override
    protected org.ogf.saga.buffer.Buffer doCreateBuffer(byte[] data) throws NotImplementedException,
            BadParameterException, NoSuccessException {       
        return new BufferImpl(data);
    }

    @Override
    protected org.ogf.saga.buffer.Buffer doCreateBuffer() throws NotImplementedException, BadParameterException,
            NoSuccessException {
        return new BufferImpl();
    }

    @Override
    protected org.ogf.saga.buffer.Buffer doCreateBuffer(int size) throws NotImplementedException,
            BadParameterException, NoSuccessException {
        return new BufferImpl(size);
    }
}
