package org.ogf.saga.proxies.sd;

import java.util.Properties;
import java.util.Set;

import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.sd.Discoverer;
import org.ogf.saga.sd.ServiceDescription;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.sd.DiscovererSPI;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a service discovery adaptor. If the url of the information system is
 * not provided then get the url from the properties file.
 * 
 */
public class DiscovererWrapper extends SagaObjectBase implements Discoverer {

    /** The name of the property. */
    private static final String INFO_SYSTEM_URL = "info.system.url";

    /** The logger. */
    private static Logger m_logger = LoggerFactory
	    .getLogger(DiscovererWrapper.class);

    /** The SAGA properties from the properties file. */
    private static final Properties m_properties = org.ogf.saga.bootstrap.SagaProperties
	    .getDefaultProperties();

    /** The URL of the information system. */
    private URL m_infoSystemUrl;

    /** The service discovery adaptor. */
    private DiscovererSPI m_proxy;

    /**
     * Create a discoverer wrapper using the url of the information system from
     * the properties file.
     * 
     * @param session
     *                the session handle
     * @throws NotImplementedException
     *                 if not implemented by that SAGA implementation at all
     * @throws IncorrectURLException
     *                 if an implementation cannot handle the specified
     *                 protocol, or that access to the specified entity via the
     *                 given protocol is impossible
     * @throws DoesNotExistException
     *                 if the url is syntactically valid, but no service can be
     *                 contacted at that URL
     * @throws AuthorizationFailedException
     *                 if none of the available contexts of the used session
     *                 could be used for successful authorization. That error
     *                 indicates that the resource could not be accessed at all,
     *                 and not that an operation was not available due to
     *                 restricted permissions.
     * @throws AuthenticationFailedException
     *                 if none of the available session contexts could
     *                 successfully be used for authentication
     * @throws TimeoutException
     *                 if a remote operation did not complete successfully
     *                 because the network communication or the remote service
     *                 timed out
     * @throws NoSuccessException
     *                 if no result can be returned because of information
     *                 system or other internal problems
     */
    protected DiscovererWrapper(Session session) throws IncorrectURLException,
	    NoSuccessException, NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    TimeoutException {
	super(session);
	setDefaultInfoSystemUrl();
	Object[] parameters = { this, session, m_infoSystemUrl };
	createProxy(parameters);
    }

    /**
     * Create a discoverer wrapper.
     * 
     * @param session
     *                the session handle
     * @param infoSystemUrl
     *                the URL to guide the implementation
     * @throws NotImplementedException
     *                 if not implemented by that SAGA implementation at all
     * @throws IncorrectURLException
     *                 if an implementation cannot handle the specified
     *                 protocol, or that access to the specified entity via the
     *                 given protocol is impossible
     * @throws DoesNotExistException
     *                 if the url is syntactically valid, but no service can be
     *                 contacted at that URL
     * @throws AuthorizationFailedException
     *                 if none of the available contexts of the used session
     *                 could be used for successful authorization. That error
     *                 indicates that the resource could not be accessed at all,
     *                 and not that an operation was not available due to
     *                 restricted permissions.
     * @throws AuthenticationFailedException
     *                 if none of the available session contexts could
     *                 successfully be used for authentication
     * @throws TimeoutException
     *                 if a remote operation did not complete successfully
     *                 because the network communication or the remote service
     *                 timed out
     * @throws NoSuccessException
     *                 if no result can be returned because of information
     *                 system or other internal problems
     */
    protected DiscovererWrapper(Session session, URL infoSystemUrl)
	    throws NoSuccessException, TimeoutException,
	    AuthorizationFailedException, AuthenticationFailedException,
	    IncorrectURLException, NotImplementedException {
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
     *                a string containing the filter for filtering on the basic
     *                service and site attributes and on related services
     * @param dataFilter
     *                a string containing the filter for filtering on key/value
     *                pairs associated with the service
     * @return list of service descriptions, in a random order, matching the
     *         filter criteria
     * @throws BadParameterException
     *                 if any filter has an invalid syntax or if any filter uses
     *                 invalid keys. However the <code>dataFilter</code> never
     *                 signals invalid keys as there is no schema with
     *                 permissible key names.
     * @throws AuthorizationFailedException
     *                 if none of the available contexts of the used session
     *                 could be used for successful authorization. That error
     *                 indicates that the resource could not be accessed at all,
     *                 and not that an operation was not available due to
     *                 restricted permissions.
     * @throws AuthenticationFailedException
     *                 if none of the available session contexts could
     *                 successfully be used for authentication
     * @throws TimeoutException
     *                 if a remote operation did not complete successfully
     *                 because the network communication or the remote service
     *                 timed out
     * @throws NoSuccessException
     *                 if no result can be returned because of information
     *                 system or other internal problems
     */
    @Override
    public Set<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter) throws BadParameterException,
	    AuthorizationFailedException, AuthenticationFailedException,
	    TimeoutException, NoSuccessException {
	return m_proxy.listServices(serviceFilter, dataFilter);
    }

