package org.ogf.saga.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Information container for a specific adaptor.
 */
class Adaptor {
    /** The class of the api this adaptor implements. */
    private Class<?> spiClass;

    /** The actual class of this adaptor, must be a subclass of spiClass. */
    private Class<?> adaptorClass;
    
    private String shortSpiName = null;
    
    private String shortAdaptorClassName;
    
    /**
     * @param spiClass
     *            The class of the api this adaptor implements.
     * @param adaptorClass
     *            The actual class of this adaptor, must be a subclass of
     *            spiClass.
     */
    Adaptor(Class<?> spiClass, Class<?> adaptorClass) {
        this.spiClass = spiClass;
        this.adaptorClass = adaptorClass;
    }
    
    /**
     * Creates an instance of the adaptor, and returns it.
     * @param parameters the constructor parameters.
     * @return the adaptor instance.
     * @exception Throwable anything that the constructor throws, or an
     *     error indicating that no suitable constructor was found.
     */
    Object instantiate(Object[] parameters)
            throws Throwable {
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i].getClass();
        }
        try {
            // TODO: this may be too strict. The parameters could be of
            // a subclass of the type specified in the constructor.
            Constructor<?> ctor = adaptorClass.getConstructor(parameterTypes);

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

    String getSpi() {
        return spiClass.getName();
    }

    Class<?> getSpiClass() {
        return spiClass;
    }

    String getName() {
        return adaptorClass.getName();
    }

    Class<?> getAdaptorClass() {
        return adaptorClass;
    }
    
    public String toString() {
        return getName();
    }

    synchronized String getShortSpiName() {
        if (shortSpiName == null) {
            shortSpiName = spiClass.getName();
            int index = shortSpiName.lastIndexOf(".");
            if(index > 0) {
                shortSpiName = shortSpiName.substring(index+1);
            }

            // clip of the "Spi"
            shortSpiName = shortSpiName.substring(0, shortSpiName.length()-3);
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
