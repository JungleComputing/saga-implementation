package org.ogf.saga.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.ogf.saga.impl.AdaptorBase;

/**
 * Information container for a specific adaptor.
 */
class Adaptor {

    /** The actual class of this adaptor, must be an implementation of spiClass. */
    final Class<?> adaptorClass;
    
    /**
     * Constructs an information container for a specific adaptor.
     * @param adaptorClass
     *            The actual class of this adaptor, must be an implementation of
     *            spiClass.
     */
    Adaptor(Class<?> adaptorClass) {
        this.adaptorClass = adaptorClass;
    }
    
    /**
     * Creates an instance of the adaptor, and returns it.
     * @param types the constructor parameter types.
     * @param parameters the actual constructor parameters.
     * @return the adaptor instance.
     * @exception Throwable anything that the constructor throws, or an
     *     error indicating that no suitable constructor was found.
     */
    AdaptorBase instantiate(Class<?>[] types, Object[] parameters)
            throws Throwable {
        // Set context classloader before calling constructor.
        // Some adaptors may need this because some libraries explicitly
        // use the context classloader. (jaxrpc).
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
                adaptorClass.getClassLoader());
        try {
            // Use the specified types to find the constructor that we want.
            Constructor<?> ctor = adaptorClass.getConstructor(types);

            if (ctor == null) {
                throw new Error("No correct contructor exists in adaptor"
                        + adaptorClass.getName());
            }
            return (AdaptorBase) ctor.newInstance(parameters);
        } catch (InvocationTargetException e) {
            // rethrow original exception
            throw e.getTargetException();
        } finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }

    /**
     * Obtains the name of the adaptor class.
     * @return the adaptor class name.
     */
    String getAdaptorName() {
        return adaptorClass.getName();
    }

    /**
     * Obtains the adaptor class.
     * @return the adaptor class.
     */
    Class<?> getAdaptorClass() {
        return adaptorClass;
    }
    
    /**
     * Returns the name of the adaptor class.
     * @return the name.
     */
    public String toString() {
        return adaptorClass.getName();
    }

    /**
     * Obtains the short name of the adaptor class (without package).
     * @return the short name of the adaptor class.
     */
    synchronized String getShortAdaptorClassName() {
        return adaptorClass.getSimpleName();
    }
}
