package org.ogf.saga.proxies.rpc;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.rpc.IOMode;

public class Parameter extends SagaObjectBase implements
        org.ogf.saga.rpc.Parameter {

    IOMode mode;
    Object data;

    protected Parameter() throws NotImplementedException, BadParameterException {
        mode = IOMode.IN;
    }

    protected Parameter(IOMode mode) throws NotImplementedException,
            BadParameterException {
        this.mode = mode;
    }

    protected Parameter(Object b) throws NotImplementedException,
            BadParameterException {
        super();
        this.data = b;
        this.mode = IOMode.IN;
    }

    protected Parameter(Object b, IOMode mode) throws NotImplementedException,
            BadParameterException {
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
