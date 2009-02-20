package org.ogf.saga.adaptors.glite;

import java.util.Properties;

import org.ogf.saga.context.Context;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.context.ContextImpl;
import org.ogf.saga.impl.context.ContextInitializerSPI;

public class ContextInitializerAdaptor extends AdaptorBase<Object> implements
	ContextInitializerSPI {

    /** The SAGA properties from the properties file. */
    private static final Properties m_properties = org.ogf.saga.bootstrap.SagaProperties
	    .getDefaultProperties();

    public ContextInitializerAdaptor() {
	super(null, null);
    }

    public void setDefaults(ContextImpl context, String type)
	    throws NotImplementedException {

	try {
	    if ("glite".equals(type)) {
		// Default: taken from env variable X509_USER_PROXY if not found
		// then from saga.properties.
		String proxy = System.getenv("X509_USER_PROXY");
		if (proxy == null) {
		    proxy = m_properties.getProperty("x509.user.proxy");
		}
		if (proxy != null) {
		    context.setValueIfEmpty(Context.USERPROXY, proxy);
		}
	    } else {
		throw new NotImplementedException(
			"This adaptor does not recognize the " + type
				+ " context");
	    }
	} catch (NotImplementedException e) {
	    throw e;
	} catch (Throwable e) {
	    // Should not happen.
	    throw new Error("Internal error: got exception", e);
	}
    }
}
