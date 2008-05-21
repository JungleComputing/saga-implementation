package org.ogf.saga.impl.context;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.Attributes;

class ContextAttributes extends Attributes {
    
    ContextAttributes() {
        this(false);
    }

    ContextAttributes(boolean autoAdd) {
        super(autoAdd);
        addAttribute(Context.TYPE, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.SERVER, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.CERTREPOSITORY, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.USERPROXY, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.USERCERT, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.USERKEY, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.USERID, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.USERPASS, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.USERVO, AttributeType.STRING, false, false, false, false);
        addAttribute(Context.LIFETIME, AttributeType.INT, false, false, false, false);
        addAttribute(Context.REMOTEID, AttributeType.STRING, false, true, false, false);
        addAttribute(Context.REMOTEHOST, AttributeType.STRING, false, true, false, false);
        addAttribute(Context.REMOTEPORT, AttributeType.INT, false, true, false, false);

        try {
            setValue(Context.LIFETIME, "-1");
        } catch(Throwable e) {
            throw new SagaRuntimeException("Internal error", e);
        }
    }
    
    ContextAttributes(ContextAttributes orig) {
        super(orig);
    }
    
    public Object clone() {
        return new ContextAttributes(this);
    }

    // Makes setValue() available for this package.
    protected void setValue(String key, String value)
            throws DoesNotExistException, NotImplementedException, IncorrectStateException, BadParameterException {
        super.setValue(key, value);
    }
    
    // Makes getValue() available for this package.
    protected String getValue(String key) {
        return super.getValue(key);
    }

    public void setValueIfEmpty(String userid, String string)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        super.setValueIfEmpty(userid, string);

    }
}
