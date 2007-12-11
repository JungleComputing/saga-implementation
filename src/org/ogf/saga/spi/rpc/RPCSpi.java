package org.ogf.saga.spi.rpc;

import org.ogf.saga.URL;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class RPCSpi extends AdaptorBase implements RPCSpiInterface {
    
    protected URL funcName;

    public RPCSpi(Session session, Object wrapper, URL funcName)
            throws NotImplemented, BadParameter, NoSuccess {
        super(session, wrapper);
        this.funcName = new URL(funcName.toString());
    }

    public Task call(TaskMode mode, Parameter... parameters)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "call", new Class[] { parameters.getClass()}, (Object[]) parameters); 
    }

    public Task close(TaskMode mode, float timeoutInSeconds)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "close", new Class[] {Float.TYPE}, timeoutInSeconds);
    }

    public Task permissionsAllow(TaskMode mode, String id, int permissions)
    throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsAllow", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<Boolean> permissionsCheck(TaskMode mode, String id,
            int permissions) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session,
                mode, "permissionsCheck", new Class[] { String.class,
                Integer.TYPE }, id, permissions);
    }

    public Task permissionsDeny(TaskMode mode, String id, int permissions)
    throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsDeny", new Class[] { String.class, Integer.TYPE },
                id, permissions);
    }

    public Task<String> getGroup(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(wrapper, session,
                mode, "getGroup", new Class[] {});
    }

    public Task<String> getOwner(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<String>(wrapper, session,
                mode, "getOwner", new Class[] {});
    }
}
