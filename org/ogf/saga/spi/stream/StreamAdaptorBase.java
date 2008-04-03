package org.ogf.saga.spi.stream;

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
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.proxies.stream.StreamWrapper;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamInputStream;
import org.ogf.saga.stream.StreamOutputStream;
import org.ogf.saga.stream.StreamState;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class StreamAdaptorBase extends AdaptorBase<Stream> implements
        StreamSPI {

    protected URL url;
    protected StreamAttributes attributes;
    protected Metric streamState;
    protected Metric streamRead;
    protected Metric streamWrite;
    protected Metric streamException;
    protected Metric streamDropped;

    public StreamAdaptorBase(StreamWrapper wrapper, Session session, URL url)
            throws NotImplementedException, BadParameterException {
        super(session, wrapper);
        this.url = url;
        attributes = new StreamAttributes(wrapper, session);
        streamState = new Metric(
                wrapper,
                session,
                org.ogf.saga.stream.Stream.STREAM_STATE,
                "fires if the state of the stream changes, and has the literal value of the stream state enum",
                "ReadOnly", "1", "Enum", StreamState.NEW.toString());
        streamRead = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_READ,
                "fires if a stream gets readable", "ReadOnly", "1", "Trigger",
                "1");
        streamWrite = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_WRITE,
                "fires if a stream gets writable", "ReadOnly", "1", "Trigger",
                "1");
        streamException = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_EXCEPTION,
                "fires if a stream has an error condition", "ReadOnly", "1",
                "Trigger", "1");
        streamDropped = new Metric(wrapper, session,
                org.ogf.saga.stream.Stream.STREAM_DROPPED,
                "fires if the stream gets dropped by the remote party",
                "ReadOnly", "1", "Trigger", "1");
    }

    public Object clone() throws CloneNotSupportedException {
        StreamAdaptorBase clone = (StreamAdaptorBase) super.clone();
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

    protected void checkBufferType(Buffer buffer) {
        if (!(buffer instanceof org.ogf.saga.impl.buffer.Buffer)) {
            throw new SagaRuntimeException("Wrong buffer type: "
                    + buffer.getClass().getName());
        }
    }

    public Task<Stream, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Void>(wrapper, session,
                mode, "close", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task<Stream, Void> connect(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Void>(wrapper, session,
                mode, "connect", new Class[] {});
    }

    public Task<Stream, Context> getContext(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Context>(wrapper,
                session, mode, "getContext", new Class[] {});
    }

    public Task<Stream, URL> getUrl(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, URL>(wrapper, session,
                mode, "getURL", new Class[] {});
    }

    public Task<Stream, Integer> read(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Integer>(wrapper,
                session, mode, "read",
                new Class[] { Buffer.class, Integer.TYPE }, buffer, len);
    }

    public Task<Stream, Integer> waitFor(TaskMode mode, int what,
            float timeoutInSeconds) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Integer>(wrapper,
                session, mode, "waitFor", new Class[] { Integer.TYPE,
                        Float.TYPE }, what, timeoutInSeconds);
    }

    public Task<Stream, Integer> write(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Integer>(wrapper,
                session, mode, "write", new Class[] { Buffer.class,
                        Integer.TYPE }, buffer, len);
    }

    public Task<Stream, Integer> addCallback(TaskMode mode, String name,
            Callback cb) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Integer>(wrapper,
                session, mode, "addCallback", new Class[] { String.class,
                        Callback.class }, name, cb);
    }

    public Task<Stream, org.ogf.saga.monitoring.Metric> getMetric(
            TaskMode mode, String name) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, org.ogf.saga.monitoring.Metric>(
                wrapper, session, mode, "getMetric",
                new Class[] { String.class }, name);
    }

    public Task<Stream, String[]> listMetrics(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, String[]>(wrapper,
                session, mode, "listMetrics", new Class[] {});
    }

    public Task<Stream, Void> removeCallback(TaskMode mode, String name,
            int cookie) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Void>(wrapper, session,
                mode, "removeCallback", new Class[] { String.class,
                        Integer.TYPE }, name, cookie);
    }

    public int addCallback(String name, Callback cb)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectStateException {
        return getMetric(name).addCallback(cb);
    }

    public Metric getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
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

    public String[] listMetrics() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return new String[] { org.ogf.saga.stream.Stream.STREAM_DROPPED,
                org.ogf.saga.stream.Stream.STREAM_EXCEPTION,
                org.ogf.saga.stream.Stream.STREAM_WRITE,
                org.ogf.saga.stream.Stream.STREAM_READ,
                org.ogf.saga.stream.Stream.STREAM_STATE };
    }

    public void removeCallback(String name, int cookie)
            throws NotImplementedException, DoesNotExistException,
            BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException {
        getMetric(name).removeCallback(cookie);
    }

    public Task<Stream, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, String>(wrapper,
                session, mode, "getGroup", new Class[] {});
    }

    public Task<Stream, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, String>(wrapper,
                session, mode, "getOwner", new Class[] {});
    }

    public Task<Stream, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Void>(wrapper, session,
                mode, "permissionsAllow", new Class[] { String.class,
                        Integer.TYPE }, id, permissions);
    }

    public Task<Stream, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Boolean>(wrapper,
                session, mode, "permissionsCheck", new Class[] { String.class,
                        Integer.TYPE }, id, permissions);
    }

    public Task<Stream, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, Void>(wrapper, session,
                mode, "permissionsDeny", new Class[] { String.class,
                        Integer.TYPE }, id, permissions);
    }

    public String[] findAttributes(String... patterns)
            throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public Task<Stream, String[]> findAttributes(TaskMode mode,
            String... patterns) throws NotImplementedException {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public Task<Stream, String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public Task<Stream, String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public Task<Stream, Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public Task<Stream, Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public Task<Stream, Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public Task<Stream, Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public Task<Stream, String[]> listAttributes(TaskMode mode)
            throws NotImplementedException {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        attributes.removeAttribute(key);
    }

    public Task<Stream, Void> removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public Task<Stream, Void> setAttribute(TaskMode mode, String key,
            String value) throws NotImplementedException {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public Task<Stream, Void> setVectorAttribute(TaskMode mode, String key,
            String[] values) throws NotImplementedException {
        return attributes.setVectorAttribute(mode, key, values);
    }

    public Task<Stream, StreamInputStream> getInputStream(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, StreamInputStream>(
                wrapper, session, mode, "getInputStream", new Class[] {});
    }

    public Task<Stream, StreamOutputStream> getOutputStream(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Stream, StreamOutputStream>(
                wrapper, session, mode, "getOutputStream", new Class[] {});
    }

}
