package org.ogf.saga.proxies.sd;

import java.util.List;

import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.sd.Discoverer;
import org.ogf.saga.sd.ServiceDescription;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.sd.DiscovererSPI;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a service discovery adapter.
 * 
 */
public class DiscovererWrapper extends SagaObjectBase implements Discoverer {

    /** The logger. */
    private static Logger m_logger = LoggerFactory
	    .getLogger(DiscovererWrapper.class);

    /** The URL of the information system. */
    private URL m_infoSystemUrl;

    /** The service discovery adapter. */
    private DiscovererSPI m_proxy;

    /**
     * Create a discoverer wrapper.
     * 
     * @param session
     *                the session handle
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws TimeoutException
     */
    protected DiscovererWrapper(Session session)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, IncorrectURLException, NoSuccessException,
	    NotImplementedException, TimeoutException {
	super(session);
	Object[] parameters = { this, session, m_infoSystemUrl };
	createProxy(parameters);
    }

    /**
     * Create a discoverer wrapper.
     * 
     * @param session
     *                the session handle
     * @param infoSystemUrl
     *                a URL, this value is no longer used
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws TimeoutException
     */
    protected DiscovererWrapper(Session session, URL infoSystemUrl)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, IncorrectURLException, NoSuccessException,
	    NotImplementedException, TimeoutException {
	super(session);
	m_infoSystemUrl = infoSystemUrl;
	Object[] parameters = { this, session, m_infoSystemUrl };
	createProxy(parameters);
    }

    /**
     * Returns the set of services that pass the set of specified filters, an
     * implicit <code>authzFilter</code> is constructed from the contexts of
     * the session. Note that this is different from an empty
     * <code>authzFilter</code>, as that would apply no authorization filter
     * at all.
     * 
     * @param serviceFilter
     * @param dataFilter
     * @return list of service descriptions, in a random order, matching the
     *         filter criteria
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws BadParameterException
     * @throws NoSuccessException
     * @throws TimeoutException
     */
    public List<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter) throws AuthenticationFailedException,
	    AuthorizationFailedException, BadParameterException,
	    NoSuccessException, TimeoutException {
	return m_proxy.listServices(serviceFilter, dataFilter);
    }

    /**
     * Returns the set of services that pass the set of specified filters. A
     * service will only be included once in the returned list of services.
     * 
     * @param serviceFilter
     * @param dataFilter
     * @param authzFilter
     * @return list of service descriptions, in a random order, matching the
     *         filter criteria
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws BadParameterException
     * @throws NoSuccessException
     * @throws TimeoutException
     */
    public List<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter, String authzFilter)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    BadParameterException, NoSuccessException, TimeoutException {
	return m_proxy.listServices(serviceFilter, dataFilter, authzFilter);
    }

    @Override
    public String toString() {
	String s = null;
	try {
	    s = "Session:" + this.getSession().toString()
		    + " Information System URL:" + m_infoSystemUrl;
	} catch (DoesNotExistException e) {
	}
	return s;
    }

    /**
     * Create the proxy that points to the adapter.
     * 
     * @param parameters
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws TimeoutException
     */
    private void createProxy(Object[] parameters)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, IncorrectURLException, NoSuccessException,
	    NotImplementedException, TimeoutException {
	try {
	    m_proxy = (DiscovererSPI) SAGAEngine.createAdaptorProxy(
		    DiscovererSPI.class, new Class[] { DiscovererWrapper.class,
			    org.ogf.saga.impl.session.SessionImpl.class,
			    URL.class }, parameters);
	} catch (AuthenticationFailedException e) {
	    throw (AuthenticationFailedException) e;
	} catch (AuthorizationFailedException e) {
	    throw (AuthorizationFailedException) e;
	} catch (DoesNotExistException e) {
	    throw (DoesNotExistException) e;
	} catch (IncorrectURLException e) {
	    throw (IncorrectURLException) e;
	} catch (NoSuccessException e) {
	    throw (NoSuccessException) e;
	} catch (NotImplementedException e) {
	    throw (NotImplementedException) e;
	} catch (TimeoutException e) {
	    throw (TimeoutException) e;
	} catch (SagaException e) {
	    throw new NoSuccessException("Constructor failed", e);
	}

	m_logger.info("Created Discoverer with " + this.toString());
    }
}
