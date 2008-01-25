package org.ogf.saga.impl;

import java.util.UUID;

import org.ogf.saga.ObjectType;
import org.ogf.saga.SagaObject;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.session.Session;

public abstract class SagaObjectBase implements SagaObject {
    
    protected org.ogf.saga.impl.session.Session session;
    private UUID uuid = UUID.randomUUID();
   
    protected SagaObjectBase(Session session) {
        if (session != null
                && ! (session instanceof org.ogf.saga.impl.session.Session)) {
            throw new SagaError("Wrong session type: " + session.getClass().getName());
        }
        this.session = (org.ogf.saga.impl.session.Session) session;
    }
    
    protected SagaObjectBase(SagaObjectBase cp) {
        session = cp.session;
        uuid = UUID.randomUUID();
    }

    public String getId() {
        return uuid.toString();
    }

    public Session getSession() throws DoesNotExist {
        if (session == null) {
            throw new DoesNotExist("No session associated with object");
        }
        return session;
    }

    public abstract ObjectType getType();
    
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
