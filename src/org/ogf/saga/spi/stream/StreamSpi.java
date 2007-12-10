package org.ogf.saga.spi.stream;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.proxies.stream.StreamWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class StreamSpi extends AdaptorBase implements StreamSpiInterface {
    
    protected Session session;
    protected URL url;
    protected StreamAttributes attributes;
    protected Metric streamState;
    protected Metric streamRead;
    protected Metric streamWrite;
    protected Metric streamException;
    protected Metric streamDropped;
    
    public StreamSpi(StreamWrapper wrapper, Session session, URL url) throws NotImplemented,
            BadParameter {
        super(wrapper);
        this.session = session;
        this.url = url;
        attributes = new StreamAttributes(wrapper, session);
        streamState = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_STATE,
                "fires if the state of the stream changes, and has the literal value of the stream state enum",
                "ReadOnly", "1", "Enum", "New");
        streamRead = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_READ,
                "fires if a stream gets readable",
                "ReadOnly", "1", "Trigger", "1");
        streamWrite = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_WRITE,
                "fires if a stream gets writable",
                "ReadOnly", "1", "Trigger", "1");
        streamException = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_EXCEPTION,
                "fires if a stream has an error condition",
                "ReadOnly", "1", "Trigger", "1");
        streamDropped = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_DROPPED,
                "fires if the stream gets dropped by the remote party",
                "ReadOnly", "1", "Trigger", "1");
    }
    
    public Object clone() throws CloneNotSupportedException {
        StreamSpi clone = (StreamSpi) super.clone();
        try {
            clone.url = new URL(url.toString());
        } catch (Throwable e) {
            // Should not happen.
            throw new CloneNotSupportedException("Oops");
        }
        clone.attributes = (StreamAttributes) attributes.clone();
        clone.streamState = (Metric) streamState.clone();
        clone.streamRead = (Metric) streamRead.clone();
        clone.streamWrite = (Metric) streamWrite.clone();
        clone.streamException = (Metric) streamException.clone();
        clone.streamDropped = (Metric) streamDropped.clone();
        return clone;
    }

    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "close", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task connect(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "connect", new Class[] { });
    }

    public Task<Context> getContext(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Context>(wrapper, session, mode,
                "getContext", new Class[] { });
    }

    public Task<URL> getUrl(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(wrapper, session, mode,
                "getURL", new Class[] { });
    }

    public Task<Integer> read(TaskMode mode, Buffer buffer, int len)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "read", new Class[] { Buffer.class, Integer.TYPE },
                buffer, len);
    }

    public Task<Integer> waitStream(TaskMode mode, int what,
            float timeoutInSeconds) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "waitStream", new Class[] { Integer.TYPE, Float.TYPE },
                what, timeoutInSeconds);
    }

    public Task<Integer> write(TaskMode mode, Buffer buffer, int len)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "write", new Class[] { Buffer.class, Integer.TYPE },
                buffer, len);
    }

    public Task<Integer> addCallback(TaskMode mode, String name, Callback cb)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "addCallback", new Class[] { String.class, Callback.class },
                name, cb);
    }

    public Task<org.ogf.saga.monitoring.Metric> getMetric(TaskMode mode, String name)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.monitoring.Metric>(wrapper, session, mode,
                "getMetric", new Class[] { String.class }, name);
    }

    public Task<String[]> listMetrics(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String[]>(wrapper, session, mode,
                "listMetrics", new Class[] { });
    }

    public Task removeCallback(TaskMode mode, String name, int cookie)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "removeCallback", new Class[] { String.class, Integer.TYPE },
                name, cookie);
    }

    public int addCallback(String name, Callback cb) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess, IncorrectState {
        return getMetric(name).addCallback(cb);
    }

    public Metric getMetric(String name) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        if (name.equals(org.ogf.saga.stream.Stream.STREAM_DROPPED)) {
            return streamDropped;
        }
        if (name.equals(org.ogf.saga.stream.Stream.STREAM_EXCEPTION)) {
            return streamException;
        }
        if (name.equals(org.ogf.saga.stream.Stream.STREAM_WRITE)) {
            return streamWrite;
        }
        if (name.equals(org.ogf.saga.stream.Stream.STREAM_READ)) {
            return streamRead;
        }
        if (name.equals(org.ogf.saga.stream.Stream.STREAM_STATE)) {
            return streamState;
        }
        throw new DoesNotExist("metric " + name + " does not exist");
    }

    public String[] listMetrics() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return new String[] {
                org.ogf.saga.stream.Stream.STREAM_DROPPED,
                org.ogf.saga.stream.Stream.STREAM_EXCEPTION,
                org.ogf.saga.stream.Stream.STREAM_WRITE,
                org.ogf.saga.stream.Stream.STREAM_READ,
                org.ogf.saga.stream.Stream.STREAM_STATE
        };
    }

    public void removeCallback(String name, int cookie) throws NotImplemented,
            DoesNotExist, BadParameter, Timeout, NoSuccess,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied {
        getMetric(name).removeCallback(cookie);
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(wrapper, session, mode,
                "getGroup", new Class[] { });
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(wrapper, session, mode,
                "getOwner", new Class[] { });
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsAllow", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session, mode,
                "permissionsCheck", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsDeny", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public String[] findAttributes(String... patterns) throws NotImplemented,
            BadParameter, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, Timeout, NoSuccess {
        return attributes.findAttributes(patterns);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplemented {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getAttribute(key);
    }

    public Task<String> getAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isReadOnlyAttribute(key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isRemovableAttribute(key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isVectorAttribute(key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isWritableAttribute(key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return attributes.listAttributes();
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplemented {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        attributes.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        attributes.setAttribute(key, value);
    }

    public Task setAttribute(TaskMode mode, String key, String value)
            throws NotImplemented {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, DoesNotExist,
            Timeout, NoSuccess {
        attributes.setVectorAttribute(key, values);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplemented {
        return attributes.setVectorAttribute(mode, key, values);
    }

}