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
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     */
    public boolean existsAttribute(String key)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    NoSuccessException, NotImplementedException,
	    PermissionDeniedException, TimeoutException {
	return m_attributes.existsAttribute(key);
    }

    /**
     * Finds matching attributes.
     * 
     * @param patterns
     *                the search patterns
     * @return the list of matching attribute keys
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws BadParameterException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     */
    public String[] findAttributes(String... patterns)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    BadParameterException, NotImplementedException, NoSuccessException,
	    PermissionDeniedException, TimeoutException {
	return m_attributes.findAttributes(patterns);
    }

    /**
     * Gets the value of an attribute.
     * 
     * @param key
     *                the attribute key
     * @return the value of this attribute
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectStateException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     * 
     * @see #setAttribute
     */
    public String getAttribute(String key)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, IncorrectStateException, NoSuccessException,
	    NotImplementedException, PermissionDeniedException,
	    TimeoutException {
	return m_attributes.getAttribute(key);
    }

    /**
     * Gets the array of values associated with an attribute.
     * 
     * @param key
     *                the attribute key
     * @return the values of this attribute, or <code>null</code>
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectStateException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     * 
     * @see #setVectorAttribute
     */
    public String[] getVectorAttribute(String key)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, IncorrectStateException, NoSuccessException,
	    NotImplementedException, PermissionDeniedException,
	    TimeoutException {
	return m_attributes.getVectorAttribute(key);
    }

    /**
     * Checks the attribute for being read-only.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute exists and is read-only
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     */
    public boolean isReadOnlyAttribute(String key)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, NoSuccessException, NotImplementedException,
	    PermissionDeniedException, TimeoutException {
	return m_attributes.isReadOnlyAttribute(key);
    }

    /**
     * Checks the attribute for being removable.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute exists and is removable
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     */
    public boolean isRemovableAttribute(String key)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, NoSuccessException, NotImplementedException,
	    PermissionDeniedException, TimeoutException {
	return m_attributes.isRemovableAttribute(key);
    }

    /**
     * Checks the attribute for being a vector.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute is a vector attribute
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     */
    public boolean isVectorAttribute(String key)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, NoSuccessException, NotImplementedException,
	    PermissionDeniedException, TimeoutException {
	return m_attributes.isVectorAttribute(key);
    }

    /**
     * Checks the attribute for being writable.
     * 
     * @param key
     *                the attribute key
     * @return <code>true</code> if the attribute exists and is writable
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     */
    public boolean isWritableAttribute(String key)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, NoSuccessException, NotImplementedException,
	    PermissionDeniedException, TimeoutException {
	return m_attributes.isWritableAttribute(key);
    }

    /**
     * Gets the list of attribute keys.
     * 
     * @return the list of attribute keys
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     * 
     */
    public String[] listAttributes() throws NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    NoSuccessException, PermissionDeniedException, TimeoutException {
	return m_attributes.listAttributes();
    }

    /**
     * Removes an attribute.
     * 
     * @param key
     *                the attribute key
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     * 
     */
    public void removeAttribute(String key) throws NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, NoSuccessException,
	    PermissionDeniedException, TimeoutException {
	m_attributes.removeAttribute(key);
    }

    /**
     * Sets an attribute to a value.
     * 
     * @param key
     *                the attribute key
     * @param value
     *                value to set the attribute to
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws BadParameterException
     * @throws DoesNotExistException
     * @throws IncorrectStateException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     * 
     * @see #getAttribute
     */
    public void setAttribute(String key, String value)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    BadParameterException, DoesNotExistException,
	    IncorrectStateException, NoSuccessException,
	    NotImplementedException, PermissionDeniedException,
	    TimeoutException {
	m_attributes.setAttribute(key, value);
    }

    /**
     * Sets an attribute to an array of values.
     * 
     * @param key
     *                the attribute key
     * @param values
     *                values to set the attribute to
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws BadParameterException
     * @throws DoesNotExistException
     * @throws IncorrectStateException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws PermissionDeniedException
     * @throws TimeoutException
     * 
     * @see #getVectorAttribute
     */
    public void setVectorAttribute(String key, String[] values)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    BadParameterException, DoesNotExistException,
	    IncorrectStateException, NoSuccessException,
	    NotImplementedException, PermissionDeniedException,
	    TimeoutException {
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
     * @throws BadParameterException
     * @throws DoesNotExistException
     * @throws IncorrectStateException
     * @throws NotImplementedException
     */
    public void setValue(String key, String value)
	    throws BadParameterException, DoesNotExistException,
	    IncorrectStateException, NotImplementedException {
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
     * @throws BadParameterException
     * @throws DoesNotExistException
     * @throws IncorrectStateException
     * @throws NotImplementedException
     */
    public void setVectorValue(String key, String[] values)
	    throws BadParameterException, DoesNotExistException,
	    IncorrectStateException, NotImplementedException {
	m_attributes.setVectorValue(key, values);
    }

}
