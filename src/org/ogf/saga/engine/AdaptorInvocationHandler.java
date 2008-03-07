package org.ogf.saga.engine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.impl.AdaptorBase;

/**
 * Takes care of method invocations.
 * Mostly stolen from the javaGAT engine implementation.
 */
public class AdaptorInvocationHandler implements InvocationHandler {

    protected static Logger logger = Logger
        .getLogger(AdaptorInvocationHandler.class);

    /**
     *  Maintains lists of adaptors per method.
     */
    private static class AdaptorSorter {
        /**
         * List of adaptor class names (Strings) in order of successful
         * execution
         */
        private LinkedList<String> adaptorlist = new LinkedList<String>();

        /**
         * List of adaptor class names (Strings) in order of successful
         * execution per method <methodName, LinkedList>
         */
        private HashMap<Method, ArrayList<String>> adaptorMethodList
                = new HashMap<Method, ArrayList<String>>();

        synchronized void add(String adaptorName) {
            if (!adaptorlist.contains(adaptorName)) {
                adaptorlist.add(adaptorName);
            }
        }

        synchronized ArrayList<String> getOrdering(Method method) {
            ArrayList<String> l = adaptorMethodList.get(method);

            if (l == null) {
                return new ArrayList<String>(adaptorlist);
            }

            // We have a list for this particular method. Use that order
            // first, and append the other adaptorInstantiations at the end (in
            // order).
            ArrayList<String> result = new ArrayList<String>(l);

            for (String s : adaptorlist) {
                if (!result.contains(s)) {
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

    private static final boolean OPTIMIZE_ADAPTOR_POLICY = true;

    private static AdaptorSorter adaptorSorter = new AdaptorSorter();

    /**
     * The available adaptors. The keys are class names, the elements are
     * of type Adaptor.
     */
    private Hashtable<String, Adaptor> adaptors
            = new Hashtable<String, Adaptor>();
    
    /**
     * The available adaptor instantiations. The keys, again, are class names, the
     * elements are instantiations.
     */
    private Hashtable<String, Object> adaptorInstantiations
        = new Hashtable<String, Object>();
    
    AdaptorInvocationHandler(AdaptorList adaptors, Class[] types, Object[] params)
            throws org.ogf.saga.error.SagaException {

        if (adaptors == null || adaptors.size() == 0) {
            throw new NoSuccessException("no adaptors could be loaded for this object");
        }
        
        org.ogf.saga.error.SagaException exception = null; 

        for (Adaptor adaptor : adaptors) {
            String adaptorname = adaptor.getAdaptorName();

            try {
                Object object = initAdaptor(adaptor, types, params);
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
                if (t instanceof org.ogf.saga.error.SagaException) {
                    org.ogf.saga.error.SagaException e = (org.ogf.saga.error.SagaException) t;
                    if (exception == null || e.compareTo(exception) < 0) {
                        exception = e;
                    }
                } else if (exception == null) {
                    exception = new NoSuccessException("Got exception from constructor of "
                            + adaptor.getShortAdaptorClassName(), t);
                }
            }
        }

        if (this.adaptors.size() == 0) {
            throw exception;
        }
    }
    
    @SuppressWarnings("unchecked")
    public AdaptorInvocationHandler(AdaptorInvocationHandler orig, Object wrapper) {
        adaptors = new Hashtable<String, Adaptor>(adaptors);
        adaptorInstantiations = new Hashtable<String, Object>();
        for (String s : orig.adaptorInstantiations.keySet()) {
            try {
                AdaptorBase cp = (AdaptorBase)
                        ((AdaptorBase) orig.adaptorInstantiations.get(s)).clone();
                // This invocation gives a warning if warnings are not suppressed.
                // And rightly so. But this should be correct.
                // TODO: can we improve on this ???
                cp.setWrapper(wrapper);
                adaptorInstantiations.put(s, cp);
            } catch (CloneNotSupportedException e) {
                logger.error("Adaptor " + orig.adaptorInstantiations.get(s)
                        + " does not support clone()", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method m, Object[] params)
        throws Throwable {
        
        org.ogf.saga.error.SagaException exception = null;
        
        ArrayList<String> adaptornames = adaptorSorter.getOrdering(m);
        boolean first = true;

        // try adaptorInstantiations in order of success
        for (String adaptorName : adaptornames) {
            // only try adaptorInstantiations available for this handler
            if (adaptorInstantiations.containsKey(adaptorName)) {
                Adaptor adaptor = adaptors.get(adaptorName);
                Object adaptorInstantiation = adaptorInstantiations.get(adaptorName);
                try {
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
                    if (t instanceof org.ogf.saga.error.SagaException) {
                        org.ogf.saga.error.SagaException e = (org.ogf.saga.error.SagaException) t;
                        if (exception == null || e.compareTo(exception) < 0) {
                            exception = e;
                        }
                    } else if (exception == null) {
                        exception = new NoSuccessException("Got exception from " + m.getName()
                                + " on " + adaptor.getShortAdaptorClassName(), t);
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("Method " + m.getName() + " on "
                                + adaptor.getShortAdaptorClassName()
                                + " failed: " + t, t);
                    }
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

        // throw the most specific exception.
        throw exception;
    }

    /**
     * Creates an instance of the specified adaptor consistent with the
     * passed parameters.
     * 
     * @param adaptor
     *            The adaptor to instantiate.
     * @param types
     *            The parameter types of the Spi constructor.
     * @param parameters
     *            The actual parameters for the Spi Constructor.
     * @return the instance.
     */
    private Object initAdaptor(Adaptor adaptor, Class<?>[] types, Object... parameters)
            throws Throwable {
        
        Object result;

        if (logger.isDebugEnabled()) {
            logger.debug("initAdaptor: trying to instantiate "
                    + adaptor.getShortAdaptorClassName() + " for type "
                    + adaptor.getSpiName());
        }
        
        result = adaptor.instantiate(types, parameters);
 
        if (logger.isInfoEnabled()) {
            logger.info("initAdaptor: instantiated "
                    + adaptor.getShortAdaptorClassName() + " for type "
                    + adaptor.getSpiName());
        }
        return result;
    }
}
