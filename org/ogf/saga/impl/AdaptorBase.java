package org.ogf.saga.impl;

/**
 * Base class for all adaptors. Makes sure that all adaptors are
 * cloneable and have a public clone method.
 */
public abstract class AdaptorBase implements Cloneable {

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
