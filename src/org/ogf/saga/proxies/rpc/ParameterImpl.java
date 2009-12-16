package org.ogf.saga.proxies.rpc;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.rpc.IOMode;

public class ParameterImpl extends SagaObjectBase implements
        org.ogf.saga.rpc.Parameter {

    IOMode mode;
    Object data;

    protected ParameterImpl(Object b, IOMode mode)
            throws NotImplementedException, BadParameterException {
        this.data = b;
        this.mode = mode;
    }

    public IOMode getIOMode() {
        return mode;
    }

    public void setIOMode(IOMode mode) {
        this.mode = mode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
