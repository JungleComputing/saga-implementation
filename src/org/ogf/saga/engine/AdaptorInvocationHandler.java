package org.ogf.saga.engine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

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
    
    AdaptorInvocationHandler(AdaptorList adaptors, Object[] params) {

        if (adaptors.size() == 0) {
            throw new Error("no adaptors could be loaded for this object");
        }

        for (Adaptor adaptor : adaptors) {
            String adaptorname = adaptor.getName();

            try {
                Object object = initAdaptor(adaptor, params);
                this.adaptorInstantiations.put(adaptorname, object);
                this.adaptors.put(adaptorname, adaptor);
                adaptorSorter.add(adaptorname);
            } catch (Throwable t) {
                // For now, ignored
            }
        }

        if (this.adaptors.size() == 0) {
            throw new Error("no adaptors could be successfully instantiated");
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

                    if (logger.isDebugEnabled()) {
                        logger.debug("Method " + m.getName() + " on "
                                + adaptor.getShortAdaptorClassName()
                                + " failed: " + t, t);
                    }
                }
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("invoke: No adaptor could be invoked.");
        }

        throw new Error("All adaptors failed");
    }

    /**
     * Instantiates an instance of the specified XXXSpi class consistent with the
     * passed parameters.
     * 
     * @param adaptor
     *            The adaptor to initialize.
     * @param parameters
     *            The parameters for the Spi Constructor.
     * @return the instance.
     */
    private Object initAdaptor(Adaptor adaptor, Object... parameters) {
        
        Object result;

        if (logger.isDebugEnabled()) {
            logger.debug("initAdaptor: trying to instantiate "
                    + adaptor.getShortAdaptorClassName() + " for type "
                    + adaptor.getShortSpiName());
        }
        
        try {
            result = adaptor.instantiate(parameters);
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("initAdaptor: Couldn't create "
                        + adaptor.getShortAdaptorClassName() + ": "
                        + t.getMessage(), t);
            }

            throw new Error("Could not create adaptor", t);
        }

        if (logger.isInfoEnabled()) {
            logger.info("initAdaptor: instantiated "
                    + adaptor.getShortAdaptorClassName() + " for type "
                    + adaptor.getShortSpiName());
        }
        return result;
    }
}
