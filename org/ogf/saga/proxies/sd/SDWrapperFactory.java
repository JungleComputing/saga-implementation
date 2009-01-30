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
    @Override
    protected Discoverer doCreateDiscoverer(Session session)
	    throws NotImplementedException, IncorrectURLException,
	    DoesNotExistException, AuthorizationFailedException,
	    AuthenticationFailedException, TimeoutException, NoSuccessException {
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
    @Override
    protected Discoverer doCreateDiscoverer(Session session, URL url)
	    throws NotImplementedException, IncorrectURLException,
	    DoesNotExistException, AuthorizationFailedException,
	    AuthenticationFailedException, TimeoutException, NoSuccessException {
	return new DiscovererWrapper(session, url);
    }

}
