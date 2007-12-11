package org.ogf.saga.proxies.rpc;

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
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.rpc.RPCSpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class RPCWrapper extends SagaObjectBase implements RPC {
    
    private RPCSpiInterface proxy;

    public RPCWrapper(Session session, URL funcName)
            throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, DoesNotExist, Timeout, NoSuccess
    {
        super(session);
        Object[] parameters = { this, session, funcName };
        try {
            proxy = (RPCSpiInterface) SAGAEngine.createAdaptorProxy(
                    RPCSpiInterface.class,
                    new Class[] { RPCWrapper.class, 
                        org.ogf.saga.impl.session.Session.class, URL.class },
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
            if (e instanceof DoesNotExist) {
                throw (DoesNotExist) e;
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

    public void call(Parameter... parameters) throws NotImplemented,
            IncorrectURL, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, DoesNotExist,
            Timeout, NoSuccess {
        proxy.call(parameters);
    }

    public Task call(TaskMode mode, Parameter... parameters)
            throws NotImplemented {
        return proxy.call(mode, parameters);
    }

    public void close(float timeoutInSeconds) throws NotImplemented,
            IncorrectState, NoSuccess {
        proxy.close(timeoutInSeconds);
    }

    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplemented {
        return proxy.close(mode, timeoutInSeconds);
    }

    public String getGroup() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getGroup();
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return proxy.getGroup(mode);
    }

    public String getOwner() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return proxy.getOwner();
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return proxy.getOwner(mode);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsAllow(id, permissions);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplemented {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, Timeout, NoSuccess {
        proxy.permissionsDeny(id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
            throws NotImplemented {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public ObjectType getType() {
        return ObjectType.RPC;
    }

    public void close() throws NotImplemented, IncorrectState, NoSuccess {
        close(0.0F);
    }

    public Task close(TaskMode mode) throws NotImplemented {
        return close(mode, 0.0F);
    }

}
