package org.ogf.saga.impl.isn;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.isn.EntityData;

/*
 * (non-Javadoc)
 * 
 * @see org.ogf.saga.isn.EntityData
 */
public final class EntityDataImpl extends SagaObjectBase implements EntityData {

    /** The entity data attributes. */
    private EntityDataAttributes m_attributes;

    /**
     * Construct a new EntityDataImpl.
     * 
     */
    public EntityDataImpl() {
        m_attributes = new EntityDataAttributes();
    }

    /**
     * Constructor for use when cloning.
     * 
     * @param orig
     *            the original object
     */
    protected EntityDataImpl(EntityDataImpl orig) {
        super(orig);
        m_attributes = new EntityDataAttributes(orig.m_attributes);
    }

    /**
     * Clone the EntityData object.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        EntityDataImpl clone = (EntityDataImpl) super.clone();
        clone.m_attributes = new EntityDataAttributes(m_attributes);
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#existsAttribute(java.lang.String)
     */
    @Override
    public boolean existsAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return m_attributes.existsAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#findAttributes(java.lang.String[])
     */
    @Override
    public String[] findAttributes(String... patterns) throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, TimeoutException,
            NoSuccessException {
        return m_attributes.findAttributes(patterns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        return m_attributes.getAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#getVectorAttribute(java.lang.String)
     */
    @Override
    public String[] getVectorAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        return m_attributes.getVectorAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isReadOnlyAttribute(java.lang.String)
     */
    @Override
    public boolean isReadOnlyAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return m_attributes.isReadOnlyAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isRemovableAttribute(java.lang.String)
     */
    @Override
    public boolean isRemovableAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return m_attributes.isRemovableAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isVectorAttribute(java.lang.String)
     */
    @Override
    public boolean isVectorAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return m_attributes.isVectorAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#isWritableAttribute(java.lang.String)
     */
    @Override
    public boolean isWritableAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return m_attributes.isWritableAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#listAttributes()
     */
    @Override
    public String[] listAttributes() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return m_attributes.listAttributes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        m_attributes.removeAttribute(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#setAttribute(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setAttribute(String key, String value) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        m_attributes.setAttribute(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.attributes.Attributes#setVectorAttribute(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public void setVectorAttribute(String key, String[] values) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        m_attributes.setVectorAttribute(key, values);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adapters.
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
     * This method has been made available for use by adapters.
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
     * This method has been made available for use by adapters.
     * 
     * @see org.ogf.saga.attributes.Attributes#setVectorValue(java.lang.String,
     *      java.lang.String[])
     */
    public void setVectorValue(String key, String[] values) throws BadParameterException, DoesNotExistException,
            IncorrectStateException, NotImplementedException {
        m_attributes.setVectorValue(key, values);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            boolean first = true;
            for (String attb : listAttributes()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",\n");
                }
                if (isVectorAttribute(attb)) {
                    sb.append(attb);
                    sb.append(":");
                    sb.append("[");
                    boolean firstVector = true;
                    for (String s : getVectorAttribute(attb)) {
                        if (firstVector) {
                            firstVector = false;
                        } else {
                            sb.append(",\n");
                        }
                        sb.append(s);
                    }
                    sb.append("]");
                } else {
                    sb.append(attb);
                    sb.append(":");
                    sb.append(getAttribute(attb));
                }
            }
        } catch (SagaException e) {
            throw new Error("Internal error: " + e.getMessage());
        }
        return sb.toString();
    }

}
