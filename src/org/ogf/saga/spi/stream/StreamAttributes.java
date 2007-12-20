package org.ogf.saga.spi.stream;

import org.ogf.saga.impl.attributes.AsyncAttributes;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.session.Session;

public class StreamAttributes extends AsyncAttributes {
    StreamAttributes(Object object, Session session, boolean autoAdd) {
        super(object, session, autoAdd);
    }
    
    protected StreamAttributes(StreamAttributes orig) {
        super(orig);
    }
    
    public Object clone() {
        return new StreamAttributes(this);
    }

    StreamAttributes(Object object, Session session) {
        this(object, session, false);
    }
    
    protected void addAttribute(String name, AttributeType type, boolean vector,
            boolean readOnly, boolean notImplemented, boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }
}
