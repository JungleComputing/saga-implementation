package org.ogf.saga.adaptors.glite.sd;

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
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.sd.ServiceDataImpl;
import org.ogf.saga.impl.sd.ServiceDescriptionImpl;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.sd.DiscovererWrapper;
import org.ogf.saga.sd.ServiceData;
import org.ogf.saga.sd.ServiceDescription;
import org.ogf.saga.spi.sd.DiscovererAdaptorBase;
import org.ogf.saga.url.URL;

/**
 * This is the gLite service discovery adaptor. It is used to communicate with
 * the gLite C++ implementation.
 * 
 */
public class DiscovererAdaptor extends DiscovererAdaptorBase {

    /**
     * @param wrapper
     *                the wrapper object
     * @param sessionImpl
     *                the session of the adaptor
     * @param infoSystemUrl
     *                the url of the information system
     */
    public DiscovererAdaptor(DiscovererWrapper wrapper,
	    SessionImpl sessionImpl, URL infoSystemUrl) {
	super(wrapper, sessionImpl, infoSystemUrl);
    }

    @Override
    public Set<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter) throws BadParameterException,
	    AuthorizationFailedException, AuthenticationFailedException,
	    TimeoutException, NoSuccessException {
	Set<ServiceDescription> serviceDescriptions = new HashSet<ServiceDescription>();

	// TODO need code to interface with c++ adaptor
	serviceDescriptions.add(createDummyServiceDescription());
	serviceDescriptions.add(createDummyServiceDescription());
	serviceDescriptions.add(createDummyServiceDescription());

	return serviceDescriptions;
    }

    @Override
    public Set<ServiceDescription> listServices(String serviceFilter,
	    String dataFilter, String authzFilter)
	    throws BadParameterException, AuthorizationFailedException,
	    AuthenticationFailedException, TimeoutException, NoSuccessException {
	Set<ServiceDescription> serviceDescriptions = new HashSet<ServiceDescription>();

	// TODO need code to interface with c++ adaptor
	serviceDescriptions.add(createDummyServiceDescription());
	serviceDescriptions.add(createDummyServiceDescription());
	serviceDescriptions.add(createDummyServiceDescription());

	return serviceDescriptions;
    }

    // temporary method to provide some testing data
    private ServiceDescription createDummyServiceDescription() {
	ServiceDataImpl serviceData = new ServiceDataImpl();
	serviceData.addAttribute("A1", AttributeType.STRING, false, true,
		false, false);
	try {
	    serviceData.setAttribute("A1", "test");
	} catch (NotImplementedException e) {
	} catch (PermissionDeniedException e) {
	} catch (IncorrectStateException e) {
	} catch (DoesNotExistException e) {
	} catch (AuthenticationFailedException e) {
	} catch (AuthorizationFailedException e) {
	} catch (BadParameterException e) {
	} catch (TimeoutException e) {
	} catch (NoSuccessException e) {
	}
	ServiceDescriptionImpl serviceDescription = new ServiceDescriptionImpl(
		(ServiceData)serviceData);

	try {
	    serviceDescription.setValue(ServiceDescription.URL,
		    "http://sdAdaptor");
	    serviceDescription.setValue(ServiceDescription.TYPE, "CE");
	} catch (NotImplementedException e) {
	} catch (IncorrectStateException e) {
	} catch (DoesNotExistException e) {
	} catch (BadParameterException e) {
	}
	return (ServiceDescription) serviceDescription;
    }

}
