package org.ogf.saga.spi.stream;

import org.ogf.saga.impl.attributes.AsyncAttributes;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.session.Session;

public class StreamAttributes extends AsyncAttributes {
    StreamAttributes(Session session, boolean autoAdd) {
        super(session, autoAdd);
    }

    StreamAttributes(Session session) {
        super(session, false);
    }
    
    protected void addAttribute(String name, AttributeType type, boolean vector,
            boolean readOnly, boolean notImplemented, boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }
}