    /**
     * Returns the set of services that pass the set of specified filters. A
     * service will only be included once in the returned list of services.
     * 
     * @param serviceFilter
     *                a string containing the filter for filtering on the basic
     *                service and site attributes and on related services
     * @param dataFilter
     *                a string containing the filter for filtering on key/value
     *                pairs associated with the service
     * @param authzFilter
     *                a string containing the filter for filtering on
     *                authorization information associated with the service
     * @return list of service descriptions, in a random order, matching the
     *         filter criteria
     * @throws BadParameterException
     *                 if any filter has an invalid syntax or if any filter uses
     *                 invalid keys. However the <code>dataFilter</code> never
     *                 signals invalid keys as there is no schema with
     *                 permissible key names.
     * @throws AuthorizationFailedException
     *                 if none of the available contexts of the used session
     *                 could be used for successful authorization. That error
     *                 indicates that the resource could not be accessed at all,
     *                 and not that an operation was not available due to
     *                 restricted permissions.
     * @throws AuthenticationFailedException
     *                 if none of the available session contexts could
     *                 successfully be used for authentication
     * @throws TimeoutException
     *                 if a remote operation did not complete successfully
     *                 because the network communication or the remote service
     *                 timed out
     * @throws NoSuccessException
     *                 if no result can be returned because of information
     *                 system or other internal problems
     */
    @Override
    public Set<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter, String authzFilter)
	    throws BadParameterException, AuthorizationFailedException,
	    AuthenticationFailedException, TimeoutException, NoSuccessException {
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
     * Create the proxy that points to the adaptor.
     * 
     * @param parameters
     * @throws NotImplementedException
     * @throws IncorrectURLException
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws TimeoutException
     * @throws NoSuccessException
     */
    private void createProxy(Object[] parameters)
	    throws NotImplementedException, IncorrectURLException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    TimeoutException, NoSuccessException {
	try {
	    m_proxy = (DiscovererSPI) SAGAEngine.createAdaptorProxy(
		    DiscovererSPI.class, new Class[] { DiscovererWrapper.class,
			    org.ogf.saga.impl.session.SessionImpl.class,
			    URL.class }, parameters);
	} catch (org.ogf.saga.error.SagaException e) {
	    if (e instanceof NotImplementedException) {
		throw (NotImplementedException) e;
	    }
	    if (e instanceof IncorrectURLException) {
		throw (IncorrectURLException) e;
	    }
	    if (e instanceof AuthenticationFailedException) {
		throw (AuthenticationFailedException) e;
	    }
	    if (e instanceof AuthorizationFailedException) {
		throw (AuthorizationFailedException) e;
	    }
	    if (e instanceof TimeoutException) {
		throw (TimeoutException) e;
	    }
	    if (e instanceof NoSuccessException) {
		throw (NoSuccessException) e;
	    }
	    throw new NoSuccessException("Constructor failed", e);
	}

	m_logger.info("Created Discoverer with " + this.toString());
    }

    /**
     * Get the default URL of the information system from the properties file
     * 
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     */
    private void setDefaultInfoSystemUrl() throws IncorrectURLException,
	    NoSuccessException, NotImplementedException {

	String infoSystemString = m_properties.getProperty(INFO_SYSTEM_URL);
	if (infoSystemString == null) {
	    throw new IncorrectURLException(
		    "Property 'info.system.url' not found");
	}
	try {
	    m_infoSystemUrl = URLFactory.createURL(infoSystemString);
	} catch (BadParameterException e) {
	    throw new IncorrectURLException(
		    "Value of property 'info.system.url' " + m_infoSystemUrl
			    + " is not a valid URL");
	}
    }
}
