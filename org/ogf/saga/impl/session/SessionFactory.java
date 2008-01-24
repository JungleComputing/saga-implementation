package org.ogf.saga.impl.session;

public class SessionFactory extends org.ogf.saga.session.SessionFactory {

    protected final Session defaultSession;
    
    public SessionFactory() {
        defaultSession = new Session(true);
    }
    
    protected synchronized org.ogf.saga.session.Session doCreateSession(
            boolean defaults) {
        return defaults ? defaultSession : new Session(false);
    }
}
