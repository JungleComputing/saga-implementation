package org.ogf.saga.proxies.rpc;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.buffer.Buffer;
import org.ogf.saga.rpc.IOMode;

public class Parameter extends Buffer implements org.ogf.saga.rpc.Parameter {
    
    IOMode mode;

    protected Parameter() throws NotImplementedException, BadParameterException {
        super();
        mode = IOMode.IN;
    }
    
    protected Parameter(IOMode mode) throws NotImplementedException, BadParameterException {
        super();
        this.mode = mode;
    }
    
    protected Parameter(int sz) throws NotImplementedException, BadParameterException { 
        super(sz);
        this.mode = IOMode.IN;
    }

    protected Parameter(int sz, IOMode mode) throws NotImplementedException, BadParameterException { 
        super(sz);
        this.mode = mode;
    }
    
    protected Parameter(byte[] b) throws NotImplementedException, BadParameterException { 
        super(b);
        this.mode = IOMode.IN;
    }
    
    protected Parameter(byte[] b, IOMode mode) throws NotImplementedException, BadParameterException { 
        super(b);
        this.mode = mode;
    }

    public IOMode getIOMode() {
        return mode;
    }

    public void setIOMode(IOMode mode) {
        this.mode = mode;

    }

}
