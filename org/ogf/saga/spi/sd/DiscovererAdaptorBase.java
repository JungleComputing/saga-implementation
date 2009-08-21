package org.ogf.saga.spi.sd;

import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.sd.DiscovererWrapper;

/**
 * <dl>
 * <dt>Base class for Service Discovery adapters. Adapters extending this class
 * should provide a constructor that should take:</dt>
 * <dd><code>DiscovererWrapper wrapper</code>, needed by the super
 * constructor</dd>
 * <dd><code>SessionImpl sessionImpl</code>, needed by the super constructor</dd>,
 * <dd><code>URL infoSystemUrl</code>, the URL to guide the implementation,
 * may be <code>null</code></dd>
 * </dt>
 * </dl>
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
