package org.ogf.saga.impl.sd;

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

}
