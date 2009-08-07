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

/*
 * (non-Javadoc)
 * 
 * @see org.ogf.saga.sd.Discoverer
 */
public class DiscovererWrapper extends SagaObjectBase implements Discoverer {

    /** The logger. */
    private static Logger m_logger = LoggerFactory.getLogger(DiscovererWrapper.class);

    /** The URL of the information system. */
    private URL m_infoSystemUrl;

    /** The service discovery adapter. */
    private DiscovererSPI m_proxy;

    /**
     * Create a discoverer wrapper.
     * 
     * @param session
     *            the session handle
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws TimeoutException
     */
    protected DiscovererWrapper(Session session) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, IncorrectURLException, NoSuccessException, NotImplementedException, TimeoutException {
        super(session);
        Object[] parameters = { this, session, m_infoSystemUrl };
        createProxy(parameters);
    }

    /**
     * Create a discoverer wrapper.
     * 
     * @param session
     *            the session handle
     * @param infoSystemUrl
     *            a URL, this value is no longer used
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws TimeoutException
     */
    protected DiscovererWrapper(Session session, URL infoSystemUrl) throws AuthenticationFailedException,
            AuthorizationFailedException, DoesNotExistException, IncorrectURLException, NoSuccessException,
            NotImplementedException, TimeoutException {
        super(session);
        m_infoSystemUrl = infoSystemUrl;
        Object[] parameters = { this, session, m_infoSystemUrl };
        createProxy(parameters);
    }

    /**
     * Clone the DiscovererWrapper and attached adapter.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DiscovererWrapper clone = (DiscovererWrapper) super.clone();
        clone.m_proxy = (DiscovererSPI) SAGAEngine.createAdaptorCopy(DiscovererSPI.class, m_proxy, clone);
        clone.m_infoSystemUrl = (URL) m_infoSystemUrl.clone();
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.sd.Discoverer#listServices(java.lang.String,
     *      java.lang.String)
     */
    public List<ServiceDescription> listServices(String serviceFilter, String dataFilter)
            throws AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            NoSuccessException, TimeoutException {
        return m_proxy.listServices(serviceFilter, dataFilter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.sd.Discoverer#listServices(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public List<ServiceDescription> listServices(String serviceFilter, String dataFilter, String authzFilter)
            throws AuthenticationFailedException, AuthorizationFailedException, BadParameterException,
            NoSuccessException, TimeoutException {
        return m_proxy.listServices(serviceFilter, dataFilter, authzFilter);
    }

    @Override
    public String toString() {
        String s = null;
        try {
            s = "Session:" + this.getSession().toString() + " Information System URL:" + m_infoSystemUrl;
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
    private void createProxy(Object[] parameters) throws AuthenticationFailedException, AuthorizationFailedException,
            DoesNotExistException, IncorrectURLException, NoSuccessException, NotImplementedException, TimeoutException {
        try {
            m_proxy = (DiscovererSPI) SAGAEngine.createAdaptorProxy(DiscovererSPI.class, new Class[] {
                    DiscovererWrapper.class, org.ogf.saga.impl.session.SessionImpl.class, URL.class }, parameters);
        } catch (AuthenticationFailedException e) {
            throw e;
        } catch (AuthorizationFailedException e) {
            throw e;
        } catch (DoesNotExistException e) {
            throw e;
        } catch (IncorrectURLException e) {
            throw e;
        } catch (NoSuccessException e) {
            throw e;
        } catch (NotImplementedException e) {
            throw e;
        } catch (TimeoutException e) {
            throw e;
        } catch (SagaException e) {
            throw new NoSuccessException("Constructor failed", e);
        }

        m_logger.info("Created Discoverer with " + this.toString());
    }
}
