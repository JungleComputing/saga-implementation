package org.ogf.saga.impl;

import org.ogf.saga.impl.session.Session;

/**
 * Base class for all adaptors. Makes sure that all adaptors are
 * cloneable and have a public clone method.
 * Also, this is a place-holder for the wrapper class. The wrapper class is
 * needed for metrics and tasks (the Monitorable of a metric callback should
 * be the wrapper object, and the getObject() method of task should return
 * a reference to the wrapper object). The type of the wrapper class is
 * specified as a generic parameter.
 */
public abstract class AdaptorBase<T> implements Cloneable {
    
    /** The wrapper object. */
    protected T wrapper;
    
    /** The session of the adaptor. */
    protected Session session;
    
    /** 
     * Constructor with the specified wrapper.
     * @param wrapper the wrapper.
     */
    protected AdaptorBase(Session session, T wrapper) {
        this.session = session;
        this.wrapper = wrapper;
    }

    /**
     * Public clone method. Adaptors should redefine this method when they
     * have fields that must be cloned.
     * @return the clone.
     * @exception CloneNotSupportedException should never be thrown for Saga adaptors.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    /**
     * Sets the wrapper object. This method is used by the SAGA engine
     * to set the wrapper object to the wrapper clone. Note that the adaptors
     * themselves should never clone the wrapper object, because that would
     * give multiple copies of the wrapper object (one for each adaptor).
     * @param wrapper the wrapper clone.
     */
    public void setWrapper(T wrapper) {
        this.wrapper = wrapper;
    }
    
    public T getWrapper() {
        return wrapper;
    }
}
