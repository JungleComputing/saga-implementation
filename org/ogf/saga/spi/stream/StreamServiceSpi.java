package org.ogf.saga.spi.stream;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.proxies.stream.StreamServiceWrapper;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamService;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class StreamServiceSpi extends AdaptorBase implements StreamServiceSpiInterface {

    protected Session session;
    protected URL url;
    protected Metric clientConnectMetric;
    
    public StreamServiceSpi(StreamServiceWrapper wrapper, Session session, URL url)
            throws NotImplemented, BadParameter {
        super(wrapper);
        this.session = session;
        this.url = url;
        clientConnectMetric = new Metric(wrapper, session,
                StreamService.STREAMSERVER_CLIENTCONNECT,
                "fires if a client connects", "ReadOnly", "1", "Trigger", "1");
    }
    
    public Object clone() throws CloneNotSupportedException {
        StreamServiceSpi clone = (StreamServiceSpi) super.clone();
        try {
            clone.url = new URL(url.toString());
        } catch (Throwable e) {
            throw new SagaError("Should not happen", e);
        }
        clone.clientConnectMetric = (Metric) clientConnectMetric.clone();
        return clone;
    }
    
    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "close", new Class[] { Float.TYPE }, timeoutInSeconds);
    }

    public Task<URL> getUrl(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(wrapper, session, mode,
                "getURL", new Class[] { });
    }

    public Task<Stream> serve(TaskMode mode, float timeoutInSeconds)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Stream>(wrapper, session, mode,
                "serve", new Class[] { Float.TYPE }, timeoutInSeconds);
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
        Metric metric = getMetric(name);
        return metric.addCallback(cb);
    }

    public Metric getMetric(String name) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        if (StreamService.STREAMSERVER_CLIENTCONNECT.equals(name)) {
            return clientConnectMetric;
        }
        throw new DoesNotExist("metric " + name + " does not exist");
    }

    public String[] listMetrics() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return new String[] { StreamService.STREAMSERVER_CLIENTCONNECT };
    }

    public void removeCallback(String name, int cookie) throws NotImplemented,
            DoesNotExist, BadParameter, Timeout, NoSuccess,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied {
        Metric metric = getMetric(name);
        metric.removeCallback(cookie);
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

}
