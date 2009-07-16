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

/*
 * (non-Javadoc)
 * 
 * @see org.ogf.saga.sd.ServiceData
 */
public class ServiceDataImpl extends SagaObjectBase implements org.ogf.saga.sd.ServiceData, Cloneable {

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

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#existsAttribute(java.lang.String)
     */
    public boolean existsAttribute(String key) throws AuthenticationFailedException, AuthorizationFailedException,
            NoSuccessException, NotImplementedException, PermissionDeniedException, TimeoutException {
        return m_attributes.existsAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#findAttributes(java.lang.String[])
     */
    public String[] findAttributes(String... patterns) throws AuthenticationFailedException,
            AuthorizationFailedException, BadParameterException, NotImplementedException, NoSuccessException,
            PermissionDeniedException, TimeoutException {
        return m_attributes.findAttributes(patterns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#getAttribute(java.lang.String)
     */
    public String getAttribute(String key) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, IncorrectStateException, NoSuccessException, NotImplementedException,
            PermissionDeniedException, TimeoutException {
        return m_attributes.getAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#getVectorAttribute(java.lang.String)
     */
    public String[] getVectorAttribute(String key) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, IncorrectStateException, NoSuccessException, NotImplementedException,
            PermissionDeniedException, TimeoutException {
        return m_attributes.getVectorAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isReadOnlyAttribute(java.lang.String)
     */
    public boolean isReadOnlyAttribute(String key) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, NoSuccessException, NotImplementedException, PermissionDeniedException,
            TimeoutException {
        return m_attributes.isReadOnlyAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isRemovableAttribute(java.lang.String)
     */
    public boolean isRemovableAttribute(String key) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, NoSuccessException, NotImplementedException, PermissionDeniedException,
            TimeoutException {
        return m_attributes.isRemovableAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isVectorAttribute(java.lang.String)
     */
    public boolean isVectorAttribute(String key) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, NoSuccessException, NotImplementedException, PermissionDeniedException,
            TimeoutException {
        return m_attributes.isVectorAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isWritableAttribute(java.lang.String)
     */
    public boolean isWritableAttribute(String key) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, NoSuccessException, NotImplementedException, PermissionDeniedException,
            TimeoutException {
        return m_attributes.isWritableAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#listAttributes()
     */
    public String[] listAttributes() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, NoSuccessException, PermissionDeniedException, TimeoutException {
        return m_attributes.listAttributes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, DoesNotExistException, NoSuccessException, PermissionDeniedException,
            TimeoutException {
        m_attributes.removeAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#setAttribute(java.lang.String,
     *      java.lang.String)
     */
    public void setAttribute(String key, String value) throws AuthenticationFailedException,
            AuthorizationFailedException, BadParameterException, DoesNotExistException, IncorrectStateException,
            NoSuccessException, NotImplementedException, PermissionDeniedException, TimeoutException {
        m_attributes.setAttribute(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#setVectorAttribute(java.lang.String,
     *      java.lang.String[])
     */
    public void setVectorAttribute(String key, String[] values) throws AuthenticationFailedException,
            AuthorizationFailedException, BadParameterException, DoesNotExistException, IncorrectStateException,
            NoSuccessException, NotImplementedException, PermissionDeniedException, TimeoutException {
        m_attributes.setVectorAttribute(key, values);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adaptors.
     * 
     * @see org.ogf.saga.attributes.Attributes#addAttribute(java.lang.String,
     *      org.ogf.saga.attributes.AttributeType, boolean, boolean, boolean,
     *      boolean)
     */
    public void addAttribute(String name, AttributeType type, boolean vector, boolean readOnly, boolean notImplemented,
            boolean removable) {
        m_attributes.addAttribute(name, type, vector, readOnly, notImplemented, removable);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adaptors.
     * 
     * @see org.ogf.saga.attributes.Attributes#setValue(java.lang.String,
     *      java.lang.String)
     */
    public void setValue(String key, String value) throws BadParameterException, DoesNotExistException,
            IncorrectStateException, NotImplementedException {
        m_attributes.setValue(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adaptors.
     * 
     * @see org.ogf.saga.attributes.Attributes#setVectorValue(java.lang.String,
     *      java.lang.String[])
     */
    public void setVectorValue(String key, String[] values) throws BadParameterException, DoesNotExistException,
            IncorrectStateException, NotImplementedException {
        m_attributes.setVectorValue(key, values);
    }

}
