package org.ogf.saga.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Information container for a specific adaptor.
 */
class Adaptor {
    /** The interface of the api implemented by this adaptor. */
    private Class<?> spiInterface;

    /** The actual class of this adaptor, must be an implementation of spiClass. */
    private Class<?> adaptorClass;
    
    private String shortSpiName = null;
    
    private String shortAdaptorClassName = null;
    
    /**
     * @param spiInterface
     *            The interface of the api this adaptor implements.
     * @param adaptorClass
     *            The actual class of this adaptor, must be an implementation of
     *            spiClass.
     */
    Adaptor(Class<?> spiInterface, Class<?> adaptorClass) {
        this.spiInterface = spiInterface;
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
    Object instantiate(Class<?>[] types, Object[] parameters)
            throws Throwable {
        try {
            // Use the specified types to find the constructor that we want.
            Constructor<?> ctor = adaptorClass.getConstructor(types);

            if (ctor == null) {
                throw new Error("No correct contructor exists in adaptor"
                        + adaptorClass.getName());
            }
            return ctor.newInstance(parameters);
        } catch (InvocationTargetException e) {
            // rethrow original exception
            throw e.getTargetException();
        }
    }

    String getAdaptorName() {
        return adaptorClass.getName();
    }

    Class<?> getAdaptorClass() {
        return adaptorClass;
    }
    
    public String toString() {
        return adaptorClass.getName();
    }

    synchronized String getShortSpiName() {
        if (shortSpiName == null) {
            shortSpiName = spiInterface.getName();
            int index = shortSpiName.lastIndexOf(".");
            if(index > 0) {
                shortSpiName = shortSpiName.substring(index+1);
            }

            // clip of the "SpiInterface"
            shortSpiName = shortSpiName.substring(0, shortSpiName.length()-12);
        }
        return shortSpiName;
    }

    synchronized String getShortAdaptorClassName() {
        if (shortAdaptorClassName == null) {
            shortAdaptorClassName = adaptorClass.getName();
            int index = shortAdaptorClassName.lastIndexOf(".");
            if(index > 0) {
                shortAdaptorClassName = shortAdaptorClassName.substring(index+1);
            }
        }
        return shortAdaptorClassName;
    }
}
