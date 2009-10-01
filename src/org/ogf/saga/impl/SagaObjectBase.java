package org.ogf.saga.impl;

import java.util.UUID;

import org.ogf.saga.SagaObject;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.session.Session;

public class SagaObjectBase implements SagaObject {

    protected org.ogf.saga.impl.session.SessionImpl sessionImpl;
    private UUID uuid = UUID.randomUUID();

    public SagaObjectBase() {
        this((Session) null);
    }

    public SagaObjectBase(Session session) {
        if (session != null
                && !(session instanceof org.ogf.saga.impl.session.SessionImpl)) {
            throw new SagaRuntimeException("Wrong session type: "
                    + session.getClass().getName());
        }
        this.sessionImpl = (org.ogf.saga.impl.session.SessionImpl) session;
    }

    protected SagaObjectBase(SagaObjectBase cp) {
        sessionImpl = cp.sessionImpl;
        uuid = UUID.randomUUID();
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
        // Should we generate a new uuid here ??? I think yes.
        // Note: session should not get cloned! According to the SAGA
        // specs, it gets "shallow copied", which means that the
        // object reference is copied.
        clone.uuid = UUID.randomUUID();
        return clone;
    }
}
