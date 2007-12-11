package org.ogf.saga.proxies.rpc;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.impl.buffer.Buffer;
import org.ogf.saga.rpc.IOMode;

public class Parameter extends Buffer implements org.ogf.saga.rpc.Parameter {
    
    IOMode mode;

    protected Parameter() throws NotImplemented, BadParameter {
        super();
        mode = IOMode.IN;
    }
    
    protected Parameter(IOMode mode) throws NotImplemented, BadParameter {
        super();
        this.mode = mode;
    }
    
    protected Parameter(int sz) throws NotImplemented, BadParameter { 
        super(sz);
        this.mode = IOMode.IN;
    }

    protected Parameter(int sz, IOMode mode) throws NotImplemented, BadParameter { 
        super(sz);
        this.mode = mode;
    }
    
    protected Parameter(byte[] b) throws NotImplemented, BadParameter { 
        super(b);
        this.mode = IOMode.IN;
    }
    
    protected Parameter(byte[] b, IOMode mode) throws NotImplemented, BadParameter { 
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
