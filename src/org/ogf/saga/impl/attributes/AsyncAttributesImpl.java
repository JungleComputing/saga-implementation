package org.ogf.saga.impl.attributes;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class AsyncAttributesImpl<T> extends AttributesImpl implements
        org.ogf.saga.attributes.AsyncAttributes<T> {

    private final Session session;
    private final T object;
    
    public AsyncAttributesImpl(T object, Session session, boolean autoAdd) {
        super(autoAdd);
        this.session = session;
        this.object = object;
    }
    
    protected AsyncAttributesImpl(AsyncAttributesImpl<T> orig) {
        super(orig);
        session = orig.session;
        object = orig.object;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(Object o) {
        if (! (o instanceof AsyncAttributesImpl)) {
            return false;
        }
        AsyncAttributesImpl a = (AsyncAttributesImpl) o;
        if (session != null) {
            if (! session.equals(a.session)) {
                return false;
            }
        } else if (a.session != null) {
            return false;
        }
        if (object != null) {
            if (! object.equals(a.object)) {
                return false;
            }
        } else if (a.object != null) {
            return false;
        }      
        return super.equals(o);
    }
    
    public int hashCode() {
        return super.hashCode();
    }

    public Task<T, String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, String[]>(
                object, session, mode, "findAttributes",
                new Class[] { String[].class },
                (Object[]) patterns);
    }

    public Task<T, String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, String>(
                    object, session, mode, "getAttribute",
                    new Class[] { String.class },
                    key);
    }

    public Task<T, String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, String[]>(
                object, session, mode, "getVectorAttribute",
                new Class[] { String.class },
                key);
    }


    public Task<T, Boolean> existsAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Boolean>(
                object, session, mode, "existsAttribute",
                new Class[] { String.class },
                key);
    }
    
    public Task<T, Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Boolean>(
                object, session, mode, "isReadOnlyAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Boolean>(
                object, session, mode, "isRemovableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Boolean>(
                object, session, mode, "isVectorAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Boolean>(
                object, session, mode, "isWritableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, String[]> listAttributes(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, String[]>(
                object, session, mode, "listAttributes",
                new Class[] { });
    }

    public Task<T, Void> removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Void>(
                object, session, mode, "removeAttributes",
                new Class[] { String.class},
                key);
    }

    public Task<T, Void> setAttribute(TaskMode mode, String key, String value)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Void>(
                object, session, mode, "setAttribute",
                new Class[] { String.class, String.class },
                key, value);
    }

    public Task<T, Void> setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<T, Void>(
                object, session, mode, "setVectorAttribute",
                new Class[] { String.class, String[].class },
                key, (Object) values);
    }

}
