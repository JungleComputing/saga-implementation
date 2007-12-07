package org.ogf.saga.proxies.stream;

import org.ogf.saga.ObjectType;
import org.ogf.saga.URL;
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
import org.ogf.saga.spi.stream.StreamServiceSpiInterface;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamService;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class StreamServiceWrapper extends SagaObjectBase implements
        StreamService {
    
    private StreamServiceSpiInterface proxy;

    public StreamServiceWrapper(Session session, URL name) 
            throws NotImplemented, IncorrectURL, BadParameter,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {       
        super(session);
        Object[] parameters = { this, session, name };
        try {
            proxy = (StreamServiceSpiInterface) SAGAEngine.createAdaptorProxy(
                    StreamServiceSpiInterface.class,
                    new Class[] { StreamServiceWrapper.class, Session.class, URL.class },
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
        StreamServiceWrapper clone = (StreamServiceWrapper) super.clone();
        clone.proxy = (StreamServiceSpiInterface) SAGAEngine.createAdaptorCopy(
                    StreamServiceSpiInterface.class, proxy);
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

    public void removeCallback(String name, int cookie) throws NotImplemented, DoesNotExist, BadParameter, Timeout, NoSuccess, AuthenticationFailed, AuthorizationFailed, PermissionDenied {
        proxy.removeCallback(name, cookie);
    }

    public Task removeCallback(TaskMode mode, String name, int cookie) throws NotImplemented {
        return proxy.removeCallback(mode, name, cookie);
    }

    public Stream serve(float timeoutInSeconds) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return proxy.serve(timeoutInSeconds);
    }

    public Task<Stream> serve(TaskMode mode, float timeoutInSeconds) throws NotImplemented {
        return proxy.serve(mode, timeoutInSeconds);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STREAMSERVICE;
    }

    public void close() throws NotImplemented, IncorrectState, NoSuccess {
        close(0.0F);
    }

    public Task close(TaskMode mode) throws NotImplemented {
        return close(mode, 0.0F);
    }

    public Stream serve() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return serve(-1.0F);
    }

    public Task<Stream> serve(TaskMode mode) throws NotImplemented {
        return serve(mode, -1.0F);
    }

}
