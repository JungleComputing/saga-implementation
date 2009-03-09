package org.ogf.saga.impl.sd;

import java.util.HashSet;
import java.util.Set;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.sd.Discoverer;
import org.ogf.saga.sd.SDFactory;
import org.ogf.saga.sd.ServiceData;
import org.ogf.saga.sd.ServiceDescription;
import org.ogf.saga.session.Session;

/**
 * Provides access to the service description attributes.
 * 
 */
public class ServiceDescriptionImpl extends SagaObjectBase implements
	org.ogf.saga.sd.ServiceDescription, Cloneable {

    /** The service description attributes */
    private ServiceDescriptionAttributes m_attributes;

    /** A container for the service data attributes */
    private ServiceData m_serviceData;

    /**
     * Constructs a service description implementation object. This provides
     * added functionality for use by adaptors.
     * 
     * @param serviceData
     *                a ServiceData object
     */
    public ServiceDescriptionImpl(ServiceData serviceData) {
	super((Session) null);
	m_attributes = new ServiceDescriptionAttributes();
	m_serviceData = serviceData;
    }

    /**
     * Returns a <code>ServiceData</code> object with the service data
     * key/value pairs.
     * 
     * @return the service data for this service. This may be empty, i.e.has no
     *         attributes at all.
     */
    public ServiceData getData() {
	return m_serviceData;
    }

    /**
     * Returns the set of related services. Alternatively, the
     * <code>org.ogf.saga.attributes.Attributes</code> interface may be used
     * to get the uids of the related services.
     * 
     * @return a set of related services. This may be an empty set.
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
    public Set<ServiceDescription> getRelatedServices()
	    throws AuthorizationFailedException, AuthenticationFailedException,
	    TimeoutException, NoSuccessException {
	Set<ServiceDescription> descriptions = new HashSet<ServiceDescription>();
	String[] relatedServices = null;
	try {
	    relatedServices = m_attributes
		    .getVectorAttribute(ServiceDescription.RELATED_SERVICES);
	} catch (NotImplementedException e) {
	    return descriptions;
	} catch (PermissionDeniedException e) {
	    return descriptions;
	} catch (IncorrectStateException e) {
	    return descriptions;
	} catch (DoesNotExistException e) {
	    return descriptions;
	}
	if (relatedServices == null) {
	    // This cannot happen
	    throw new NoSuccessException(
		    "Internal error, list of related services is null");
	}
	if (relatedServices.length == 0) {
	    return descriptions;
	}
	String filter = "";
	for (String service : relatedServices) {
	    if (filter.equals("")) {
		filter = filter + "uid = '" + service + "'";
	    } else {
		filter = filter + " or uid = '" + service + "'";
	    }
	}
	Discoverer discoverer = null;
	try {
	    discoverer = SDFactory.createDiscoverer();
	} catch (NotImplementedException e) {
	    throw new NoSuccessException(
		    "Internal error, unable to get related services");
	} catch (IncorrectURLException e) {
	    throw new NoSuccessException(
		    "Internal error, unable to get related services");
	} catch (DoesNotExistException e) {
	    throw new NoSuccessException(
		    "Internal error, unable to get related services");
	}
	try {
	    descriptions = discoverer.listServices(filter, "", "");
	} catch (BadParameterException e) {
	    throw new NoSuccessException(
		    "Internal error, unable to get related services");
	}
	return descriptions;
    }

    /**
     * Returns the <code>URL</code> to contact the service. The
     * <code>URL</code> may also be obtained using the
     * <code>org.ogf.saga.attributes.Attributes</code> interface.
     * 
     * @return a string containing the URL to contact this service
     */
    public String getUrl() {
	String url = null;
	try {
	    url = m_attributes.getAttribute(ServiceDescription.URL);
	} catch (NotImplementedException e) {
	} catch (AuthenticationFailedException e) {
	} catch (AuthorizationFailedException e) {
	} catch (PermissionDeniedException e) {
	} catch (IncorrectStateException e) {
	} catch (DoesNotExistException e) {
	} catch (TimeoutException e) {
	} catch (NoSuccessException e) {
	}
	return url;
    }

    /**
     * Checks the existence of an attribute.
     * 
     * @param key
     *                the attribute key.
     * @return <code>true</code> if the attribute exists.
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     */
    public boolean existsAttribute(String key) throws NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    PermissionDeniedException, TimeoutException, NoSuccessException {
	return m_attributes.existsAttribute(key);
    }

    /**
     * Finds matching attributes.
     * 
     * @param patterns
     *                the search patterns
     * @return the list of matching attribute keys
     * @throws NotImplementedException
     *                 ...
     * @throws BadParameterException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     * 
     */
    public String[] findAttributes(String... patterns)
	    throws NotImplementedException, BadParameterException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    PermissionDeniedException, TimeoutException, NoSuccessException {
	return m_attributes.findAttributes(patterns);
    }

    /**
     * Gets the value of an attribute.
     * 
     * @param key
     *                the attribute key
     * @return the value of this attribute
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws IncorrectStateException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     * 
     * @see #setAttribute
     */
    public String getAttribute(String key) throws NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    PermissionDeniedException, IncorrectStateException,
	    DoesNotExistException, TimeoutException, NoSuccessException {
	return m_attributes.getAttribute(key);
    }

    /**
     * Gets the array of values associated with an attribute.
     * 
     * @param key
     *                the attribute key
     * @return the values of this attribute, or <code>null</code>
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws IncorrectStateException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     * 
     * @see #setVectorAttribute
     */
    public String[] getVectorAttribute(String key)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    IncorrectStateException, DoesNotExistException, TimeoutException,
	    NoSuccessException {
	return m_attributes.getVectorAttribute(key);
    }

    /**
     * Checks the attribute for being read-only.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute exists and is read-only
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     */
    public boolean isReadOnlyAttribute(String key)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    DoesNotExistException, TimeoutException, NoSuccessException {
	return m_attributes.isReadOnlyAttribute(key);
    }

    /**
     * Checks the attribute for being removable.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute exists and is removable
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     */
    public boolean isRemovableAttribute(String key)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    DoesNotExistException, TimeoutException, NoSuccessException {
	return m_attributes.isRemovableAttribute(key);
    }

    /**
     * Checks the attribute for being a vector.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute is a vector attribute
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     */
    public boolean isVectorAttribute(String key)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    DoesNotExistException, TimeoutException, NoSuccessException {
	return m_attributes.isVectorAttribute(key);
    }

    /**
     * Checks the attribute for being writable.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute exists and is writable
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     */
    public boolean isWritableAttribute(String key)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    DoesNotExistException, TimeoutException, NoSuccessException {
	return m_attributes.isWritableAttribute(key);
    }

    /**
     * Gets the list of attribute keys.
     * 
     * @return the list of attribute keys
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     * 
     */
    public String[] listAttributes() throws NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    PermissionDeniedException, TimeoutException, NoSuccessException {
	return m_attributes.listAttributes();
    }

    /**
     * Removes an attribute.
     * 
     * @param key
     *                the attribute key
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     * 
     */
    public void removeAttribute(String key) throws NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    PermissionDeniedException, DoesNotExistException, TimeoutException,
	    NoSuccessException {
	m_attributes.removeAttribute(key);
    }

    /**
     * Sets an attribute to a value.
     * 
     * @param key
     *                the attribute key
     * @param value
     *                value to set the attribute to
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws IncorrectStateException
     *                 ...
     * @throws BadParameterException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     * 
     * @see #getAttribute
     */
    public void setAttribute(String key, String value)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    IncorrectStateException, BadParameterException,
	    DoesNotExistException, TimeoutException, NoSuccessException {
	m_attributes.setAttribute(key, value);
    }

    /**
     * Sets an attribute to an array of values.
     * 
     * @param key
     *                the attribute key
     * @param values
     *                values to set the attribute to
     * @throws NotImplementedException
     *                 ...
     * @throws AuthenticationFailedException
     *                 ...
     * @throws AuthorizationFailedException
     *                 ...
     * @throws PermissionDeniedException
     *                 ...
     * @throws IncorrectStateException
     *                 ...
     * @throws BadParameterException
     *                 ...
     * @throws DoesNotExistException
     *                 ...
     * @throws TimeoutException
     *                 ...
     * @throws NoSuccessException
     *                 ...
     * 
     * @see #getVectorAttribute
     */
    public void setVectorAttribute(String key, String[] values)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    IncorrectStateException, BadParameterException,
	    DoesNotExistException, TimeoutException, NoSuccessException {
	m_attributes.setVectorAttribute(key, values);
    }

    /**
     * Set method without checks for readOnly.
     * 
     * Sets an attribute to a value without checks for readOnly. This method has
     * been made available for use by adaptors.
     * 
     * @param key
     *                the attribute key
     * @param value
     *                value to set the attribute to
     * @throws DoesNotExistException
     *                 ...
     * @throws NotImplementedException
     *                 ...
     * @throws IncorrectStateException
     *                 ...
     * @throws BadParameterException
     *                 ...
     */
    public void setValue(String key, String value)
	    throws DoesNotExistException, NotImplementedException,
	    IncorrectStateException, BadParameterException {
	m_attributes.setValue(key, value);
    }

    /**
     * Set method without checks for readOnly.
     * 
     * Sets an attribute to an array of values without checks for readOnly. This
     * method has been made available for use by adaptors.
     * 
     * @param key
     *                the attribute key
     * @param values
     *                array of values to set the attribute to
     * @throws DoesNotExistException
     *                 ...
     * @throws NotImplementedException
     *                 ...
     * @throws IncorrectStateException
     *                 ...
     * @throws BadParameterException
     *                 ...
     */
    public void setVectorValue(String key, String[] values)
	    throws DoesNotExistException, NotImplementedException,
	    IncorrectStateException, BadParameterException {
	m_attributes.setVectorValue(key, values);
    }
}
