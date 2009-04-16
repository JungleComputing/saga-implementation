package org.ogf.saga.proxies.sd;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.sd.Discoverer;
import org.ogf.saga.sd.SDFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;

/**
 * The implementation of the service discovery factory.
 * 
 */
public class SDWrapperFactory extends SDFactory {
    public SDWrapperFactory() {
    }

    /**
     * Creates a <code>Discoverer</code> with the default <code>URL</code>.
     * 
     * @param session
     *                the session handle
     * @return the discoverer wrapper
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws TimeoutException
     */
    @Override
    protected Discoverer doCreateDiscoverer(Session session)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, IncorrectURLException, NoSuccessException,
	    NotImplementedException, TimeoutException {
	return new DiscovererWrapper(session);
    }

    /**
     * Creates a <code>Discoverer</code>.
     * 
     * @param session
     *                the session handle
     * @param url
     *                the URL to guide the implementation
     * @return the discoverer wrapper
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws DoesNotExistException
     * @throws IncorrectURLException
     * @throws NoSuccessException
     * @throws NotImplementedException
     * @throws TimeoutException
     */
    @Override
    protected Discoverer doCreateDiscoverer(Session session, URL url)
	    throws AuthenticationFailedException, AuthorizationFailedException,
	    DoesNotExistException, IncorrectURLException, NoSuccessException,
	    NotImplementedException, TimeoutException {
	return new DiscovererWrapper(session, url);
    }

}
