package org.ogf.saga.spi.stream;

import org.ogf.saga.URL;
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
import org.ogf.saga.proxies.stream.StreamServiceWrapper;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamService;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class StreamServiceAdaptorBase extends AdaptorBase<StreamServiceWrapper> implements StreamServiceSPI {

    protected URL url;
    protected Metric clientConnectMetric;
    
    public StreamServiceAdaptorBase(StreamServiceWrapper wrapper, Session session, URL url)
            throws NotImplementedException, BadParameterException {
        super(session, wrapper);
        this.url = url;
        clientConnectMetric = new Metric(wrapper, session,
                StreamService.STREAMSERVER_CLIENTCONNECT,
                "fires if a client connects", "ReadOnly", "1", "Trigger", "1");
    }
    
    public Object clone() throws CloneNotSupportedException {
        StreamServiceAdaptorBase clone = (StreamServiceAdaptorBase) super.clone();
        try {
            clone.url = new URL(url.toString());
        } catch (Throwable e) {
            throw new SagaRuntimeException("Should not happen", e);
        }
        clone.clientConnectMetric = (Metric) clientConnectMetric.clone();
        return clone;
    }
    
    public Task<StreamService, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, Void>(wrapper, session, mode,
                "close", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task<StreamService, URL> getUrl(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, URL>(wrapper, session, mode,
                "getURL", new Class[] { });
    }

    public Task<StreamService, Stream> serve(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, Stream>(wrapper, session, mode,
                "serve", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task<StreamService, Integer> addCallback(TaskMode mode, String name, Callback cb)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, Integer>(wrapper, session, mode,
                "addCallback", new Class[] { String.class, Callback.class },
                name, cb);
    }

    public Task<StreamService, org.ogf.saga.monitoring.Metric> getMetric(TaskMode mode, String name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, org.ogf.saga.monitoring.Metric>(wrapper, session, mode,
                "getMetric", new Class[] { String.class }, name);
    }

    public Task<StreamService, String[]> listMetrics(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, String[]>(wrapper, session, mode,
                "listMetrics", new Class[] { });
    }

    public Task<StreamService, Void> removeCallback(TaskMode mode, String name, int cookie)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, Void>(wrapper, session, mode,
                "removeCallback", new Class[] { String.class, Integer.TYPE },
                name, cookie);
    }

    public int addCallback(String name, Callback cb) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException, IncorrectStateException {
        Metric metric = getMetric(name);
        return metric.addCallback(cb);
    }

    public Metric getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (StreamService.STREAMSERVER_CLIENTCONNECT.equals(name)) {
            return clientConnectMetric;
        }
        throw new DoesNotExistException("metric " + name + " does not exist");
    }

    public String[] listMetrics() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return new String[] { StreamService.STREAMSERVER_CLIENTCONNECT };
    }

    public void removeCallback(String name, int cookie) throws NotImplementedException,
            DoesNotExistException, BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException {
        Metric metric = getMetric(name);
        metric.removeCallback(cookie);
    }

    public Task<StreamService, String> getGroup(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, String>(wrapper, session, mode,
                "getGroup", new Class[] { });
    }

    public Task<StreamService, String> getOwner(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, String>(wrapper, session, mode,
                "getOwner", new Class[] { });
    }

    public Task<StreamService, Void> permissionsAllow(TaskMode mode, String id, int permissions)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, Void>(wrapper, session, mode,
                "permissionsAllow", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public Task<StreamService, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, Boolean>(wrapper, session, mode,
                "permissionsCheck", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

    public Task<StreamService, Void> permissionsDeny(TaskMode mode, String id, int permissions)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<StreamService, Void>(wrapper, session, mode,
                "permissionsDeny", new Class[] {String.class, Integer.TYPE},
                id, permissions);
    }

}
