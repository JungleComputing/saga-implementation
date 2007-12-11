package org.ogf.saga.proxies.rpc;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.rpc.IOMode;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.rpc.RPCFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class RPCWrapperFactory extends RPCFactory {

    protected RPC doCreateRPC(Session session, URL funcname)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter, DoesNotExist,
            Timeout, NoSuccess {
        return new RPCWrapper(session, funcname);
    }

    protected Task<RPC> doCreateRPC(TaskMode mode, Session session, URL funcname)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<RPC>(this, session, mode,
                "doCreateRPC",
                new Class[] { Session.class, URL.class},
                session, funcname);
    }

    @Override
    protected org.ogf.saga.rpc.Parameter doCreateParameter(byte[] data, IOMode mode)
            throws BadParameter, NoSuccess, NotImplemented {
        return new Parameter(data, mode);
    }

    @Override
    protected org.ogf.saga.rpc.Parameter doCreateParameter(IOMode mode) throws BadParameter,
            NoSuccess, NotImplemented {
        return new Parameter(mode);
    }

    @Override
    protected org.ogf.saga.rpc.Parameter doCreateParameter(int sz, IOMode mode)
            throws BadParameter, NoSuccess, NotImplemented {
        return new Parameter(sz, mode);
    }
}
