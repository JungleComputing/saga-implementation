package org.ogf.saga.impl.monitoring;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;

/**
 * Base implementation of metrics.
 */
public class Metric extends SagaObjectBase implements org.ogf.saga.monitoring.Metric {
    
    protected static Logger logger = LoggerFactory.getLogger(Metric.class);
    
    /** A thread pool to execute callbacks. */
    private static ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            3L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(true));
      
    private static class CallbackHandler implements Runnable {
        boolean busy = false;
        private Monitorable monitorable;
        final Callback cb;
        private final Metric metric;
        final int cookie;
        Context context;
        
        public CallbackHandler(Monitorable monitorable, Callback cb,
                Metric metric, int cookie, Context context) {
            this.cb = cb;
            this.metric = metric;
            this.cookie = cookie;
            this.monitorable = monitorable;
            this.context = context;
        }
        
        public void setMonitorable(Monitorable monitorable) {
            this.monitorable = monitorable;
        }
        
        public void setContext(Context context) {
            this.context = context;
        }
        
        public void run() {        
            // Wait until a previous invocation of this cb
            // is finished, then set a flag that it is busy.
            synchronized(this) {
                while (busy) {
                    try {
                        wait();
                    } catch(Throwable e) {
                        // ignored
                    }
                }
                busy = true;
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking callback for metric " + metric);
            }
            boolean retval = true;
            try {
                retval = cb.cb(monitorable, metric, context);
            } catch(SagaException e) {
                synchronized(metric) {
                    metric.callbackExceptions.add(e);
                }
                logger.warn("Callback throws exception", e);
                // if callback throws an exception, keep the callback.
                retval = true;
            } finally {
                synchronized(metric) {
                    metric.fireCount--;
                    if (metric.fireCount == 0) {
                        metric.notifyAll();
                    }
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
    
    private final MetricAttributes attributes;
    private final ArrayList<CallbackHandler> callBacks;
    private Monitorable monitorable;    
    private int fireCount = 0;
    private final ArrayList<SagaException> callbackExceptions = new ArrayList<SagaException>();
    
    Metric(Session session, String name, String desc, String mode,
            String unit, String type, String value) throws NotImplementedException,
            BadParameterException {
        super(session);
        callBacks = new ArrayList<CallbackHandler>();
        attributes = new MetricAttributes();
        try {
            attributes.setValue(Metric.NAME, name);
            attributes.setValue(Metric.DESCRIPTION, desc);
            attributes.setValue(Metric.MODE, mode);
            attributes.setValue(Metric.TYPE, type);
            attributes.setValue(Metric.UNIT, unit);
            attributes.setValue(Metric.VALUE, value);
        } catch(IncorrectStateException e) {
            // Should not happen.
        } catch(DoesNotExistException e) {
            // Should not happen.
        }        
    }
    
    public Metric(Metric orig) {
        super(orig);
        attributes = new MetricAttributes(orig.attributes);
        this.monitorable = orig.monitorable;
        fireCount = 0;
        callBacks = new ArrayList<CallbackHandler>(orig.callBacks);
        for (int i = 0; i < callBacks.size(); i++) {
            CallbackHandler cb = callBacks.get(i);
            if (cb == null) {
                continue;
            }
            callBacks.set(i,
                    new CallbackHandler(monitorable, cb.cb, this, cb.cookie, cb.context));
        }
    }
       
    public Object clone() {
        return new Metric(this);
    }
      
    public Metric(Monitorable monitorable, Session session, String name, String desc,
            String mode, String unit, String type, String value)
            throws NotImplementedException, BadParameterException {
        this(session, name, desc, mode, unit, type, value);
        this.monitorable = monitorable;
    }
    
    Metric(String name, String desc,
            String mode, String unit, String type, String value)
            throws NotImplementedException, BadParameterException {
        this(null, name, desc, mode, unit, type, value);
    }
     
    public String toString() {
        return attributes.getValue(Metric.NAME);
    }
 
    public void setValue(String value) throws NotImplementedException , BadParameterException, IncorrectStateException, DoesNotExistException {
        attributes.setValue(Metric.VALUE, value);   
    }
    
    public void setMode(String value) throws NotImplementedException, BadParameterException, DoesNotExistException, IncorrectStateException {
        attributes.setValue(Metric.MODE, value);
    }
    
    // This method is to be called from addMetric() implementations.
    public synchronized void setMonitorable(Monitorable monitorable) {
        this.monitorable = monitorable;
    }

    public synchronized String[] findAttributes(String... patterns) throws NotImplementedException,
            BadParameterException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public synchronized String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public String[] getVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public boolean existsAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return attributes.existsAttribute(key);
    }    

    public boolean isReadOnlyAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public boolean isVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public boolean isWritableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.removeAttribute(key);

    }

    public void setAttribute(String key, String value) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, BadParameterException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);

    }

    public synchronized int addCallback(Callback cb) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        try {
            if ("Final".equals(attributes.getAttribute(Metric.MODE))) {
                throw new IncorrectStateException("Callback added on Final metric");
            }
        } catch(DoesNotExistException e) {
            // Should not happen.
        }
        CallbackHandler b = new CallbackHandler(monitorable, cb, this, callBacks.size(), null);
        callBacks.add(b);
        return callBacks.size()-1;
    }

    public void fire() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        try {
            if ("Final".equals(attributes.getAttribute(Metric.MODE))) {
                throw new IncorrectStateException("fire called on Final metric");
            }
            if (! "ReadWrite".equals(attributes.getAttribute(Metric.MODE))) {
                throw new PermissionDeniedException("file called on non-readwrite metric");
            }
        } catch(DoesNotExistException e) {
            // Should not happen.
        }
        internalFire();
    }
    
    public void internalFire() {
        ArrayList<CallbackHandler> cbhs;
        
        synchronized(this) {
            while (fireCount != 0) {
                try {
                    wait();
                } catch(Throwable e) {
                    // ignored
                }
            }
            callbackExceptions.clear();
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
        
        // Wait until all callbacks are done.
        synchronized(this) {
            while (fireCount != 0) {
                try {
                    wait();
                } catch(Throwable e) {
                    // ignored
                }
            }
        }
    }

    public synchronized void setCallbackContext(Context context) {
        
        while (fireCount != 0) {
            try {
                wait();
            } catch(Throwable e) {
                // ignored
            }
        }
        for (CallbackHandler cbh : callBacks) {
            if (cbh != null) {
                cbh.setContext(context);
            }
        }
    }

    public void removeCallback(int cookie) throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        
        CallbackHandler cb;
        
        synchronized(this) {
            if (cookie >= callBacks.size()) {
                throw new BadParameterException("removeCallback with invalid cookie: " + cookie);
            }

            cb = callBacks.get(cookie);
            if (cb == null) {
                // This callback was already removed. Ignore.
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
                } catch(Throwable e) {
                    // ignored
                }
            }
        }
    }

}
