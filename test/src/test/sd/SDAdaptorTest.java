package test.sd;

import java.util.List;
import java.util.Set;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.context.ContextImpl;
import org.ogf.saga.sd.Discoverer;
import org.ogf.saga.sd.SDFactory;
import org.ogf.saga.sd.ServiceData;
import org.ogf.saga.sd.ServiceDescription;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.misc.AdaptorTestResult;
import test.misc.AdaptorTestResultEntry;

public class SDAdaptorTest {
    private static Logger m_logger = LoggerFactory
	    .getLogger(SDAdaptorTest.class);

    public static void main(String[] args) {
	System.setProperty("Discoverer.adaptor.name", args[0]);
	SDAdaptorTest a = new SDAdaptorTest();
	a.test(args[0], args[1], args[2]).print();
    }

    public AdaptorTestResult test(String adaptor, String host, String urlString) {

	// Create a session and add a context to it
	Session session = getSession(adaptor);

	// create a url
	URL url = getUrl(urlString);

	AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor,
		host);

	// Run through tests
	adaptorTestResult.put("Create Discoverer with default session and url",
		discovererCreationTest());
	adaptorTestResult.put(
		"Create Discoverer with default session and user defined url",
		discovererCreationTest(url));
	adaptorTestResult.put(
		"Create Discoverer with  user defined session and default url",
		discovererCreationTest(session));
	adaptorTestResult.put(
		"Create Discoverer with user defined session and url",
		discovererCreationTest(session, url));
	adaptorTestResult
		.put("listServices with 2 params", listServicesTest1(session, url));
	adaptorTestResult
		.put("listServices with 3 params", listServicesTest2(session, url));
	adaptorTestResult.put(
		"listServices with 3 params and filter not empty",
		listServicesTest3(session, url));
	adaptorTestResult.put("getRelatedServices with for single service",
		relatedServicesTest(session, url));

