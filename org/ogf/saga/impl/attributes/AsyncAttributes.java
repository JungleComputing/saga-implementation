package org.ogf.saga.impl.attributes;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class AsyncAttributes<T> extends Attributes implements
        org.ogf.saga.attributes.AsyncAttributes<T> {

    private final Session session;
    private final T object;
    
    public AsyncAttributes(T object, Session session, boolean autoAdd) {
        super(autoAdd);
        this.session = session;
        this.object = object;
    }
    
    protected AsyncAttributes(AsyncAttributes<T> orig) {
        super(orig);
        session = orig.session;
        object = orig.object;
    }
    
    public Object clone() {
        return new AsyncAttributes<T>(this);
    }

    public Task<T, String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, String[]>(
                object, session, mode, "findAttributes",
                new Class[] { String[].class },
                (Object[]) patterns);
    }

    public Task<T, String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, String>(
                    object, session, mode, "getAttribute",
                    new Class[] { String.class },
                    key);
    }

    public Task<T, String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, String[]>(
                object, session, mode, "getVectorAttribute",
                new Class[] { String.class },
                key);
    }


    public Task<T, Boolean> existsAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Boolean>(
                object, session, mode, "existsAttribute",
                new Class[] { String.class },
                key);
    }
    
    public Task<T, Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Boolean>(
                object, session, mode, "isReadOnlyAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Boolean>(
                object, session, mode, "isRemovableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Boolean>(
                object, session, mode, "isVectorAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Boolean>(
                object, session, mode, "isWritableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<T, String[]> listAttributes(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, String[]>(
                object, session, mode, "listAttributes",
                new Class[] { });
    }

    public Task<T, Void> removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Void>(
                object, session, mode, "removeAttributes",
                new Class[] { String.class},
                key);
    }

    public Task<T, Void> setAttribute(TaskMode mode, String key, String value)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Void>(
                object, session, mode, "setAttribute",
                new Class[] { String.class, String.class },
                key, value);
    }

    public Task<T, Void> setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<T, Void>(
                object, session, mode, "setVectorAttribute",
                new Class[] { String.class, String[].class },
                key, (Object) values);
    }

}
