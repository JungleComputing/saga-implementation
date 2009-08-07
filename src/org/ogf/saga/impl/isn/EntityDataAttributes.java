package org.ogf.saga.impl.isn;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.AttributesImpl;

/**
 * Provides access to the entity data attributes.
 * 
 */
public final class EntityDataAttributes extends AttributesImpl {

    /**
     * Construct a new EntityDataAttributes.
     * 
     */
    protected EntityDataAttributes() {
    }

    /**
     * Constructor for use when cloning.
     * 
     * @param orig
     *            the original object
     */
    protected EntityDataAttributes(EntityDataAttributes orig) {
        super(orig);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adapters.
     * 
     * @see org.ogf.saga.attributes.Attributes#addAttribute(java.lang.String,
     *      org.ogf.saga.attributes.AttributeType, boolean, boolean, boolean,
     *      boolean)
     */
    @Override
    protected void addAttribute(String name, AttributeType type, boolean vector, boolean readOnly,
            boolean notImplemented, boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adapters.
     * 
     * @see org.ogf.saga.attributes.Attributes#setValue(java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected synchronized void setValue(String key, String value) throws DoesNotExistException,
            NotImplementedException, IncorrectStateException, BadParameterException {
        super.setValue(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * This method has been made available for use by adapters.
     * 
     * @see org.ogf.saga.attributes.Attributes#setVectorValue(java.lang.String,
     *      java.lang.String[])
     */
    @Override
    protected synchronized void setVectorValue(String key, String[] values) throws DoesNotExistException,
            NotImplementedException, IncorrectStateException, BadParameterException {
        super.setVectorValue(key, values);
    }

}
