package org.ogf.saga.proxies.file;

import org.ogf.saga.ObjectType;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.impl.buffer.Buffer;

public class IOVec extends Buffer implements org.ogf.saga.file.IOVec {

    private int lenIn;
    private int lenOut = 0;
    private int offset = 0;
    
    public IOVec(byte[] data, int lenIn) throws NotImplemented, BadParameter {
        super(data);
        if (lenIn > size) {
            throw new BadParameter("lenIn is larger than the buffer supplied");
        }
        this.lenIn = lenIn;
    }

    public IOVec(int size, int lenIn) throws NotImplemented, BadParameter {
        super(size);
        if (size >= 0 && lenIn > size) {
            throw new BadParameter("lenIn is larger than the specified size");
        }
        this.lenIn = lenIn; 
    }

    public IOVec(byte[] data) throws BadParameter, NotImplemented {
        this(data, data.length);
    }
    
    public IOVec(int size) throws BadParameter, NotImplemented {
        this(size, size);
    }

    public IOVec(IOVec orig) {
        super(orig);
        this.lenIn = orig.lenIn;
        this.lenOut = orig.lenOut;
        this.offset = orig.offset;
    }
    
    public Object clone() {
        return new IOVec(this);
    }

    public int getLenIn() {
        return lenIn;
    }

    public int getLenOut() {
        return lenOut;
    }

    public int getOffset() {
        return offset;
    }

    public void setLenIn(int len) throws BadParameter {
        if (size >= 0 && (offset + len > size)) {
            throw new BadParameter("Specified lenIn + offset larger than size");
        }
        this.lenIn = len;
    }

    public void setOffset(int offset) throws BadParameter {
        if (size >= 0 && (offset + lenIn > size)) {
            throw new BadParameter("Specified lenIn + offset larger than size");
        }
        this.offset = offset;
    }
    
    // For use in implementation.
    public void setLenOut(int len) {
        this.lenOut = len;
    }
    
    public ObjectType getType() {
        return ObjectType.IOVEC;
    }
}
