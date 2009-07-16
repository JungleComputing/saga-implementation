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
     * @param serviceData
     *            a ServiceData object
     */
    public ServiceDescriptionImpl(ServiceData serviceData) {
        super((Session) null);
        m_attributes = new ServiceDescriptionAttributes();
        m_serviceData = serviceData;
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
            throw new NoSuccessException("Internal error, list of related services is null");
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

        URL informationServiceUrl = null;
        try {
            informationServiceUrl = URLFactory.createURL(m_attributes
                    .getAttribute(ServiceDescription.INFORMATION_SERVICE_URL));
        } catch (Throwable e) {
            throw new Error("Got exception while creating url: ", e);
        }

        try {
            if (informationServiceUrl == null) {
                discoverer = SDFactory.createDiscoverer();
            } else {
                discoverer = SDFactory.createDiscoverer(informationServiceUrl);
            }
        } catch (NotImplementedException e) {
            throw new NoSuccessException("Internal error, unable to get related services");
        } catch (IncorrectURLException e) {
            throw new NoSuccessException("Internal error, unable to get related services");
        } catch (DoesNotExistException e) {
            throw new NoSuccessException("Internal error, unable to get related services");
        }
        try {
            descriptions = new HashSet<ServiceDescription>(discoverer.listServices(filter, "", ""));
        } catch (BadParameterException e) {
            throw new NoSuccessException("Internal error, unable to get related services");
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
}
