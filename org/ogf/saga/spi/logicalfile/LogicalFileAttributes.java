package org.ogf.saga.spi.logicalfile;

import org.ogf.saga.session.Session;
import org.ogf.saga.impl.attributes.AsyncAttributes;
import org.ogf.saga.impl.attributes.AttributeType;

class LogicalFileAttributes extends AsyncAttributes {

    LogicalFileAttributes(Object object, Session session, boolean autoAdd) {
        super(object, session, autoAdd);
    }

    LogicalFileAttributes(Object object, Session session) {
        super(object, session, false);
    }
    
    protected void addAttribute(String name, AttributeType type, boolean vector,
            boolean readOnly, boolean notImplemented, boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }
}
