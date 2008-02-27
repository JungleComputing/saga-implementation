package org.ogf.saga.impl.context;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.session.Session;

public class Context extends SagaObjectBase
        implements org.ogf.saga.context.Context {

    private final ContextAttributes attributes;

    Context(String type) throws NotImplementedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
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
                throw new NoSuccessException("Oops, could not set TYPE attribute", e);
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

    public void setDefaults() throws NotImplementedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        String type;
        try {
            type = attributes.getAttribute(TYPE);
        } catch (DoesNotExistException e1) {
            throw new IncorrectStateException(
                    "setDefaults called but TYPE attribute not set");
        } catch (Throwable e) {
            // Should not happen.
            throw new SagaRuntimeException("could not get TYPE attribute", e);
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
                throw new NoSuccessException("Unrecognized TYPE attribute value: "
                        + type);
            }
        } catch (DoesNotExistException e) {
            // Should not happen.
        } catch (BadParameterException e) {
            // Should not happen.
        }
    }

    public String[] findAttributes(String... patterns) throws NotImplementedException,
            BadParameterException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public String[] getVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public boolean isVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public boolean isWritableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.removeAttribute(key);
    }

    public void setAttribute(String key, String value) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, BadParameterException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public String getValue(String key) {
        return attributes.getValue(key);
    }

    void setValue(String key, String value) throws DoesNotExistException,
            NotImplementedException, IncorrectStateException, BadParameterException {
        attributes.setValue(key, value);
    }
}
