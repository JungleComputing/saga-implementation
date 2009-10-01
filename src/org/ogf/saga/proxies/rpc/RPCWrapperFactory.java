package org.ogf.saga.proxies.rpc;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.rpc.IOMode;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.rpc.RPCFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class RPCWrapperFactory extends RPCFactory {

    public RPC doCreateRPC(Session session, URL funcname)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return new RPCWrapper(session, funcname);
    }

    protected Task<RPCFactory, RPC> doCreateRPC(TaskMode mode, Session session,
            URL funcname) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<RPCFactory, RPC>(this,
                session, mode, "doCreateRPC", new Class[] { Session.class,
                        URL.class }, session, funcname);
    }

    protected org.ogf.saga.rpc.Parameter doCreateParameter(Object data,
            IOMode mode) throws BadParameterException, NoSuccessException,
            NotImplementedException {
        return new ParameterImpl(data, mode);
    }

    protected org.ogf.saga.rpc.Parameter doCreateParameter(IOMode mode)
            throws BadParameterException, NoSuccessException,
            NotImplementedException {
        return new ParameterImpl(mode);
    }
}
