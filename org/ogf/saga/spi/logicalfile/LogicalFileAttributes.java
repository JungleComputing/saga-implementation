package org.ogf.saga.spi.logicalfile;

import org.ogf.saga.impl.attributes.AsyncAttributes;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.session.Session;

class LogicalFileAttributes extends AsyncAttributes<LogicalFile> {

    LogicalFileAttributes(LogicalFile object, Session session, boolean autoAdd) {
        super(object, session, autoAdd);
    }

    LogicalFileAttributes(LogicalFile object, Session session) {
        super(object, session, false);
    }
    
    protected void addAttribute(String name, AttributeType type, boolean vector,
            boolean readOnly, boolean notImplemented, boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }
}