	return adaptorTestResult;
    }

    private URL getUrl(String urlString) throws Error {
	URL url = null;
	try {
	    url = URLFactory.createURL(urlString);
	} catch (Throwable e) {
	    throw new Error("Got exception while creating url: ", e);
	}
	return url;
    }

    private Session getSession(String adaptor) throws Error {
	Session session = null;
	try {
	    session = SessionFactory.createSession(true);
	} catch (Throwable e) {
	    throw new Error("Got exception while creating session: ", e);
	}
	ContextImpl context;
	try {
	    context = (ContextImpl) ContextFactory.createContext(adaptor);
	} catch (Throwable e) {
	    throw new Error("Got exception while creating context: ", e);
	}
	context.addAttribute("testVector", AttributeType.STRING, true, false,
		false, false);
	context.addAttribute("testScalar", AttributeType.STRING, false, false,
		false, false);
	String[] s = { "vv1", "vv2" };
	try {
	    context.setVectorAttribute("testVector", s);
	    context.setAttribute("testScalar", "s1");
	} catch (SagaException e) {
	    m_logger.error("Got error setting context attribute:"
		    + e.toString());
	}
	try {
	    session.addContext(context);
	} catch (NotImplementedException e) {
	    throw new Error("Got exception while adding context to session:", e);
	}
	return session;
    }

    private AdaptorTestResultEntry discovererCreationTest() {
	m_logger.info("testing createDiscoverer with default session and url");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer();
	    checkContext(discoverer);
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry discovererCreationTest(Session session) {
	m_logger
		.info("testing createDiscoverer with user defined session and default url");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session, null);
	    checkContext(discoverer);
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry discovererCreationTest(URL url) {
	m_logger
		.info("testing createDiscoverer with default session and user defined url");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(null, url);
	    checkContext(discoverer);
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry discovererCreationTest(Session session,
	    URL url) {
	m_logger
		.info("testing createDiscoverer with user defined session and url");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session, url);
	    checkContext(discoverer);
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry listServicesTest1(Session session, URL url) {
	m_logger.info("testing listServices with 2 filters");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session, url);
	    checkContext(discoverer);
	    List<ServiceDescription> serviceDescriptions = discoverer
		    .listServices("", "");
	    checkServiceDescription(serviceDescriptions);
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry listServicesTest2(Session session, URL url) {
	m_logger.info("testing listServices with 3 filters");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session, url);
	    checkContext(discoverer);
	    List<ServiceDescription> serviceDescriptions = discoverer
		    .listServices("", "", "");
	    checkServiceDescription(serviceDescriptions);
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry listServicesTest3(Session session, URL url) {
	m_logger
		.info("testing listServices with 3 filters and filter not empty");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session, url);
	    checkContext(discoverer);
	    List<ServiceDescription> serviceDescriptions = discoverer
		    .listServices("site='CERN-PROD'", "", "");
	    checkServiceDescription(serviceDescriptions);
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry relatedServicesTest(Session session, URL url) {
	m_logger.info("testing getRelatedServices with for single service");
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session, url);
	    checkContext(discoverer);
	    List<ServiceDescription> serviceDescriptions = discoverer
		    .listServices(
			    "uid='_fts-t0-export.cern.ch_org.glite.FileTransfer'",
			    "", "");
	    checkRelatedService(serviceDescriptions.get(0));
	} catch (Throwable e) {
	    m_logger.info(e.getMessage());
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private void checkContext(Discoverer discoverer)
	    throws DoesNotExistException, NotImplementedException,
	    AuthenticationFailedException, AuthorizationFailedException,
	    PermissionDeniedException, TimeoutException, NoSuccessException,
	    IncorrectStateException {
	m_logger.debug("Checking discoverer for context");
	Session session = discoverer.getSession();
	Context[] contexts = session.listContexts();
	m_logger.info("Found " + contexts.length + " contexts");

	if (contexts.length < 1) {
	    throw new Error("No context found in session");
	}
	for (Context context : contexts) {
	    for (String key : context.listAttributes()) {
		if (context.isVectorAttribute(key)) {
		    m_logger.debug("context - " + key + ":"
			    + context.getVectorAttribute(key));
		} else {
		    m_logger.debug("context - " + key + ":"
			    + context.getAttribute(key));
		}
	    }

	    m_logger.debug("context - UserProxy:"
		    + context.getAttribute("UserProxy"));
	}
    }

    private void checkServiceDescription(
	    List<ServiceDescription> serviceDescriptions)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    TimeoutException, NoSuccessException, IncorrectStateException,
	    DoesNotExistException {

	m_logger.info("Found " + serviceDescriptions.size() + " services");

	for (ServiceDescription serviceDescription : serviceDescriptions) {
	    m_logger.debug("");
	    m_logger
		    .debug("Checking serviceDescription for attributes, should be "
			    + serviceDescription.listAttributes().length);
	    m_logger
		    .debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	    for (String key : serviceDescription.listAttributes()) {
		if (serviceDescription.isVectorAttribute(key)) {
		    m_logger
			    .debug("description - "
				    + key
				    + " size:"
				    + serviceDescription
					    .getVectorAttribute(key).length);
		    for (String vectorValue : serviceDescription
			    .getVectorAttribute(key)) {
			m_logger.debug("description - " + key + ":"
				+ vectorValue);
		    }
		} else {
		    m_logger.debug("description - " + key + ":"
			    + serviceDescription.getAttribute(key));
		}
	    }
	    m_logger.debug("data");
	    ServiceData data = serviceDescription.getData();
	    for (String s : data.listAttributes()) {
		if (data.isVectorAttribute(s)) {
		    for (String s2 : data.getVectorAttribute(s)) {
			m_logger.debug("data - " + s + ":" + s2);
		    }
		} else {
		    m_logger.debug("data - " + s + ":" + data.getAttribute(s));
		}
	    }
	    m_logger.debug("getUrl - " + serviceDescription.getUrl());
	}
    }

    private void checkRelatedService(ServiceDescription serviceDescription)
	    throws AuthorizationFailedException, AuthenticationFailedException,
	    TimeoutException, NoSuccessException {

	Set<ServiceDescription> relatedServices = serviceDescription
		.getRelatedServices();
	m_logger.info("Found " + relatedServices.size() + " related services");

	for (ServiceDescription relatedService : relatedServices) {
	    m_logger
		    .debug("relatedService getUrl - " + relatedService.getUrl());
	}
    }
}
