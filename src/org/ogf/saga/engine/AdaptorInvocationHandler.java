package org.ogf.saga.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ogf.saga.SagaObject;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.AdaptorBase;

/**
 * This class takes care of forwarding method invocations to the
 * adaptors. It maintains adaptor lists for each method, with
 * succesful invocations in front.
 * Note that each Saga object that has associated adaptors has
 * its own instance of an <code>AdaptorInvocationHandler</code>.
 */
public class AdaptorInvocationHandler implements InvocationHandler {
 
    /**
     * Order of importance in exceptions, from high to low. If all adaptors give an
     * exception on a method invocation, this list determines which exception is actually
     * passed on to the user.
     * Note, this is NOT the order mentioned in the SAGA specs! That one determines which
     * exception an adaptor method should throw. But, when an implementation tries several
     * adaptors for a method, and one adaptor gives NotImplemented and another gives
     * DoesNotExist, to the user, the latter is more specific. So, an attempt is made
     * here to determine an order to use in a Saga implementation that uses multiple
     * adaptors.
     */
    private static Class<?>[] exceptionClasses = {
        AlreadyExistsException.class,
        DoesNotExistException.class,
        SagaIOException.class,
        IncorrectStateException.class,
        TimeoutException.class,
        AuthenticationFailedException.class,
        AuthorizationFailedException.class,
        PermissionDeniedException.class,
        BadParameterException.class,
        IncorrectURLException.class,
        NoSuccessException.class,
        NotImplementedException.class,
    };

    /**
     * Returns the most important exception of the two parameters.
     * @param e1 exception 1.
     * @param e2 exception 2.
     * @return the most important exception.
     */
    private static SagaException compare(SagaException e1, SagaException e2) {
        for (Class<?> c : exceptionClasses) {
            if (c.isInstance(e1)) {
                return e1;
            }
            if (c.isInstance(e2)) {
                return e2;
            }
        }
        // Something wrong? O well ...
        logger.debug("Got exceptions not present in list!");
        return e1;
    }
    
    /** Logger. */
    private static Logger logger = LoggerFactory
        .getLogger(AdaptorInvocationHandler.class);

    /**
     *  Maintains lists of adaptors per method. The lists are
     *  self-organizing: succesful adaptors are placed in front.
     */
    private static class AdaptorSorter {
        /**
         * List of adaptor class names (Strings) in order of successful
         * execution
         */
        private ArrayList<String> adaptorlist = new ArrayList<String>();

        /**
         * List of adaptor class names (Strings) in order of successful
         * execution per method <methodName, LinkedList>.
         */
        private HashMap<Method, ArrayList<String>> adaptorMethodList
                = new HashMap<Method, ArrayList<String>>();

        /**
         * Adds the specified adaptor name to the adaptor list.
         * @param adaptorName the adaptor name to add.
         */
        synchronized void add(String adaptorName) {
            if (!adaptorlist.contains(adaptorName)) {
                adaptorlist.add(adaptorName);
            }
        }

        /**
         * Returns the current adaptor ordering for the specified method.
         * @param method the method that is going to be invoked.
         * @return the list of adaptor names to try, in that order.
         */
        synchronized ArrayList<String> getOrdering(Method method) {
            ArrayList<String> l = adaptorMethodList.get(method);

            if (l == null) {
                return new ArrayList<String>(adaptorlist);
            }

            // We have a list for this particular method. Use that order
            // first, and append the other adaptors at the end (in
            // order).
            ArrayList<String> result = new ArrayList<String>(l);

            for (String s : adaptorlist) {
                if (!l.contains(s)) {
                    result.add(s);
                }
            }
            return result;
        }

        /**
         * The specified adaptor was successful for the specified method,
         * so place it in front of the adaptor list specific for this method. 
         * @param adaptorName the successful adaptor.
         * @param method the method.
         */
        synchronized void success(String adaptorName, Method method) {
            ArrayList<String> l = adaptorMethodList.get(method);

            if (l == null) {
                l = new ArrayList<String>();
                adaptorMethodList.put(method, l);
            } else {
                l.remove(adaptorName);
            }

            l.add(0, adaptorName);
        }
    }

    /** Re-organize adaptor lists per method. When a method succeeds, it is put in front. */
    private static final boolean OPTIMIZE_ADAPTOR_POLICY = true;

    /** Maintains lists of adaptors per method. */
    private static AdaptorSorter adaptorSorter = new AdaptorSorter();

    /**
     * The available adaptors. The keys are class names, the elements are
     * of type Adaptor.
     */
    private Hashtable<String, Adaptor> adaptors
            = new Hashtable<String, Adaptor>();
    
    /**
     * The available adaptor instantiations. The keys, again, are class names,
     * the elements are instantiations.
     */
    private Hashtable<String, AdaptorBase<?>> adaptorInstantiations
            = new Hashtable<String, AdaptorBase<?>>();
    
