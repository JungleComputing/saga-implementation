package org.ogf.saga.spi.sd;

import java.util.Set;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.sd.ServiceDescription;

/**
 * Provides the interface for service discovery for use by adaptors.
 * 
 */
public interface DiscovererSPI {
    /**
     * Returns the set of services that pass the set of specified filters, an
     * implicit <code>authzFilter</code> is constructed from the contexts of
     * the session. Note that this is different from an empty
     * <code>authzFilter</code>, as that would apply no authorization filter
     * at all.
     * 
     * @param serviceFilter
     *                a string containing the filter for filtering on the basic
     *                service and site attributes and on related services
     * @param dataFilter
     *                a string containing the filter for filtering on key/value
     *                pairs associated with the service
     * @return list of service descriptions, in a random order, matching the
     *         filter criteria
     * @throws BadParameterException
     *                 if any filter has an invalid syntax or if any filter uses
     *                 invalid keys. However the <code>dataFilter</code> never
     *                 signals invalid keys as there is no schema with
     *                 permissible key names.
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
    public Set<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter) throws BadParameterException,
	    AuthorizationFailedException, AuthenticationFailedException,
	    TimeoutException, NoSuccessException;

    /**
     * Returns the set of services that pass the set of specified filters. A
     * service will only be included once in the returned list of services.
     * 
     * @param serviceFilter
     *                a string containing the filter for filtering on the basic
     *                service and site attributes and on related services
     * @param dataFilter
     *                a string containing the filter for filtering on key/value
     *                pairs associated with the service
     * @param authzFilter
     *                a string containing the filter for filtering on
     *                authorization information associated with the service
     * @return list of service descriptions, in a random order, matching the
     *         filter criteria
     * @throws BadParameterException
     *                 if any filter has an invalid syntax or if any filter uses
     *                 invalid keys. However the <code>dataFilter</code> never
     *                 signals invalid keys as there is no schema with
     *                 permissible key names.
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
    public Set<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter, String authzFilter)
	    throws BadParameterException, AuthorizationFailedException,
	    AuthenticationFailedException, TimeoutException, NoSuccessException;

}
