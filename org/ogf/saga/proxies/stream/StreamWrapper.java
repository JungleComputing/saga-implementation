package org.ogf.saga.proxies.stream;

import java.io.IOException;

import org.ogf.saga.ObjectType;
import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.stream.StreamSpiInterface;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class StreamWrapper extends SagaObjectBase implements Stream {
    
    private StreamSpiInterface proxy;

    public StreamWrapper(Session session, URL name)
            throws NotImplemented, IncorrectURL, BadParameter,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        super(session);
        Object[] parameters = { this, session, name };
        try {
            proxy = (StreamSpiInterface) SAGAEngine.createAdaptorProxy(
                    StreamSpiInterface.class,
                    new Class[] { StreamWrapper.class, Session.class, URL.class },
                    parameters);
        } catch(org.ogf.saga.error.Exception e) {
            if (e instanceof NotImplemented) {
                throw (NotImplemented) e;
            }
            if (e instanceof IncorrectURL) {
                throw (IncorrectURL) e;
            }
            if (e instanceof AuthenticationFailed) {
                throw (AuthenticationFailed) e;
            }
            if (e instanceof AuthorizationFailed) {
                throw (AuthorizationFailed) e;
            }
            if (e instanceof PermissionDenied) {
                throw (PermissionDenied) e;
            }
            if (e instanceof BadParameter) {
                throw (BadParameter) e;
            }
            if (e instanceof Timeout) {
                throw (Timeout) e;
            }
            if (e instanceof NoSuccess) {
                throw (NoSuccess) e;
            }
            throw new NoSuccess("Constructor failed", e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        StreamWrapper clone = (StreamWrapper) super.clone();
        proxy = (StreamSpiInterface) proxy.clone();
        return clone;
    }
    
    public int addCallback(String name, Callback cb) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess, IncorrectState {
        return proxy.addCallback(name, cb);
    }

    public Task<Integer> addCallback(TaskMode mode, String name, Callback cb) throws NotImplemented {
        return proxy.addCallback(mode, name, cb);
    }

    public void close(float timeoutInSeconds) throws NotImplemented, IncorrectState, NoSuccess {
        proxy.close(timeoutInSeconds);
    }

    public Task close(TaskMode mode, float timeoutInSeconds) throws NotImplemented {
        return proxy.close(mode, timeoutInSeconds);
    }

    public void connect() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        proxy.connect();
    }

    public Task connect(TaskMode mode) throws NotImplemented {
        return proxy.connect(mode);
    }

    public String[] findAttributes(String... patterns) throws NotImplemented, BadParameter, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.findAttributes(patterns);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns) throws NotImplemented {
        return proxy.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getAttribute(key);
    }

    public Task<String> getAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.getAttribute(mode, key);
    }

    public Context getContext() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return proxy.getContext();
    }

    public Task<Context> getContext(TaskMode mode) throws NotImplemented {
        return proxy.getContext(mode);
    }

    public String getGroup() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getGroup();
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return proxy.getGroup(mode);
    }

    public Metric getMetric(String name) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.getMetric(name);
    }

    public Task<Metric> getMetric(TaskMode mode, String name) throws NotImplemented {
        return proxy.getMetric(mode, name);
    }

    public String getOwner() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getOwner();
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return proxy.getOwner(mode);
    }

    public URL getUrl() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return proxy.getUrl();
    }

    public Task<URL> getUrl(TaskMode mode) throws NotImplemented {
        return proxy.getUrl(mode);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return proxy.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isReadOnlyAttribute(key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isRemovableAttribute(key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isVectorAttribute(key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return proxy.isWritableAttribute(key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.listAttributes();
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplemented {
        return proxy.listAttributes(mode);
    }

    public String[] listMetrics() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.listMetrics();
    }

    public Task<String[]> listMetrics(TaskMode mode) throws NotImplemented {
        return proxy.listMetrics(mode);
    }

    public void permissionsAllow(String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(id, permissions);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions) throws NotImplemented {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id, int permissions) throws NotImplemented {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions) throws NotImplemented {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public int read(Buffer buffer, int len) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.read(buffer, len);
    }

    public Task<Integer> read(TaskMode mode, Buffer buffer, int len) throws NotImplemented {
        return proxy.read(mode, buffer, len);
    }

    public void removeAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        proxy.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key) throws NotImplemented {
        return proxy.removeAttribute(mode, key);
    }

    public void removeCallback(String name, int cookie) throws NotImplemented, DoesNotExist, BadParameter, Timeout, NoSuccess, AuthenticationFailed, AuthorizationFailed, PermissionDenied {
        proxy.removeCallback(name, cookie);
    }

    public Task removeCallback(TaskMode mode, String name, int cookie) throws NotImplemented {
        return proxy.removeCallback(mode, name, cookie);
    }

    public void setAttribute(String key, String value) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        proxy.setAttribute(key, value);
    }

    public Task setAttribute(TaskMode mode, String key, String value) throws NotImplemented {
        return proxy.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        proxy.setVectorAttribute(key, values);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values) throws NotImplemented {
        return proxy.setVectorAttribute(mode, key, values);
    }

    public int waitStream(int what, float timeoutInSeconds) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, NoSuccess {
        return proxy.waitStream(what, timeoutInSeconds);
    }

    public Task<Integer> waitStream(TaskMode mode, int what, float timeoutInSeconds) throws NotImplemented {
        return proxy.waitStream(mode, what, timeoutInSeconds);
    }

    public int write(Buffer buffer, int len) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.write(buffer, len);
    }

    public Task<Integer> write(TaskMode mode, Buffer buffer, int len) throws NotImplemented {
        return proxy.write(mode, buffer, len);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STREAM;
    }

    public void close() throws NotImplemented, IncorrectState, NoSuccess {
        close(0.0F);
    }

    public Task close(TaskMode mode) throws NotImplemented {
        return close(mode, 0.0F);
    }

    public int read(Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return read(buffer, -1);
    }

    public Task<Integer> read(TaskMode mode, Buffer buffer) throws NotImplemented {
        return read(mode, buffer, -1);
    }

    public int waitStream(int what) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, NoSuccess {
        return waitStream(what, -1.0F);
    }

    public Task<Integer> waitStream(TaskMode mode, int what) throws NotImplemented {
        return waitStream(mode, what, -1.0F);
    }

    public int write(Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return write(buffer, -1);
    }

    public Task<Integer> write(TaskMode mode, Buffer buffer) throws NotImplemented {
        return write(mode, buffer, -1);
    }

}
