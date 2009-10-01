package org.ogf.saga.impl.sd;

import java.util.HashSet;
import java.util.Set;

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
import org.ogf.saga.sd.Discoverer;
import org.ogf.saga.sd.SDFactory;
import org.ogf.saga.sd.ServiceData;
import org.ogf.saga.sd.ServiceDescription;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

/*
 * (non-Javadoc)
 * 
 * @see org.ogf.saga.sd.ServiceDescription
 */
public class ServiceDescriptionImpl extends SagaObjectBase implements org.ogf.saga.sd.ServiceDescription, Cloneable {

    /** The service description attributes */
    private ServiceDescriptionAttributes m_attributes;

    /** A container for the service data attributes */
    private ServiceData m_serviceData;

    /**
     * Constructs a service description implementation object. This provides
     * added functionality for use by adaptors.
     * 
     * @param session
     *            the session handle
     * @param serviceData
     *            a ServiceData object
     */
    public ServiceDescriptionImpl(Session session, ServiceData serviceData) {
        super(session);
        m_attributes = new ServiceDescriptionAttributes();
        m_serviceData = serviceData;
    }

    /**
     * Clone the ServiceDescription and attached ServiceData object.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ServiceDescriptionImpl clone = (ServiceDescriptionImpl) super.clone();
        clone.m_attributes = new ServiceDescriptionAttributes(m_attributes);
        clone.m_serviceData = new ServiceDataImpl((ServiceDataImpl) m_serviceData);
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.sd.ServiceDescription#getData()
     */
    public ServiceData getData() {
        return m_serviceData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.sd.ServiceDescription#getRelatedServices()
     */
    public Set<ServiceDescription> getRelatedServices() throws AuthenticationFailedException,
            AuthorizationFailedException, NoSuccessException, TimeoutException {
        Set<ServiceDescription> descriptions = new HashSet<ServiceDescription>();
        String[] relatedServices = null;
        try {
            relatedServices = m_attributes.getVectorAttribute(ServiceDescription.RELATED_SERVICES);
        } catch (SagaException e) {
            throw new Error("Internal error, getVectorAttribute(" + ServiceDescription.RELATED_SERVICES + "): "
                    + e.getMessage());
        }
        if (relatedServices == null) {
            // This cannot happen
            throw new Error("Internal error, list of related services is null");
        }
        if (relatedServices.length == 0) {
            return descriptions;
        }
        StringBuilder sb = new StringBuilder();
        for (String service : relatedServices) {
            if (sb.length() == 0) {
                sb = sb.append("uid = '" + service + "'");
            } else {
                sb = sb.append(" or uid = '" + service + "'");
            }
        }
        String filter = sb.toString();
        URL informationServiceUrl = null;
        try {
            informationServiceUrl = URLFactory.createURL(m_attributes
                    .getAttribute(ServiceDescription.INFORMATION_SERVICE_URL));
        } catch (SagaException e) {
            throw new Error("Internal error, got exception while creating URL: " + e.getMessage());
        }
        Discoverer discoverer = null;
        try {
            discoverer = SDFactory.createDiscoverer(getSession(), informationServiceUrl);
        } catch (SagaException e) {
            throw new Error("Internal error, unable to get related services: " + e.getMessage());
        }
        try {
            descriptions = new HashSet<ServiceDescription>(discoverer.listServices(filter, "", ""));
        } catch (BadParameterException e) {
            throw new Error("Internal error, unable to get related services: " + e.getMessage());
        }
        return descriptions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.sd.ServiceDescription#getUrl()
     */
    public String getUrl() {
        String url = null;
        try {
            url = m_attributes.getAttribute(ServiceDescription.URL);
        } catch (SagaException e) {
            throw new Error("Internal error, unable to get URL: " + e.getMessage());
        }
        return url;
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
            if (m_serviceData.listAttributes().length > 0) {
                sb.append(",\n");
                sb.append(m_serviceData.toString());
            }
            sb.append("\n");
        } catch (SagaException e) {
            throw new Error("Internal error: " + e.getMessage());
        }
        return sb.toString();
    }

}
