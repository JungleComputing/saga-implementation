package org.ogf.saga.spi.sd;

import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.sd.DiscovererWrapper;
import org.ogf.saga.url.URL;

/**
 * Base class for Service Discovery adaptors.
 * 
 */
public abstract class DiscovererAdaptorBase extends
	AdaptorBase<DiscovererWrapper> implements DiscovererSPI {

    /** The URL of the information system. */
    private URL m_infoSystemUrl;

    /**
     * Constructs a DiscovererAdaptorBase object.
     * 
     * @param wrapper
     *                the wrapper object
     * @param sessionImpl
     *                the session of the adaptor
     * @param infoSystemUrl
     *                the url of the information system
     */
    public DiscovererAdaptorBase(DiscovererWrapper wrapper,
	    SessionImpl sessionImpl, URL infoSystemUrl) {
	super(sessionImpl, wrapper);
	m_infoSystemUrl = infoSystemUrl;
    }

    /**
     * Returns the url of the information system.
     * 
     * @return the URL of the information system
     */
    protected URL getInfoSystemUrl() {
	return m_infoSystemUrl;
    }

}
