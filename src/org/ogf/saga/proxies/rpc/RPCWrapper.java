package org.ogf.saga.proxies.rpc;

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
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.rpc.RPCSPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class RPCWrapper extends SagaObjectBase implements RPC {

    private RPCSPI proxy;

    public RPCWrapper(Session session, URL funcName)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        super(session);
        Object[] parameters = { this, session, funcName };
        try {
            proxy = (RPCSPI) SAGAEngine.createAdaptorProxy(RPCSPI.class,
                    new Class[] { RPCWrapper.class,
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
            if (e instanceof DoesNotExistException) {
                throw (DoesNotExistException) e;
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

    public void call(Parameter... parameters) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.call(parameters);
    }

    public Task<RPC, Void> call(TaskMode mode, Parameter... parameters)
            throws NotImplementedException {
        return proxy.call(mode, parameters);
    }
    
    protected void finalize() {
        try {
            close(0.0f);
        } catch (Throwable e) {
            // ignored
        }
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        proxy.close(timeoutInSeconds);
    }

    public Task<RPC, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return proxy.close(mode, timeoutInSeconds);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getGroup();
    }

    public Task<RPC, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return proxy.getGroup(mode);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.getOwner();
    }

    public Task<RPC, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return proxy.getOwner(mode);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsAllow(id, permissions);
    }

    public Task<RPC, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsAllow(mode, id, permissions);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return proxy.permissionsCheck(id, permissions);
    }

    public Task<RPC, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsCheck(mode, id, permissions);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        proxy.permissionsDeny(id, permissions);
    }

    public Task<RPC, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return proxy.permissionsDeny(mode, id, permissions);
    }

    public void close() throws NotImplementedException,
            NoSuccessException {
        close(0.0F);
    }

    public Task<RPC, Void> close(TaskMode mode) throws NotImplementedException {
        return close(mode, 0.0F);
    }

}
