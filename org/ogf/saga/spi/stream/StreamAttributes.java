package org.ogf.saga.spi.stream;

import org.ogf.saga.impl.attributes.AsyncAttributes;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.session.Session;
import org.ogf.saga.stream.Stream;

public class StreamAttributes extends AsyncAttributes<Stream> {
    StreamAttributes(Stream object, Session session, boolean autoAdd) {
        super(object, session, autoAdd);
    }
    
    protected StreamAttributes(StreamAttributes orig) {
        super(orig);
    }
    
    public Object clone() {
        return new StreamAttributes(this);
    }

    StreamAttributes(Stream object, Session session) {
        this(object, session, false);
    }
    
    protected void addAttribute(String name, AttributeType type, boolean vector,
            boolean readOnly, boolean notImplemented, boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }
}
