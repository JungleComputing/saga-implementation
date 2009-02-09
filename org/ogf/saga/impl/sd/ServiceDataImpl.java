package org.ogf.saga.impl.sd;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.session.Session;

/**
 * Provides access to the service data attributes.
 * 
 */
public class ServiceDataImpl extends SagaObjectBase implements
	org.ogf.saga.sd.ServiceData, Cloneable {

    /** The service data attributes */
    private ServiceDataAttributes m_attributes;

    /**
     * Constructs a service data implementation object. This provides added
     * functionality for use by adaptors.
     * 
     */
    public ServiceDataImpl() {
	super((Session) null);
	m_attributes = new ServiceDataAttributes();
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
     * Stores information about a specific attribute. This method has been made
     * available for use by adaptors.
     * 
     * @param name
     *                name of attribute
     * @param type
     *                the attribute type
     * @param vector
     *                true if attribute is vector
     * @param readOnly
     *                true if attribute is read only
     * @param notImplemented
     *                true if attribute is not available in this implementation
     * @param removable
     *                true if attribute is removable
     */
    public void addAttribute(String name, AttributeType type, boolean vector,
	    boolean readOnly, boolean notImplemented, boolean removable) {
	m_attributes.addAttribute(name, type, vector, readOnly, notImplemented,
		removable);
    }

}
