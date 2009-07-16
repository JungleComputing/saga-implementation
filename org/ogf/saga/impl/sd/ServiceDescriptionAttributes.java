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

        addAttribute(ServiceDescription.CAPABILITIES, AttributeType.STRING, VECTOR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);

        addAttribute(ServiceDescription.IMPLEMENTATION_VERSION, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED,
                NOT_REMOVABLE);

        addAttribute(ServiceDescription.IMPLEMENTOR, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED,
                NOT_REMOVABLE);
        try {
            setValue(ServiceDescription.IMPLEMENTOR, "Not Set");
        } catch (BadParameterException e) {
        } catch (DoesNotExistException e) {
        } catch (IncorrectStateException e) {
        } catch (NotImplementedException e) {
        }

        addAttribute(ServiceDescription.INTERFACE_VERSION, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED,
                NOT_REMOVABLE);

        addAttribute(ServiceDescription.INFORMATION_SERVICE_URL, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED,
                NOT_REMOVABLE);

        addAttribute(ServiceDescription.NAME, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
        try {
            setValue(ServiceDescription.NAME, "Not Set");
        } catch (BadParameterException e) {
        } catch (DoesNotExistException e) {
        } catch (IncorrectStateException e) {
        } catch (NotImplementedException e) {
        }

        addAttribute(ServiceDescription.RELATED_SERVICES, AttributeType.STRING, VECTOR, READ_ONLY, IMPLEMENTED,
                NOT_REMOVABLE);

        addAttribute(ServiceDescription.SITE, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
        try {
            setValue(ServiceDescription.SITE, "Not Set");
        } catch (BadParameterException e) {
        } catch (DoesNotExistException e) {
        } catch (IncorrectStateException e) {
        } catch (NotImplementedException e) {
        }

        addAttribute(ServiceDescription.TYPE, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
        try {
            setValue(ServiceDescription.TYPE, "Not Set");
        } catch (BadParameterException e) {
        } catch (DoesNotExistException e) {
        } catch (IncorrectStateException e) {
        } catch (NotImplementedException e) {
        }

        addAttribute(ServiceDescription.UID, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
        try {
            setValue(ServiceDescription.UID, "Not Set");
        } catch (BadParameterException e) {
        } catch (DoesNotExistException e) {
        } catch (IncorrectStateException e) {
        } catch (NotImplementedException e) {
        }

        addAttribute(ServiceDescription.URL, AttributeType.STRING, SCALAR, READ_ONLY, IMPLEMENTED, NOT_REMOVABLE);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adaptors.
     * 
     * @see org.ogf.saga.attributes.Attributes#setValue(java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected synchronized void setValue(String key, String value) throws BadParameterException, DoesNotExistException,
            IncorrectStateException, NotImplementedException {
        super.setValue(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adaptors.
     * 
     * @see org.ogf.saga.attributes.Attributes#setVectorValue(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    protected synchronized void setVectorValue(String key, String[] values) throws BadParameterException,
            DoesNotExistException, IncorrectStateException, NotImplementedException {
        super.setVectorValue(key, values);
    }
}
