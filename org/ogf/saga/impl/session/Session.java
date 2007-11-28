package org.ogf.saga.impl.session;

import java.util.HashMap;
import java.util.HashSet;

import org.ogf.saga.ObjectType;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.impl.SagaObjectBase;

public class Session extends SagaObjectBase
        implements org.ogf.saga.session.Session {

    private HashSet<Context> contexts = new HashSet<Context>();
    
    private HashMap<String, AdaptorSessionInterface> adaptorSessions
            = new HashMap<String, AdaptorSessionInterface>();

    Session(boolean defaults) {
        super(null);
    }
    
    public synchronized AdaptorSessionInterface getAdaptorSession(String name) {
        return adaptorSessions.get(name);
    }
    
    public synchronized void putAdaptorSession(String name,
            AdaptorSessionInterface session) {
        adaptorSessions.put(name, session);
    }

    public synchronized void addContext(Context context) throws NotImplemented {
        try {
            context = (Context) context.clone();
        } catch(CloneNotSupportedException e) {
            throw new SagaError("Context.clone() not supported?", e);
        }
        for (AdaptorSessionInterface session : adaptorSessions.values()) {
            try {
                session.addContext((org.ogf.saga.impl.context.Context) context);
            } catch(Throwable e) {
                // ignored
            }
        }
        contexts.add(context);
    }

    public synchronized void close() throws NotImplemented {
        for (AdaptorSessionInterface session : adaptorSessions.values()) {
            try {
                session.close();
            } catch(Throwable e) {
                // ignored
            }
        }
        adaptorSessions.clear();
    }

    protected void finalize() {
        try {
            close();
        } catch (Throwable e) {
            // ignored
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized Object clone() throws CloneNotSupportedException {
        Session clone = (Session) super.clone();
        clone.contexts = new HashSet<Context>(contexts);
        clone.adaptorSessions
                = new HashMap<String, AdaptorSessionInterface>();
        for (String key : adaptorSessions.keySet()) {
            clone.adaptorSessions.put(key,
                    (AdaptorSessionInterface) adaptorSessions.get(key).clone());
        }
        return clone;
    }

    public void close(float timeoutInSeconds) throws NotImplemented {
        close();
    }

    public synchronized Context[] listContexts() throws NotImplemented {
        Context[] c = contexts.toArray(new Context[contexts.size()]);
        for (int i = 0; i < c.length; i++) {
            try {
                c[i] = (Context) c[i].clone();
            } catch (CloneNotSupportedException e) {
                // Should not happen
            }
        }
        return c;
    }

    public synchronized void removeContext(Context context)
            throws NotImplemented, DoesNotExist {

        if (!contexts.remove(context)) {
            throw new DoesNotExist("Element " + context + " does not exist");
        }
        for (AdaptorSessionInterface session : adaptorSessions.values()) {
            try {
                session.removeContext((org.ogf.saga.impl.context.Context) context);
            } catch(Throwable e) {
                // ignored
            }
        }
    }

    public ObjectType getType() {
        return ObjectType.SESSION;
    }
}
