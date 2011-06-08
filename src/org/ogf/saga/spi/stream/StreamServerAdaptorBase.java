package org.ogf.saga.spi.stream;

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
import org.ogf.saga.impl.monitoring.MetricImpl;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.proxies.stream.StreamServerWrapper;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamServer;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public abstract class StreamServerAdaptorBase extends
        AdaptorBase<StreamServerWrapper> implements StreamServerSPI {

    protected URL url;
    protected MetricImpl clientConnectMetric;

    public StreamServerAdaptorBase(StreamServerWrapper wrapper,
            SessionImpl sessionImpl, URL url) throws NotImplementedException,
            BadParameterException {
        super(sessionImpl, wrapper);
        this.url = url;
        clientConnectMetric = new MetricImpl(wrapper, sessionImpl,
                StreamServer.STREAMSERVER_CLIENTCONNECT,
                "fires if a client connects", "ReadOnly", "1", "Trigger", "1");
    }

    public Object clone() throws CloneNotSupportedException {
        StreamServerAdaptorBase clone = (StreamServerAdaptorBase) super
                .clone();
        try {
            clone.url = URLFactory.createURL(MY_FACTORY, url.toString());
        } catch (Throwable e) {
            throw new SagaRuntimeException("Should not happen", e);
        }
        clone.clientConnectMetric = (MetricImpl) clientConnectMetric.clone();
        return clone;
    }
    
    public Task<StreamServer, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Void>(
                wrapper, sessionImpl, mode, "close",
                new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task<StreamServer, URL> getUrl(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, URL>(wrapper,
                sessionImpl, mode, "getURL", new Class[] {});
    }

    public Task<StreamServer, Stream> serve(TaskMode mode,
            float timeoutInSeconds) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Stream>(
                wrapper, sessionImpl, mode, "serve",
                new Class[] { Float.TYPE }, timeoutInSeconds);
    }
    

    public Task<StreamServer, Stream> connect(TaskMode mode,
            float timeoutInSeconds) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Stream>(
                wrapper, sessionImpl, mode, "connect",
                new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task<StreamServer, Integer> addCallback(TaskMode mode, String name,
            Callback cb) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Integer>(
                wrapper, sessionImpl, mode, "addCallback", new Class[] {
                        String.class, Callback.class }, name, cb);
    }

    public Task<StreamServer, org.ogf.saga.monitoring.Metric> getMetric(
            TaskMode mode, String name) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, org.ogf.saga.monitoring.Metric>(
                wrapper, sessionImpl, mode, "getMetric",
                new Class[] { String.class }, name);
    }

    public Task<StreamServer, String[]> listMetrics(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, String[]>(
                wrapper, sessionImpl, mode, "listMetrics", new Class[] {});
    }

    public Task<StreamServer, Void> removeCallback(TaskMode mode, String name,
            int cookie) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Void>(
                wrapper, sessionImpl, mode, "removeCallback", new Class[] {
                        String.class, Integer.TYPE }, name, cookie);
    }

    public int addCallback(String name, Callback cb)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectStateException {
        MetricImpl metricImpl = getMetric(name);
        return metricImpl.addCallback(cb);
    }

    public MetricImpl getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        if (StreamServer.STREAMSERVER_CLIENTCONNECT.equals(name)) {
            return clientConnectMetric;
        }
        throw new DoesNotExistException("metric " + name + " does not exist");
    }

    public String[] listMetrics() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return new String[] { StreamServer.STREAMSERVER_CLIENTCONNECT };
    }

    public void removeCallback(String name, int cookie)
            throws NotImplementedException, DoesNotExistException,
            BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException {
        MetricImpl metricImpl = getMetric(name);
        metricImpl.removeCallback(cookie);
    }

    public Task<StreamServer, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, String>(
                wrapper, sessionImpl, mode, "getGroup", new Class[] {});
    }

    public Task<StreamServer, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, String>(
                wrapper, sessionImpl, mode, "getOwner", new Class[] {});
    }

    public Task<StreamServer, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Void>(
                wrapper, sessionImpl, mode, "permissionsAllow", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<StreamServer, Boolean> permissionsCheck(TaskMode mode,
            String id, int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Boolean>(
                wrapper, sessionImpl, mode, "permissionsCheck", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<StreamServer, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<StreamServer, Void>(
                wrapper, sessionImpl, mode, "permissionsDeny", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

}
