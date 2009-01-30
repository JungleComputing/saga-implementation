package test.sd;

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
import org.ogf.saga.error.TimeoutException;
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
    private static Logger m_logger = LoggerFactory.getLogger(SDAdaptorTest.class);

    public static void main(String[] args) {
	System.setProperty("Discoverer.adaptor.name", args[0]);
	SDAdaptorTest a = new SDAdaptorTest();
	a.test(args[0], args[1]).print();
    }

    public AdaptorTestResult test(String adaptor, String host) {

	// Create a session and add a context to it
	Session session = null;
	try {
	    session = SessionFactory.createSession(false);
	    Context context = ContextFactory.createContext();
	    session.addContext(context);
	} catch (Throwable e) {
	    throw new Error("Got exception while creating session: ", e);
	}

	// create a url
	URL url = null;
	try {
	    url = URLFactory.createURL("any://" + host);
	} catch (Throwable e) {
	    throw new Error("Got exception while creating url: ", e);
	}

	AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor,
		host);

	// Run through tests
	adaptorTestResult.put("Create Discoverer with default session and url",
		discovererCreationTest());
	adaptorTestResult.put(
		"Create Discoverer with  user defined session and default url",
		discovererCreationTest(session));
	adaptorTestResult.put(
		"Create Discoverer with default session and user defined url",
		discovererCreationTest(url));
	adaptorTestResult.put(
		"Create Discoverer with user defined session and url",
		discovererCreationTest(session, url));
	adaptorTestResult
		.put("listServices with 2 params", listServicesTest1());
	adaptorTestResult
		.put("listServices with 3 params", listServicesTest2());

	return adaptorTestResult;
    }

    private AdaptorTestResultEntry discovererCreationTest() {
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer();
	    checkContext(discoverer);
	} catch (Throwable e) {
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry discovererCreationTest(Session session) {
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session);
	    checkContext(discoverer);
	} catch (Throwable e) {
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry discovererCreationTest(URL url) {
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(url);
	    checkContext(discoverer);
	} catch (Throwable e) {
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry discovererCreationTest(Session session,
	    URL url) {
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer(session, url);
	    checkContext(discoverer);
	} catch (Throwable e) {
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry listServicesTest1() {
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer();
	    Set<ServiceDescription> serviceDescriptions = discoverer
		    .listServices("", "");
	    checkServiceDescription(serviceDescriptions);
	} catch (Throwable e) {
	    return new AdaptorTestResultEntry(false, 0, e);
	}
	long stop = System.currentTimeMillis();
	return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry listServicesTest2() {
	long start = System.currentTimeMillis();
	Discoverer discoverer;
	try {
	    discoverer = SDFactory.createDiscoverer();
	    Set<ServiceDescription> serviceDescriptions = discoverer
		    .listServices("", "", "");
	    checkServiceDescription(serviceDescriptions);
	} catch (Throwable e) {
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
	Session session = discoverer.getSession();
	Context[] contexts = session.listContexts();
	m_logger.debug("Checking discoverer for context");
	for (Context context : contexts) {
	    for (String key : context.listAttributes()) {
		m_logger.debug("context - " + key + ":"
			+ context.getAttribute(key));
	    }
	}
    }

    private void checkServiceDescription(
	    Set<ServiceDescription> serviceDescriptions)
	    throws NotImplementedException, AuthenticationFailedException,
	    AuthorizationFailedException, PermissionDeniedException,
	    TimeoutException, NoSuccessException, IncorrectStateException,
	    DoesNotExistException {
	for (ServiceDescription serviceDescription : serviceDescriptions) {
	    m_logger.debug("Checking serviceDescription for attributes");
	    for (String key : serviceDescription.listAttributes()) {
		if (!(key.equals("relatedServices"))) {
		    m_logger.debug("description - " + key + ":"
			    + serviceDescription.getAttribute(key));
		}
	    }
	    m_logger.debug("data");
	    ServiceData data = serviceDescription.getData();
	    for (String s : data.listAttributes()) {
		m_logger.debug("data - " + s + ":" + data.getAttribute(s));
	    }
	}
    }
}