    /**
     * Constructs an <code>AdaptorInvocationHandler</code>, attempting to
     * instantiate adaptors with the specified parameters on the fly.
     * @param adaptors the list of adaptor names.
     * @param types the types of the constructor parameters.
     * @param params the constructor parameters.
     * @throws SagaException is thrown when no adaptors could be instantiated
     *    for some reason. Actually, the most specific exception thrown by
     *    any of the constructors is thrown.
     */
    AdaptorInvocationHandler(AdaptorList adaptors, Class<?>[] types,
            Object[] params) throws SagaException {

        // Do we have any adaptors?
        if (adaptors == null || adaptors.size() == 0) {
            throw new NoSuccessException("no adaptors could be loaded for this object");
        }
        
        SagaException exception = null; 
        NestedException nested = null;
        boolean multiple = false;

        for (Adaptor adaptor : adaptors) {
            String adaptorname = adaptor.getAdaptorName();

            try {
                // instantiate the adaptor.
                if (logger.isDebugEnabled()) {
                    logger.debug("initAdaptor: trying to instantiate "
                            + adaptor.getShortAdaptorClassName() + " for type "
                            + adaptors.getSpiName());
                }
                
                AdaptorBase<?> object = adaptor.instantiate(types, params);
         
                if (logger.isInfoEnabled()) {
                    logger.info("initAdaptor: instantiated "
                            + adaptor.getShortAdaptorClassName() + " for type "
                            + adaptors.getSpiName());
                }
                this.adaptorInstantiations.put(adaptorname, object);
                this.adaptors.put(adaptorname, adaptor);
                adaptorSorter.add(adaptorname);
            } catch (Throwable t) {
                logger.debug("Instantiation of " + adaptorname + " failed", t);

                while (t instanceof InvocationTargetException) {
                    t = ((InvocationTargetException) t)
                        .getTargetException();
                }
                
                // keep the most specific exception around. We can throw that
                // when all adaptors fail.
                if (t instanceof SagaException) {
                    SagaException e = (SagaException) t;
                    if (nested == null) {
                        nested = new NestedException();
                    }
                    nested.add(adaptors.getSpiName(), e);
                    if (exception == null) {
                        exception = e;
                    } else {
                        multiple = true;
                        exception = compare(exception, e);
                    }
                } else if (exception == null) {
                    exception = new NoSuccessException("Got exception from constructor of "
                            + adaptorname, t);
                    if (nested == null) {
                        nested = new NestedException();
                    }
                    nested.add(adaptors.getSpiName(), exception);
                }
            }
        }

        if (this.adaptors.size() == 0) {
            if (multiple) {
                try {
                   Constructor<?> c = exception.getClass().getConstructor(String.class, Throwable.class);
                   SagaException ex = (SagaException) c.newInstance(exception.getMessage(), nested);
                   ex.setStackTrace(exception.getStackTrace());
                   exception = ex;
                } catch(Throwable e) {
                    // O well, we tried ...
                    logger.debug("Creation of nested exception failed");
                }
                
            }
            throw exception;
        }
    }
    
    /**
     * This is a copying constructor, used in case a clone() was invoked
     * on a Saga object. In that case, the adaptors associated with the
     * Saga object must be cloned as well. On the other hand, the adaptors
     * have a reference to the Saga object (the wrapper), and the adaptor
     * clones must now refer to the clone of the wrapper (they cannot call
     * clone() on the wrapper object!).
     * @param orig the adaptor invocation handler to copy.
     * @param wrapper the clone of the wrapper object.
     */
    @SuppressWarnings("unchecked")
    public AdaptorInvocationHandler(AdaptorInvocationHandler orig,
            Object wrapper) {
        
        adaptors = new Hashtable<String, Adaptor>(adaptors);
        
        for (String s : orig.adaptorInstantiations.keySet()) {
            try {
                AdaptorBase cp = (AdaptorBase)
                        orig.adaptorInstantiations.get(s).clone();
                // This next invocation gives a compiler warning if warnings are
                // not suppressed, and rightly so.
                // But this should be correct.
                // TODO: can we improve on this and avoid the warning ???
                cp.setWrapper(wrapper);
                adaptorInstantiations.put(s, cp);
            } catch (CloneNotSupportedException e) {
                logger.error("Adaptor " + orig.adaptorInstantiations.get(s)
                        + " does not support clone()", e);
            }
        }
    }
    
