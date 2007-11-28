package org.ogf.saga.impl.session;

import org.ogf.saga.impl.context.Context;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.NotImplemented;

/**
 * Adaptor-specific session stuff. Each adaptor can add an adaptor session
 * to the SAGA session. Some of the methods of SAGA session are forwarded
 * to all registered adaptor sessions. 
 */
public interface AdaptorSessionInterface {  
    
    /**
     * Attaches a deep copy of the specified security context to the session.
     * @param context the context to be added.
     */
    public void addContext(Context context) throws NotImplemented;
    
    /**
     * Closes an adaptor session. Middleware may for instance have threads
     * which may need to be terminated, or the application will hang.
     */
    public void close() throws NotImplemented;
    
    /**
     * Copies the adaptor session.
     * @return the clone.
     * @throws CloneNotSupportedException when the clone method is not supported.
     */
    public Object clone() throws CloneNotSupportedException;
    
    /**
     * Detaches the specified security context from the adaptor session.
     * @param context the context to be removed.
     * @exception DoesNotExist is thrown when the session does not
     *     contain the specified context.
     */
    public void removeContext(Context context) throws NotImplemented, DoesNotExist;
}
