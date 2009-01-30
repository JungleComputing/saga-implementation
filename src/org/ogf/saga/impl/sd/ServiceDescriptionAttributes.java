package org.ogf.saga.impl.sd;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.AttributesImpl;
import org.ogf.saga.sd.ServiceDescription;

/**
 * Provides access to the service description attributes.
 * 
 */
public class ServiceDescriptionAttributes extends AttributesImpl {

    /** The attribute is implemented */
    private static final boolean IMPLEMENTED = false;

    /** The attribute is not removable */
    private static final boolean NOT_REMOVABLE = false;

    /** The attribute is read only */
    private static final boolean READ_ONLY = true;

    /** The attribute is a scalar */
    private static final boolean SCALAR = false;

    /** The attribute is a vector */
    private static final boolean VECTOR = true;

    /**
     * Set up the service description attributes. The specification states that
     * a number of these attributes MUST NOT be empty strings. These attributes
     * have been given the default value of "Not Set".
     * 
     */
    protected ServiceDescriptionAttributes() {
	addAttribute(ServiceDescription.URL, AttributeType.STRING, SCALAR,
		READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);

	addAttribute(ServiceDescription.TYPE, AttributeType.STRING, SCALAR,
		READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
	try {
	    setValue(ServiceDescription.TYPE, "Not Set");
	} catch (DoesNotExistException e) {
	} catch (NotImplementedException e) {
	} catch (IncorrectStateException e) {
	} catch (BadParameterException e) {
	}

	addAttribute(ServiceDescription.UID, AttributeType.STRING, SCALAR,
		READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
	try {
	    setValue(ServiceDescription.UID, "Not Set");
	} catch (DoesNotExistException e) {
	} catch (NotImplementedException e) {
	} catch (IncorrectStateException e) {
	} catch (BadParameterException e) {
	}

	addAttribute(ServiceDescription.SITE, AttributeType.STRING, SCALAR,
		READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
	try {
	    setValue(ServiceDescription.SITE, "Not Set");
	} catch (DoesNotExistException e) {
	} catch (NotImplementedException e) {
	} catch (IncorrectStateException e) {
	} catch (BadParameterException e) {
	}

	addAttribute(ServiceDescription.NAME, AttributeType.STRING, SCALAR,
		READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
	try {
	    setValue(ServiceDescription.NAME, "Not Set");
	} catch (DoesNotExistException e) {
	} catch (NotImplementedException e) {
	} catch (IncorrectStateException e) {
	} catch (BadParameterException e) {
	}

	addAttribute(ServiceDescription.IMPLEMENTOR, AttributeType.STRING,
		SCALAR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
	try {
	    setValue(ServiceDescription.IMPLEMENTOR, "Not Set");
	} catch (DoesNotExistException e) {
	} catch (NotImplementedException e) {
	} catch (IncorrectStateException e) {
	} catch (BadParameterException e) {
	}

	addAttribute(ServiceDescription.RELATED_SERVICES, AttributeType.STRING,
		VECTOR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);

    }

    @Override
    protected synchronized void setValue(String key, String value)
	    throws DoesNotExistException, NotImplementedException,
	    IncorrectStateException, BadParameterException {
	super.setValue(key, value);
    }
}
