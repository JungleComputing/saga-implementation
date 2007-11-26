package org.ogf.saga.impl.monitoring;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import org.ogf.saga.ObjectType;
import org.ogf.saga.attributes.Attributes;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;
import org.ogf.saga.impl.SagaObjectBase;

public class Metric extends SagaObjectBase implements Attributes,
        org.ogf.saga.monitoring.Metric {
    
    protected static Logger logger = Logger.getLogger(Metric.class);
    
    private static ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            10L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    
    private int fireCount = 0;
    
    private class CallbackHandler implements Runnable {
        boolean busy = false;
        private Callback cb;
        private Metric metric;
        private int cookie;
        
        public CallbackHandler(Callback cb, Metric metric, int cookie) {
            this.cb = cb;
            this.metric = metric;
            this.cookie = cookie;
        }
        
        public void run() {        
            // Wait until a previous invocation of this cb
            // is finished, then set a flag that it is busy.
            synchronized(this) {
                while (busy) {
                    try {
                        wait();
                    } catch(Exception e) {
                        // ignored
                    }
                }
                busy = true;
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking callback for metric " + metric);
            }
            boolean retval;
            try {
                retval = cb.cb(monitorable, metric, null);
            } catch(Throwable e) {
                logger.warn("Callback throws exception", e);
                // if callback throws an exception, keep the callback.
                retval = true;
            }
            synchronized(metric) {
                metric.fireCount--;
                if (metric.fireCount == 0) {
                    metric.notifyAll();
                }
            }
            if (! retval) {
                try {
                    metric.removeCallback(cookie);
                } catch(Throwable e) {
                    // ignored
                }
            }
            
            // Release busy flag, notify waiters.
            synchronized(this) {
                busy = false;
                notifyAll();
            }
        }
    };
    
    private final MetricAttributes attributes = new MetricAttributes();
    private final ArrayList<CallbackHandler> callBacks = new ArrayList<CallbackHandler>();
    private Monitorable monitorable;
     
    Metric(Session session, String name, String desc, String mode,
            String unit, String type, String value) throws NotImplemented,
            BadParameter {
        super(session);
        try {
            attributes.setValue(Metric.NAME, name);
            attributes.setValue(Metric.DESCRIPTION, desc);
            attributes.setValue(Metric.MODE, mode);
            attributes.setValue(Metric.TYPE, type);
            attributes.setValue(Metric.UNIT, unit);
            attributes.setValue(Metric.VALUE, value);
        } catch(IncorrectState e) {
            // Should not happen.
        } catch(DoesNotExist e) {
            // Should not happen.
        }        
    }
      
    public Metric(Monitorable monitorable, Session session, String name, String desc,
            String mode, String unit, String type, String value)
            throws NotImplemented, BadParameter {
        this(session, name, desc, mode, unit, type, value);
        this.monitorable = monitorable;
    }
    
    Metric(String name, String desc,
            String mode, String unit, String type, String value)
            throws NotImplemented, BadParameter {
        this(null, name, desc, mode, unit, type, value);
    }
     
    public String toString() {
        return attributes.getValue(Metric.NAME);
    }
 
    protected void setValue(String value) throws NotImplemented , BadParameter, IncorrectState, DoesNotExist {
        attributes.setValue(Metric.VALUE, value);   
    }
    
    protected void setMode(String value) throws NotImplemented, BadParameter, DoesNotExist, IncorrectState {
        attributes.setValue(Metric.MODE, value);
    }
    
    // This method is to be called from addMetric() implementations.
    public synchronized void setMonitorable(Session session, Monitorable monitorable) {
        this.monitorable = monitorable;
    }

    @Override
    public ObjectType getType() {
        return ObjectType.METRIC;
    }

    public synchronized String[] findAttributes(String... patterns) throws NotImplemented,
            BadParameter, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, Timeout, NoSuccess {
        return attributes.findAttributes(patterns);
    }

    public synchronized String getAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getAttribute(key);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getVectorAttribute(key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isReadOnlyAttribute(key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isRemovableAttribute(key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isVectorAttribute(key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isWritableAttribute(key);
    }

    public String[] listAttributes() throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return attributes.listAttributes();
    }

    public void removeAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        attributes.removeAttribute(key);

    }

    public void setAttribute(String key, String value) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        attributes.setAttribute(key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, DoesNotExist,
            Timeout, NoSuccess {
        attributes.setVectorAttribute(key, values);

    }

    public synchronized int addCallback(Callback cb) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, Timeout, NoSuccess {
        try {
            if ("Final".equals(attributes.getAttribute(Metric.MODE))) {
                throw new IncorrectState("Callback added on Final metric");
            }
        } catch(DoesNotExist e) {
            // Should not happen.
        }
        CallbackHandler b = new CallbackHandler(cb, this, callBacks.size());
        callBacks.add(b);
        return callBacks.size()-1;
    }

    public void fire() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, IncorrectState, Timeout,
            NoSuccess {
        try {
            if ("Final".equals(attributes.getAttribute(Metric.MODE))) {
                throw new IncorrectState("fire called on Final metric");
            }
            if (! "ReadWrite".equals(attributes.getAttribute(Metric.MODE))) {
                throw new PermissionDenied("file called on non-readwrite metric");
            }
        } catch(DoesNotExist e) {
            // Should not happen.
        }
        internalFire();
    }
    
    protected void internalFire() {
        ArrayList<CallbackHandler> cbhs;
        
        synchronized(this) {
            while (fireCount != 0) {
                try {
                    wait();
                } catch(Exception e) {
                    // ignored
                }
            }
            cbhs = new ArrayList<CallbackHandler>();
            for (CallbackHandler cbh : callBacks) {
                if (cbh != null) {
                    cbhs.add(cbh);
                }
            }
            fireCount = cbhs.size();
        }
        
        synchronized(executor) {            
            for (CallbackHandler cbh : cbhs) {
                executor.submit(cbh);
            }
        }
        
        // TODO: Should we wait here? I think yes.
        while (fireCount != 0) {
            try {
                wait();
            } catch(Exception e) {
                // ignored
            }
        }
    }
  
    public void removeCallback(int cookie) throws NotImplemented, BadParameter,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        
        CallbackHandler cb;
        
        synchronized(this) {
            if (cookie >= callBacks.size()) {
                throw new BadParameter("removeCallback with invalid cookie: " + cookie);
            }

            cb = callBacks.get(cookie);
            if (cb == null) {
                return;
            }
            callBacks.set(cookie, null);
        }

        // The SAGA specs prescribe that we block here until no
        // activation of the removed method is active.
        synchronized(cb) {
            while (cb.busy) {
                try {
                    cb.wait();
                } catch(Exception e) {
                    // ignored
                }
            }
        }
    }

}
