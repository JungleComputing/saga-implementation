package org.ogf.saga.impl.session;

public class SessionFactoryImpl extends org.ogf.saga.session.SessionFactory {

    protected final SessionImpl defaultSession;
    
    public SessionFactoryImpl() {
        defaultSession = new SessionImpl(true);
    }
    
    protected synchronized org.ogf.saga.session.Session doCreateSession(
            boolean defaults) {
        return defaults ? defaultSession : new SessionImpl(false);
    }
}
