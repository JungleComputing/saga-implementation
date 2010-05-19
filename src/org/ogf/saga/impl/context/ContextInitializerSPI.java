package org.ogf.saga.impl.context;

import org.ogf.saga.error.NotImplementedException;

/**
 * An initializer interface, to be used by adaptors that require specific
 * default values for context attributes. The initializer method of this
 * interface is used to initialize a context.
 */
public interface ContextInitializerSPI {
    /**
     * Initializes the specified context with "sensible" default values.
     * 
     * @param context
     *            the context to initialize.
     * @param type
     *            the value of the TYPE attribute of this context.
     * @throws NotImplementedException
     *             is thrown when this adaptor does not recognize the context
     *             name.
     */
    public void setDefaults(ContextImpl context, String type)
            throws NotImplementedException;
}
