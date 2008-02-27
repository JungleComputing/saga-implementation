package org.ogf.saga.spi.stream;

import java.io.IOException;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
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
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.session.Session;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamState;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class ConnectedStream extends SagaObjectBase implements Stream {

    protected URL url;
    protected StreamAttributes attributes;
    protected Metric streamState;
    protected Metric streamRead;
    protected Metric streamWrite;
    protected Metric streamException;
    protected Metric streamDropped;
    
    public ConnectedStream(Session session, URL url) {
        super(session);
        this.url = url;
        attributes = new StreamAttributes(this, session);

        try {
            streamState = new Metric(this, session,
                    org.ogf.saga.stream.Stream.STREAM_STATE,
                    "fires if the state of the stream changes, and has the literal value of the stream state enum",
                    "ReadOnly", "1", "Enum", StreamState.NEW.toString());
            streamRead = new Metric(this, session,
                    org.ogf.saga.stream.Stream.STREAM_READ,
                    "fires if a stream gets readable",
                    "ReadOnly", "1", "Trigger", "1");
            streamWrite = new Metric(this, session,
                    org.ogf.saga.stream.Stream.STREAM_WRITE,
                    "fires if a stream gets writable",
                    "ReadOnly", "1", "Trigger", "1");
            streamException = new Metric(this, session,
                    org.ogf.saga.stream.Stream.STREAM_EXCEPTION,
                    "fires if a stream has an error condition",
                    "ReadOnly", "1", "Trigger", "1");
            streamDropped = new Metric(this, session,
                    org.ogf.saga.stream.Stream.STREAM_DROPPED,
                    "fires if the stream gets dropped by the remote party",
                    "ReadOnly", "1", "Trigger", "1");
        } catch (NotImplementedException e) {
            // Should not happen.
            throw new SagaRuntimeException("Internal error", e);
        } catch (BadParameterException e) {
            // Should not happen.
            throw new SagaRuntimeException("Internal error", e);
        }

    }

    public String[] findAttributes(String... patterns) throws NotImplementedException,
            BadParameterException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplementedException {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public Task<String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplementedException {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public Task setAttribute(TaskMode mode, String key, String value)
            throws NotImplementedException {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, BadParameterException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplementedException {
        return attributes.setVectorAttribute(mode, key, values);
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
        return clone;
    }

    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "close", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task connect(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "connect", new Class[] { });
    }

    public Task<Context> getContext(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Context>(this, session, mode,
                "getContext", new Class[] { });
    }

    public Task<URL> getUrl(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<URL>(this, session, mode,
                "getURL", new Class[] { });
    }

    public Task<Integer> read(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "read", new Class[] { Buffer.class, Integer.TYPE },
                buffer, len);
    }

    public Task<Integer> waitFor(TaskMode mode, int what,
            float timeoutInSeconds) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "waitFor", new Class[] { Integer.TYPE, Float.TYPE },
                what, timeoutInSeconds);
    }

    public Task<Integer> write(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "write", new Class[] { Buffer.class, Integer.TYPE },
                buffer, len);
    }

    public Task<Integer> addCallback(TaskMode mode, String name, Callback cb)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "addCallback", new Class[] { String.class, Callback.class },
                name, cb);
    }

    public Task<org.ogf.saga.monitoring.Metric> getMetric(TaskMode mode, String name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.monitoring.Metric>(this, session, mode,
                "getMetric", new Class[] { String.class }, name);
    }

    public Task<String[]> listMetrics(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String[]>(this, session, mode,
                "listMetrics", new Class[] { });
    }

    public Task removeCallback(TaskMode mode, String name, int cookie)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "removeCallback", new Class[] { String.class, Integer.TYPE },
                name, cookie);
    }

    public int addCallback(String name, Callback cb) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException, IncorrectStateException {
        return getMetric(name).addCallback(cb);
    }

    public Metric getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
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
        throw new DoesNotExistException("metric " + name + " does not exist");
    }

    public String[] listMetrics() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return new String[] {
                org.ogf.saga.stream.Stream.STREAM_DROPPED,
                org.ogf.saga.stream.Stream.STREAM_EXCEPTION,
                org.ogf.saga.stream.Stream.STREAM_WRITE,
                org.ogf.saga.stream.Stream.STREAM_READ,
                org.ogf.saga.stream.Stream.STREAM_STATE
        };
    }

    public void removeCallback(String name, int cookie) throws NotImplementedException,
            DoesNotExistException, BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException {
        getMetric(name).removeCallback(cookie);
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String>(this, session, mode,
                "getGroup", new Class[] { });
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String>(this, session, mode,
                "getOwner", new Class[] { });
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsAllow", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session, mode,
                "permissionsCheck", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsDeny", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public void close() throws NotImplementedException, IncorrectStateException, NoSuccessException {
        close(0.0F);
    }

    public Task close(TaskMode mode) throws NotImplementedException {
        return close(mode, 0.0F);
    }

    public void connect() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        throw new IncorrectStateException("Stream is already connected");
    }

    public int read(Buffer buffer) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException, IOException {
        return read(buffer, -1);
    }

    public Task<Integer> read(TaskMode mode, Buffer buffer)
            throws NotImplementedException {
        return read(mode, buffer, -1);
    }

    public int waitFor(int what) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, NoSuccessException {
        return waitFor(what, -1.0F);
    }

    public Task<Integer> waitFor(TaskMode mode, int what)
            throws NotImplementedException {
        return waitFor(mode, what, -1.0F);
    }

    public int write(Buffer buffer) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException, IOException {
        return write(buffer, -1);
    }

    public Task<Integer> write(TaskMode mode, Buffer buffer)
            throws NotImplementedException {
        return write(mode, buffer, -1);
    }
}
