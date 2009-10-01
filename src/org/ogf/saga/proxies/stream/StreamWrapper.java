package org.ogf.saga.proxies.stream;

import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.stream.StreamSPI;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamInputStream;
import org.ogf.saga.stream.StreamOutputStream;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class StreamWrapper extends SagaObjectBase implements Stream {

    private StreamSPI proxy;

    public StreamWrapper(Session session, URL name)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        super(session);
        Object[] parameters = { this, session, name };
        try {
            proxy = (StreamSPI) SAGAEngine.createAdaptorProxy(StreamSPI.class,
                    new Class[] { StreamWrapper.class,
                            org.ogf.saga.impl.session.SessionImpl.class,
                            URL.class }, parameters);
        } catch (org.ogf.saga.error.SagaException e) {
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
            }
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            if (e instanceof AuthenticationFailedException) {
                throw (AuthenticationFailedException) e;
            }
            if (e instanceof AuthorizationFailedException) {
                throw (AuthorizationFailedException) e;
            }
            if (e instanceof PermissionDeniedException) {
                throw (PermissionDeniedException) e;
            }
            if (e instanceof BadParameterException) {
                throw (BadParameterException) e;
            }
            if (e instanceof TimeoutException) {
                throw (TimeoutException) e;
            }
            if (e instanceof NoSuccessException) {
                throw (NoSuccessException) e;
            }
            throw new NoSuccessException("Constructor failed", e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        StreamWrapper clone = (StreamWrapper) super.clone();
        clone.proxy = (StreamSPI) SAGAEngine.createAdaptorCopy(StreamSPI.class,
                proxy, clone);
        return clone;
    }

    public int addCallback(String name, Callback cb)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectStateException {
        return proxy.addCallback(name, cb);
    }

    public Task<Stream, Integer> addCallback(TaskMode mode, String name,
            Callback cb) throws NotImplementedException {
        return proxy.addCallback(mode, name, cb);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        proxy.close(timeoutInSeconds);
    }

    public Task<Stream, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return proxy.close(mode, timeoutInSeconds);
    }

    public void connect() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        proxy.connect();
    }

    public Task<Stream, Void> connect(TaskMode mode)
            throws NotImplementedException {
        return proxy.connect(mode);
    }

    public String[] findAttributes(String... patterns)
            throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.findAttributes(patterns);
    }

    public Task<Stream, String[]> findAttributes(TaskMode mode,
            String... patterns) throws NotImplementedException {
        return proxy.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.getAttribute(key);
    }

    public Task<Stream, String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.getAttribute(mode, key);
    }

    public Context getContext() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.getContext();
    }

    public Task<Stream, Context> getContext(TaskMode mode)
            throws NotImplementedException {
        return proxy.getContext(mode);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getGroup();
    }

    public Task<Stream, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return proxy.getGroup(mode);
    }

    public Metric getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return proxy.getMetric(name);
    }

    public Task<Stream, Metric> getMetric(TaskMode mode, String name)
            throws NotImplementedException {
        return proxy.getMetric(mode, name);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getOwner();
    }

    public Task<Stream, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return proxy.getOwner(mode);
    }

    public URL getUrl() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.getUrl();
    }

    public Task<Stream, URL> getUrl(TaskMode mode)
            throws NotImplementedException {
        return proxy.getUrl(mode);
    }

    public String[] getVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return proxy.getVectorAttribute(key);
    }

    public Task<Stream, String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isReadOnlyAttribute(key);
    }

    public Task<Stream, Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.isReadOnlyAttribute(mode, key);
    }

    public boolean existsAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.existsAttribute(key);
    }

    public Task<Stream, Boolean> existsAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.existsAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isRemovableAttribute(key);
    }

    public Task<Stream, Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isVectorAttribute(key);
    }

    public Task<Stream, Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isWritableAttribute(key);
    }

    public Task<Stream, Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.listAttributes();
    }

    public Task<Stream, String[]> listAttributes(TaskMode mode)
            throws NotImplementedException {
        return proxy.listAttributes(mode);
    }

    public String[] listMetrics() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.listMetrics();
    }

    public Task<Stream, String[]> listMetrics(TaskMode mode)
            throws NotImplementedException {
        return proxy.listMetrics(mode);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsAllow(id, permissions);
    }

    public Task<Stream, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<Stream, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsDeny(id, permissions);
    }

    public Task<Stream, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public int read(Buffer buffer, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return proxy.read(buffer, len);
    }

    public Task<Stream, Integer> read(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return proxy.read(mode, buffer, len);
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        proxy.removeAttribute(key);
    }

    public Task<Stream, Void> removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.removeAttribute(mode, key);
    }

    public void removeCallback(String name, int cookie)
            throws NotImplementedException, DoesNotExistException,
            BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException {
        proxy.removeCallback(name, cookie);
    }

    public Task<Stream, Void> removeCallback(TaskMode mode, String name,
            int cookie) throws NotImplementedException {
        return proxy.removeCallback(mode, name, cookie);
    }

    public void setAttribute(String key, String value)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.setAttribute(key, value);
    }

    public Task<Stream, Void> setAttribute(TaskMode mode, String key,
            String value) throws NotImplementedException {
        return proxy.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.setVectorAttribute(key, values);
    }

    public Task<Stream, Void> setVectorAttribute(TaskMode mode, String key,
            String[] values) throws NotImplementedException {
        return proxy.setVectorAttribute(mode, key, values);
    }

    public int waitFor(int what, float timeoutInSeconds)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, NoSuccessException {
        return proxy.waitFor(what, timeoutInSeconds);
    }

    public Task<Stream, Integer> waitFor(TaskMode mode, int what,
            float timeoutInSeconds) throws NotImplementedException {
        return proxy.waitFor(mode, what, timeoutInSeconds);
    }

    public int write(Buffer buffer, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return proxy.write(buffer, len);
    }

    public Task<Stream, Integer> write(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return proxy.write(mode, buffer, len);
    }

    public void close() throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        close(0.0F);
    }

    public Task<Stream, Void> close(TaskMode mode)
            throws NotImplementedException {
        return close(mode, 0.0F);
    }

    public int read(Buffer buffer) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return read(buffer, -1);
    }

    public Task<Stream, Integer> read(TaskMode mode, Buffer buffer)
            throws NotImplementedException {
        return read(mode, buffer, -1);
    }

    public int waitFor(int what) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            NoSuccessException {
        return waitFor(what, -1.0F);
    }

    public Task<Stream, Integer> waitFor(TaskMode mode, int what)
            throws NotImplementedException {
        return waitFor(mode, what, -1.0F);
    }

    public int write(Buffer buffer) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return write(buffer, -1);
    }

    public Task<Stream, Integer> write(TaskMode mode, Buffer buffer)
            throws NotImplementedException {
        return write(mode, buffer, -1);
    }

    public StreamInputStream getInputStream() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException, SagaIOException {
        return proxy.getInputStream();
    }

    public Task<Stream, StreamInputStream> getInputStream(TaskMode mode)
            throws NotImplementedException {
        return proxy.getInputStream(mode);
    }

    public StreamOutputStream getOutputStream() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException, SagaIOException {
        return proxy.getOutputStream();
    }

    public Task<Stream, StreamOutputStream> getOutputStream(TaskMode mode)
            throws NotImplementedException {
        return proxy.getOutputStream(mode);
    }

}
