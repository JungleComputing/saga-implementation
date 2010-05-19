package org.ogf.saga.impl.context;

import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.session.Session;

public class ContextImpl extends SagaObjectBase implements
        org.ogf.saga.context.Context {

    private static ContextInitializerSPI proxy = null;
    private ContextAttributes attributes;

    static {
        Object[] parameters = {};
        Class<?>[] parameterClasses = {};
        try {
            proxy = (ContextInitializerSPI) SAGAEngine.createAdaptorProxy(
                    ContextInitializerSPI.class, parameterClasses, parameters);
        } catch (Throwable e) {
            // What to do with this?
        }
    }

    protected ContextImpl(String type) throws IncorrectStateException,
            TimeoutException, NoSuccessException {
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
                throw new NoSuccessException(
                        "Oops, could not set TYPE attribute", e);
            }
        }
    }

    public ContextImpl(ContextImpl orig) {
        super(orig, false);
        attributes = new ContextAttributes(orig.attributes);
    }

    public boolean equals(Object o) {
        if (!(o instanceof ContextImpl)) {
            return false;
        }
        ContextImpl ctxt = (ContextImpl) o;
        return attributes.equals(ctxt.attributes);
    }

    public int hashCode() {
        return attributes.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        ContextImpl o = (ContextImpl) super.clone();
        o.attributes = new ContextAttributes(attributes);
        return o;
    }

    public void setDefaults() throws NoSuccessException {
        String type;
        try {
            type = attributes.getAttribute(TYPE);
        } catch (DoesNotExistException e1) {
            return;
        } catch (Throwable e) {
            // Should not happen.
            throw new SagaRuntimeException("could not get TYPE attribute", e);
        }

        if ("Unknown".equals(type) || "".equals(type)) {
            // nothing.
        } else if ("preferences".equals(type)) {
            // Special context, no defaults.
        } else {
            try {
                proxy.setDefaults(this, type);
            } catch (Throwable e) {
                throw new NoSuccessException(
                        "Unrecognized TYPE attribute value: " + type);
            }
        }
    }

    public String[] findAttributes(String... patterns)
            throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public String[] getVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public boolean existsAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.existsAttribute(key);
    }

    public boolean isReadOnlyAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public boolean isRemovableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public boolean isVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public boolean isWritableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        attributes.removeAttribute(key);
    }

    public void setAttribute(String key, String value)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public String getValue(String key) {
        return attributes.getValue(key);
    }

    public void setValue(String key, String value)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        attributes.setValue(key, value);
    }

    public void setValueIfEmpty(String key, String value)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        attributes.setValueIfEmpty(key, value);
    }

    // Allows the addition of adaptor-specific attributes.
    public void addAttribute(String name, AttributeType type, boolean vector,
            boolean readOnly, boolean notImplemented, boolean removeable) {
        attributes.addAttribute(name, type, vector, readOnly, notImplemented,
                removeable);
    }
}
