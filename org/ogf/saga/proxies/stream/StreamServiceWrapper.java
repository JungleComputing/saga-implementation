package org.ogf.saga.proxies.stream;

import org.ogf.saga.URL;
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
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.stream.StreamServiceSPI;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamService;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class StreamServiceWrapper extends SagaObjectBase implements
        StreamService {

    private StreamServiceSPI proxy;

    public StreamServiceWrapper(Session session, URL name)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        super(session);
        Object[] parameters = { this, session, name };
        try {
            proxy = (StreamServiceSPI) SAGAEngine
                    .createAdaptorProxy(StreamServiceSPI.class,
                            new Class[] { StreamServiceWrapper.class,
                                    org.ogf.saga.impl.session.Session.class,
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
        StreamServiceWrapper clone = (StreamServiceWrapper) super.clone();
        clone.proxy = (StreamServiceSPI) SAGAEngine.createAdaptorCopy(
                StreamServiceSPI.class, proxy, clone);
        return clone;
    }

    public int addCallback(String name, Callback cb)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectStateException {
        return proxy.addCallback(name, cb);
    }

    public Task<StreamService, Integer> addCallback(TaskMode mode, String name,
            Callback cb) throws NotImplementedException {
        return proxy.addCallback(mode, name, cb);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        proxy.close(timeoutInSeconds);
    }

    public Task<StreamService, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return proxy.close(mode, timeoutInSeconds);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getGroup();
    }

    public Task<StreamService, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return proxy.getGroup(mode);
    }

    public Metric getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return proxy.getMetric(name);
    }

    public Task<StreamService, Metric> getMetric(TaskMode mode, String name)
            throws NotImplementedException {
        return proxy.getMetric(mode, name);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getOwner();
    }

    public Task<StreamService, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return proxy.getOwner(mode);
    }

    public URL getUrl() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.getUrl();
    }

    public Task<StreamService, URL> getUrl(TaskMode mode)
            throws NotImplementedException {
        return proxy.getUrl(mode);
    }

    public String[] listMetrics() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.listMetrics();
    }

    public Task<StreamService, String[]> listMetrics(TaskMode mode)
            throws NotImplementedException {
        return proxy.listMetrics(mode);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsAllow(id, permissions);
    }

    public Task<StreamService, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<StreamService, Boolean> permissionsCheck(TaskMode mode,
            String id, int permissions) throws NotImplementedException {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsDeny(id, permissions);
    }

    public Task<StreamService, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public void removeCallback(String name, int cookie)
            throws NotImplementedException, DoesNotExistException,
            BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException {
        proxy.removeCallback(name, cookie);
    }

    public Task<StreamService, Void> removeCallback(TaskMode mode, String name,
            int cookie) throws NotImplementedException {
        return proxy.removeCallback(mode, name, cookie);
    }

    public Stream serve(float timeoutInSeconds) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.serve(timeoutInSeconds);
    }

    public Task<StreamService, Stream> serve(TaskMode mode,
            float timeoutInSeconds) throws NotImplementedException {
        return proxy.serve(mode, timeoutInSeconds);
    }

    public void close() throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        close(0.0F);
    }

    public Task<StreamService, Void> close(TaskMode mode)
            throws NotImplementedException {
        return close(mode, 0.0F);
    }

    public Stream serve() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return serve(-1.0F);
    }

    public Task<StreamService, Stream> serve(TaskMode mode)
            throws NotImplementedException {
        return serve(mode, -1.0F);
    }

}
