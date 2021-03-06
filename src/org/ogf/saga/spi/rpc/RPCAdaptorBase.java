package org.ogf.saga.spi.rpc;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public abstract class RPCAdaptorBase extends AdaptorBase<RPC> implements RPCSPI {

    protected URL funcName;

    public RPCAdaptorBase(SessionImpl sessionImpl, RPC wrapper, URL funcName)
            throws NotImplementedException, BadParameterException,
            NoSuccessException {
        super(sessionImpl, wrapper);
        this.funcName = URLFactory.createURL(MY_FACTORY, funcName.toString());
    }

    public Task<RPC, Void> call(TaskMode mode, Parameter... parameters)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPC, Void>(wrapper,
                sessionImpl, mode, "call",
                new Class[] { parameters.getClass() }, (Object[]) parameters);
    }

    public Task<RPC, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPC, Void>(wrapper,
                sessionImpl, mode, "close", new Class[] { Float.TYPE },
                timeoutInSeconds);
    }

    public Task<RPC, Void> permissionsAllow(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPC, Void>(wrapper,
                sessionImpl, mode, "permissionsAllow", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<RPC, Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPC, Boolean>(wrapper,
                sessionImpl, mode, "permissionsCheck", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<RPC, Void> permissionsDeny(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPC, Void>(wrapper,
                sessionImpl, mode, "permissionsDeny", new Class[] {
                        String.class, Integer.TYPE }, id, permissions);
    }

    public Task<RPC, String> getGroup(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPC, String>(wrapper,
                sessionImpl, mode, "getGroup", new Class[] {});
    }

    public Task<RPC, String> getOwner(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPC, String>(wrapper,
                sessionImpl, mode, "getOwner", new Class[] {});
    }
}
