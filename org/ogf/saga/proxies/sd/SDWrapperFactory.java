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

/*
 * (non-Javadoc)
 * 
 * @see org.ogf.saga.sd.SDFactory
 */
public class SDWrapperFactory extends SDFactory {
    public SDWrapperFactory() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.sd.SDFactory#doCreateDiscoverer(org.ogf.saga.Session)
     */
    @Override
    protected Discoverer doCreateDiscoverer(Session session) throws AuthenticationFailedException,
            AuthorizationFailedException, DoesNotExistException, IncorrectURLException, NoSuccessException,
            NotImplementedException, TimeoutException {
        return new DiscovererWrapper(session);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.sd.SDFactory#doCreateDiscoverer(org.ogf.saga.session.Session,
     *      org.ogf.saga.url.URL)
     */
    @Override
    protected Discoverer doCreateDiscoverer(Session session, URL url) throws AuthenticationFailedException,
            AuthorizationFailedException, DoesNotExistException, IncorrectURLException, NoSuccessException,
            NotImplementedException, TimeoutException {
        return new DiscovererWrapper(session, url);
    }

}
