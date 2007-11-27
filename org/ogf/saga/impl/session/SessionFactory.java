package org.ogf.saga.impl.session;

public class SessionFactory extends org.ogf.saga.session.SessionFactory {

    protected Session defaultSession = new Session(true);
    
    protected synchronized org.ogf.saga.session.Session doCreateSession(
            boolean defaults) {
        if (defaults) {
            return defaultSession;
        }
        return new Session(false);
    }
}
