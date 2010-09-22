package org.ogf.saga.impl.session;

import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.context.ContextImpl;

/**
 * Adaptor-specific session stuff. Each adaptor can add an adaptor session to
 * the SAGA session. Some of the methods of SAGA session are forwarded to all
 * registered adaptor sessions.
 */
public interface AdaptorSessionInterface {

    /**
     * Attaches a deep copy of the specified security context to the session.
     * 
     * @param contextImpl
     *            the context to be added.
     */
    public void addContext(ContextImpl contextImpl) throws NoSuccessException, TimeoutException;

    /**
     * Closes an adaptor session. Middleware may for instance have threads which
     * may need to be terminated, or the application will hang.
     */
    public void close();

    /**
     * Copies the adaptor session.
     * 
     * @return the clone.
     * @throws CloneNotSupportedException
     *             when the clone method is not supported.
     */
    public Object clone() throws CloneNotSupportedException;

    /**
     * Detaches the specified security context from the adaptor session.
     * 
     * @param contextImpl
     *            the context to be removed.
     */
    public void removeContext(ContextImpl contextImpl)
            throws DoesNotExistException;
}
