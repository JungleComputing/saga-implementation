package org.ogf.saga.impl.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.ogf.saga.context.Context;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.impl.context.ContextImpl;

public class SessionImpl extends SagaObjectBase implements
        org.ogf.saga.session.Session {

    private HashSet<ContextImpl> contexts = new LinkedHashSet<ContextImpl>();

    private HashMap<String, AdaptorSessionInterface> adaptorSessions
            = new HashMap<String, AdaptorSessionInterface>();
    
    private boolean closed = false;
    
    private final boolean isDefaultSession;

    SessionImpl(boolean defaults) {
        super((org.ogf.saga.session.Session) null);
        isDefaultSession = defaults;
    }
    
    public boolean isDefaultSession() {
        return isDefaultSession;
    }

    public synchronized AdaptorSessionInterface getAdaptorSession(String name) {
        return adaptorSessions.get(name);
    }

    public synchronized void putAdaptorSession(String name,
            AdaptorSessionInterface session) {
        adaptorSessions.put(name, session);
        for (ContextImpl ctxt : contexts) {
            try {
                session.addContext(ctxt);
            } catch (Throwable e) {
                // ignored
            }
        }
    }

    public synchronized void addContext(Context ctxt) throws NoSuccessException,
            TimeoutException {
        
        if (closed) {
            throw new NoSuccessException("This session is closed");
        }
        
        ContextImpl context = (ContextImpl) ctxt;
        context = new ContextImpl(context);
        context.setDefaults();
        for (AdaptorSessionInterface session : adaptorSessions.values()) {
            try {
                session.addContext(context);
            } catch (Throwable e) {
                // ignored
            }
        }       
        contexts.add(context);
    }

    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        for (AdaptorSessionInterface session : adaptorSessions.values()) {
            try {
                session.close();
            } catch (Throwable e) {
                // ignored
            }
        }
        adaptorSessions.clear();
        contexts.clear();
    }

    protected void finalize() {
        try {
            close();
        } catch (Throwable e) {
            // ignored
        }
    }

    public synchronized Object clone() throws CloneNotSupportedException {
        SessionImpl clone = (SessionImpl) super.clone();
        synchronized (clone) {
            clone.contexts = new HashSet<org.ogf.saga.impl.context.ContextImpl>(contexts);
            clone.adaptorSessions = new HashMap<String, AdaptorSessionInterface>();

            for (String key : adaptorSessions.keySet()) {
                clone.adaptorSessions.put(key,
                        (AdaptorSessionInterface) adaptorSessions.get(key)
                                .clone());
            }
        }
        return clone;
    }

    public void close(float timeoutInSeconds) {
        close();
    }

    public synchronized Context[] listContexts() {
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

    public synchronized void removeContext(Context ctxt)
            throws DoesNotExistException {
        
        if (closed) {
            return;
        }
        
        ContextImpl context= (ContextImpl) ctxt;

        if (!contexts.remove(context)) {
            throw new DoesNotExistException("Element " + context
                    + " does not exist");
        }
        for (AdaptorSessionInterface session : adaptorSessions.values()) {
            try {
                session.removeContext(context);
            } catch (Throwable e) {
                // ignored
            }
        }
    }
}
