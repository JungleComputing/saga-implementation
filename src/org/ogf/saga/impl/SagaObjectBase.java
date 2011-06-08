package org.ogf.saga.impl;

import java.util.UUID;

import org.ogf.saga.SagaObject;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.impl.bootstrap.MetaFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;

public class SagaObjectBase implements SagaObject {
    
    public static final String MY_FACTORY = MetaFactory.class.getName();

    protected org.ogf.saga.impl.session.SessionImpl sessionImpl;
    private UUID uuid = UUID.randomUUID();

    public SagaObjectBase() {
        this((Session) null);
    }
    
    public void checkSessionType(Session session) {
        if (session != null
                && !(session instanceof org.ogf.saga.impl.session.SessionImpl)) {
            throw new SagaRuntimeException("Wrong session type: "
                    + session.getClass().getName());
        }
    }
        
    public void checkURLType(URL url) {
        if (url != null
                && !(url instanceof org.ogf.saga.impl.url.URLImpl)) {
            throw new SagaRuntimeException("Wrong URL type: "
                    + url.getClass().getName());
        }
    }

    public SagaObjectBase(Session session) {
	checkSessionType(session);
        this.sessionImpl = (org.ogf.saga.impl.session.SessionImpl) session;
    }

    protected SagaObjectBase(SagaObjectBase cp) {
        this(cp, true);
    }

    
    protected SagaObjectBase(SagaObjectBase cp, boolean newUuid) {
        sessionImpl = cp.sessionImpl;
        if (! newUuid) {
            uuid = cp.uuid;
        }
    }

    public String getId() {
        return uuid.toString();
    }

    public Session getSession() throws DoesNotExistException {
        if (sessionImpl == null) {
            throw new DoesNotExistException("No session associated with object");
        }
        return sessionImpl;
    }

    public Object clone() throws CloneNotSupportedException {
        SagaObjectBase clone = (SagaObjectBase) super.clone();
        // Note: session should not get cloned! According to the SAGA
        // specs, it gets "shallow copied", which means that the
        // object reference is copied.
        clone.uuid = UUID.randomUUID();
        return clone;
    }
}
