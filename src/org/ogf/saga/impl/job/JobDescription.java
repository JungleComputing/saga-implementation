package org.ogf.saga.impl.job;

import org.ogf.saga.ObjectType;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.SagaObjectBase;

public class JobDescription extends SagaObjectBase implements
        org.ogf.saga.job.JobDescription {

    JobDescriptionAttributes attributes = new JobDescriptionAttributes();
    
    public JobDescription() {
        super(null);
    }

    public Object clone() throws CloneNotSupportedException {
        JobDescription clone = (JobDescription) super.clone();
        clone.attributes = (JobDescriptionAttributes) attributes.clone();
        return clone;
    }
    
    public ObjectType getType() {
        return ObjectType.JOBDESCRIPTION;
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


}
