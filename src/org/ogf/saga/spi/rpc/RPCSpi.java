package org.ogf.saga.spi.rpc;

import org.ogf.saga.URL;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class RPCSpi extends AdaptorBase implements RPCSpiInterface {
    
    protected URL funcName;

    public RPCSpi(Session session, Object wrapper, URL funcName)
            throws NotImplementedException, BadParameterException, NoSuccessException {
        super(session, wrapper);
        this.funcName = new URL(funcName.toString());
    }

    public Task call(TaskMode mode, Parameter... parameters)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "call", new Class[] { parameters.getClass()}, (Object[]) parameters); 
    }

    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "close", new Class[] {Float.TYPE}, timeoutInSeconds);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
    throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsAllow", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session,
                mode, "permissionsCheck", new Class[] { String.class,
                Integer.TYPE }, id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
    throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsDeny", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String>(wrapper, session,
                mode, "getGroup", new Class[] {});
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<String>(wrapper, session,
                mode, "getOwner", new Class[] {});
    }
}
