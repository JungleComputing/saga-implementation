package org.ogf.saga.impl.attributes;

import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class AsyncAttributes extends Attributes implements
        org.ogf.saga.attributes.AsyncAttributes {

    private final Session session;
    private final Object object;
    
    public AsyncAttributes(Object object, Session session, boolean autoAdd) {
        super(autoAdd);
        this.session = session;
        this.object = object;
    }
    
    protected AsyncAttributes(AsyncAttributes orig) {
        super(orig);
        session = orig.session;
        object = orig.object;
    }
    
    public Object clone() {
        return new AsyncAttributes(this);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String[]>(
                object, session, mode, "findAttributes",
                new Class[] { String[].class },
                (Object[]) patterns);
    }

    public Task<String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String>(
                    object, session, mode, "getAttribute",
                    new Class[] { String.class },
                    key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String[]>(
                object, session, mode, "getVectorAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                object, session, mode, "isReadOnlyAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                object, session, mode, "isRemovableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                object, session, mode, "isVectorAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                object, session, mode, "isWritableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String[]>(
                object, session, mode, "listAttributes",
                new Class[] { });
    }

    public Task removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(
                object, session, mode, "removeAttributes",
                new Class[] { String.class},
                key);
    }

    public Task setAttribute(TaskMode mode, String key, String value)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(
                object, session, mode, "setAttribute",
                new Class[] { String.class, String.class },
                key, value);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(
                object, session, mode, "setVectorAttribute",
                new Class[] { String.class, String[].class },
                key, (Object) values);
    }
}
