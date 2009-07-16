package org.ogf.saga.spi.sd;

import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.sd.DiscovererWrapper;

/**
 * Base class for Service Discovery adapters.
 * 
 */
public abstract class DiscovererAdaptorBase extends AdaptorBase<DiscovererWrapper> implements DiscovererSPI {

    /**
     * Constructs a DiscovererAdaptorBase object.
     * 
     * @param wrapper
     *            the wrapper object
     * @param sessionImpl
     *            the session of the adapter
     */
    public DiscovererAdaptorBase(DiscovererWrapper wrapper, SessionImpl sessionImpl) {
        super(sessionImpl, wrapper);
    }
}
