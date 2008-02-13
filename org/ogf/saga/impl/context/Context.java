package org.ogf.saga.impl.context;

import org.ogf.saga.ObjectType;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.session.Session;

public class Context extends SagaObjectBase
        implements org.ogf.saga.context.Context {

    private final ContextAttributes attributes;

    Context(String type) throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        super((Session) null);
        
        // Allow for a "preferences" extensible context. This allows for
        // passing on adaptor-specific info.
        if ("preferences".equals(type)) {
            attributes = new ContextAttributes(true);
        } else {
            attributes = new ContextAttributes();
        }
        if (type != null && !type.equals("")) {
            try {
                attributes.setValue(TYPE, type);
            } catch (Throwable e) {
                throw new NoSuccess("Oops, could not set TYPE attribute", e);
            }
            setDefaults();
        }
    }
    
    Context(Context orig) {
        super(orig);
        attributes = new ContextAttributes(orig.attributes);
    }

    public Object clone() {
        return new Context(this);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.CONTEXT;
    }

    public void setDefaults() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        String type;
        try {
            type = attributes.getAttribute(TYPE);
        } catch (DoesNotExist e1) {
            throw new IncorrectState(
                    "setDefaults called but TYPE attribute not set");
        } catch (Throwable e) {
            // Should not happen.
            throw new SagaError("could not get TYPE attribute", e);
        }
        try {
            if ("Unknown".equals(type)) {
                // nothing
            } else if ("ftp".equals(type)) {
                // Default is anonymous
                setValue(Context.USERID, "anonymous");
                setValue(Context.USERPASS, "anonymous@localhost");
            } else if ("ssh".equals(type) || "sftp".equals(type)) {
                // setValue(Context.USERID, "");
                // setValue(Context.USERPASS, "");
                // setValue(Context.USERKEY, "");
            } else if ("globus".equals(type) || "gridftp".equals(type)) {
                // Default: taken from .globus dir in user home.
                String home = System.getProperty("user.home");
                setValue(Context.USERKEY, home + "/.globus/userkey.pem");
                setValue(Context.USERCERT, home + "/.globus/usercert.pem");
                // attributes.setValue(Context.USERPASS, "");
            } else if ("preferences".equals(type)) {
                // nothing
            } else if (!type.equals("")) {
                throw new NoSuccess("Unrecognized TYPE attribute value: "
                        + type);
            }
        } catch (DoesNotExist e) {
            // Should not happen.
        } catch (BadParameter e) {
            // Should not happen.
        }
    }

    public String[] findAttributes(String... patterns) throws NotImplemented,
            BadParameter, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, Timeout, NoSuccess {
        return attributes.findAttributes(patterns);
    }

    public String getAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getAttribute(key);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getVectorAttribute(key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isReadOnlyAttribute(key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isRemovableAttribute(key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isVectorAttribute(key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isWritableAttribute(key);
    }

    public String[] listAttributes() throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return attributes.listAttributes();
    }

    public void removeAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        attributes.removeAttribute(key);
    }

    public void setAttribute(String key, String value) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        attributes.setAttribute(key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, DoesNotExist,
            Timeout, NoSuccess {
        attributes.setVectorAttribute(key, values);
    }

    public String getValue(String key) {
        return attributes.getValue(key);
    }

    void setValue(String key, String value) throws DoesNotExist,
            NotImplemented, IncorrectState, BadParameter {
        attributes.setValue(key, value);
    }
}
