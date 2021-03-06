package org.ogf.saga.impl.context;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.AttributesImpl;

class ContextAttributes extends AttributesImpl {

    protected ContextAttributes() {
        this(false);
    }

    protected ContextAttributes(boolean autoAdd) {
        super(autoAdd);
        addAttribute(ContextImpl.TYPE, AttributeType.STRING, false, false,
                false, false);
        addAttribute(ContextImpl.SERVER, AttributeType.STRING, false, false,
                false, false);
        addAttribute(ContextImpl.CERTREPOSITORY, AttributeType.STRING, false,
                false, false, false);
        addAttribute(ContextImpl.USERPROXY, AttributeType.STRING, false, false,
                false, false);
        addAttribute(ContextImpl.USERCERT, AttributeType.STRING, false, false,
                false, false);
        addAttribute(ContextImpl.USERKEY, AttributeType.STRING, false, false,
                false, false);
        addAttribute(ContextImpl.USERID, AttributeType.STRING, false, false,
                false, false);
        addAttribute(ContextImpl.USERPASS, AttributeType.STRING, false, false,
                false, false);
        addAttribute(ContextImpl.USERVO, AttributeType.STRING, false, false,
                false, false);

        addAttribute(ContextImpl.LIFETIME, AttributeType.INT, false, false,
                false, false);
        setDefaultValue(ContextImpl.LIFETIME, "-1");
        
        addAttribute(ContextImpl.REMOTEID, AttributeType.STRING, false, true,
                false, false);
        addAttribute(ContextImpl.REMOTEHOST, AttributeType.STRING, false, true,
                false, false);
        addAttribute(ContextImpl.REMOTEPORT, AttributeType.INT, false, true,
                false, false);
    }

    protected ContextAttributes(ContextAttributes orig) {
        super(orig);
    }

    public Object clone() {
        return new ContextAttributes(this);
    }

    // Makes setValue() available for this package.
    protected synchronized void setValue(String key, String value)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        super.setValue(key, value);
    }

    // Makes getValue() available for this package.
    protected synchronized String getValue(String key) {
        return super.getValue(key);
    }

    // Makes setValueIfEmpty available for this package.
    protected void setValueIfEmpty(String userid, String string)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        super.setValueIfEmpty(userid, string);
    }

    // Makes addAttribute available for this package.
    protected synchronized void addAttribute(String name, AttributeType type,
            boolean vector, boolean readOnly, boolean notImplemented,
            boolean removeable) {
        super.addAttribute(name, type, vector, readOnly, notImplemented,
                removeable);
    }
}
