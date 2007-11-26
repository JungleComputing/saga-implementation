package org.ogf.saga.impl.attributes;

import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class AsyncAttributes extends Attributes implements
        org.ogf.saga.attributes.AsyncAttributes {

    private Session session;
    
    public AsyncAttributes(Session session, boolean autoAdd) {
        super(autoAdd);
        this.session = session;
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String[]>(
                this, session, mode, "findAttributes",
                new Class[] { String[].class },
                (Object[]) patterns);
    }

    public Task<String> getAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(
                    this, session, mode, "getAttribute",
                    new Class[] { String.class },
                    key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String[]>(
                this, session, mode, "getVectorAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                this, session, mode, "isReadOnlyAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                this, session, mode, "isRemovableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                this, session, mode, "isVectorAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(
                this, session, mode, "isWritableAttribute",
                new Class[] { String.class },
                key);
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String[]>(
                this, session, mode, "listAttributes",
                new Class[] { });
    }

    public Task removeAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(
                this, session, mode, "removeAttributes",
                new Class[] { String.class},
                key);
    }

    public Task setAttribute(TaskMode mode, String key, String value)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(
                this, session, mode, "setAttribute",
                new Class[] { String.class, String.class },
                key, value);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(
                this, session, mode, "setVectorAttribute",
                new Class[] { String.class, String[].class },
                key, (Object) values);
    }
}