    /**
     * Adds a wrapper object to the exception, in case the adaptor or could'nt for some
     * other reason. This is what makes SAGAException.getObject() work.
     * @param exception the exception
     * @param base the adaptor base
     * @return the modified exception.
     */
    private SagaException addWrapper(SagaException exception, AdaptorBase<?> base) {
        Object sagaObject = null;
        try {
            sagaObject = exception.getObject();
        } catch(Throwable e) {
            // ignored
        }
        if (sagaObject != null) {
            return exception;
        }
        sagaObject = base.getWrapper();
        SagaException ex = exception;
        try {
            if (exception instanceof SagaIOException) {
                Constructor<?> c = exception.getClass().getConstructor(String.class,
                        Throwable.class, Integer.TYPE, SagaObject.class);

                ex = (SagaException) c.newInstance(exception.getMessage(), exception.getCause(),
                        ((SagaIOException) exception).getPosixErrorCode(),
                        (SagaObject) sagaObject);
            } else {
                Constructor<?> c = exception.getClass().getConstructor(String.class,
                        Throwable.class, SagaObject.class);

                ex = (SagaException) c.newInstance(exception.getMessage(), exception.getCause(),
                        (SagaObject) sagaObject);
            }
            ex.setStackTrace(exception.getStackTrace());
            exception = ex;
        } catch(Throwable e) {
            // o well, we tried ...
        }
        return exception;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method m, Object[] params)
        throws Throwable {
        
        SagaException exception = null;
        NestedException nested = null;
        boolean multiple = false;
        
        ArrayList<String> adaptornames = adaptorSorter.getOrdering(m);
        boolean first = true;

        // Try adaptorInstantiations in order of earlier success.
        for (String adaptorName : adaptornames) {
            // Only try adaptorInstantiations available for this handler
            if (adaptorInstantiations.containsKey(adaptorName)) {
                Adaptor adaptor = adaptors.get(adaptorName);
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                AdaptorBase<?> adaptorInstantiation = adaptorInstantiations.get(adaptorName);
                try {
                    // Set context classloader before invoking method.
                    // Some adaptors may need this because some libraries
                    // explicitly use the context classloader. (jaxrpc).
                    Thread.currentThread().setContextClassLoader(
                            adaptor.adaptorClass.getClassLoader());
                    if (logger.isDebugEnabled()) {
                        logger.debug("invocation of method " + m.getName()
                                + " on " + adaptor.getShortAdaptorClassName()
                                + " START");
                    }

                    // now invoke the method on the adaptor
                    Object res = m.invoke(adaptorInstantiation, params);

                    if (logger.isDebugEnabled()) {
                        logger.debug("invocation of method " + m.getName()
                                + " on " + adaptor.getShortAdaptorClassName()
                                + " DONE");
                    }

                    if (OPTIMIZE_ADAPTOR_POLICY && ! first) {
                        // move successful adaptor to start of list
                        adaptorSorter.success(adaptorName, m);
                    }

                    return res; // return on first successful adaptor
                } catch (Throwable t) {
                    first = false;
                    while (t instanceof InvocationTargetException) {
                        t = ((InvocationTargetException) t)
                            .getTargetException();
                    }
                    // keep the most specific exception around. We can throw that
                    // when all adaptors fail.
                    if (t instanceof SagaException) {
                        SagaException e = (SagaException) t;
                        addWrapper(e, adaptorInstantiation);
                        if (nested == null) {
                            nested = new NestedException();
                        }
                        nested.add(adaptorName, e);
                        if (exception == null) {
                            exception = e;
                        } else {
                            multiple = true;
                            exception = compare(exception, e);
                        }
                    } else if (exception == null) {
                        exception = new NoSuccessException("Got exception from method "
                                + m.getName(), t,
                                (SagaObject) adaptorInstantiation.getWrapper());
                        nested.add(adaptorName, exception);
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("Method " + m.getName() + " on "
                                + adaptor.getShortAdaptorClassName()
                                + " failed: " + t, t);
                    }                    
                } finally {
                    Thread.currentThread().setContextClassLoader(loader);
                }
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("invoke: All adaptors failed");
        }
        
        if (exception == null) {
            // Can this happen??? I don't think so.
            throw new NoSuccessException("no adaptor found for " + m.getName());
        }
        
        if (multiple) {
            try {
                Object sagaObject = null;
                try {
                    sagaObject = exception.getObject();
                } catch(Throwable e) {
                    // ignored
                }
                SagaException ex = null;
                if (exception instanceof SagaIOException) {
                    Constructor<?> c = exception.getClass().getConstructor(String.class,
                            Throwable.class, Integer.TYPE, SagaObject.class);

                    ex = (SagaException) c.newInstance(exception.getMessage(), nested,
                            ((SagaIOException) exception).getPosixErrorCode(), sagaObject);
                } else {
                    Constructor<?> c = exception.getClass().getConstructor(String.class,
                            Throwable.class, SagaObject.class);

                    ex = (SagaException) c.newInstance(exception.getMessage(), nested,
                            sagaObject);
                }
                ex.setStackTrace(exception.getStackTrace());
                exception = ex;
            } catch(Throwable e) {
                // O well, we tried ...
                logger.debug("Creation of nested exception failed");
            }
            
        }
        throw exception;
    }
}
