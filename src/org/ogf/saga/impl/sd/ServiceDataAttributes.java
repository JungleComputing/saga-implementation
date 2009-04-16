package org.ogf.saga.impl.sd;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.AttributesImpl;

/**
 * Provides access to the service data attributes.
 * 
 */
public class ServiceDataAttributes extends AttributesImpl {
    protected ServiceDataAttributes() {
    }

    @Override
    protected void addAttribute(String name, AttributeType type,
	    boolean vector, boolean readOnly, boolean notImplemented,
	    boolean removeable) {
	super.addAttribute(name, type, vector, readOnly, notImplemented,
		removeable);
    }

    @Override
    protected synchronized void setValue(String key, String value)
	    throws DoesNotExistException, NotImplementedException,
	    IncorrectStateException, BadParameterException {
	super.setValue(key, value);
    }

    @Override
    protected synchronized void setVectorValue(String key, String[] values)
	    throws DoesNotExistException, NotImplementedException,
	    IncorrectStateException, BadParameterException {
	super.setVectorValue(key, values);
    }

}
