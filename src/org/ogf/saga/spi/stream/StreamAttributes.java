package org.ogf.saga.spi.stream;

import org.ogf.saga.impl.attributes.AsyncAttributesImpl;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.session.Session;
import org.ogf.saga.stream.Stream;

public class StreamAttributes extends AsyncAttributesImpl<Stream> {
    StreamAttributes(Stream object, Session session, boolean autoAdd) {
        super(object, session, autoAdd);
    }

    protected StreamAttributes(StreamAttributes orig) {
        super(orig);
    }

    StreamAttributes(Stream object, Session session) {
        this(object, session, false);
    }

    protected void addAttribute(String name, AttributeType type,
            boolean vector, boolean readOnly, boolean notImplemented,
            boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented,
                removeable);
    }
}
