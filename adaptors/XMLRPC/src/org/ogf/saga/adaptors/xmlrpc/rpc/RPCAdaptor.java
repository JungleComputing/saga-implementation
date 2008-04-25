package org.ogf.saga.adaptors.xmlrpc.rpc;

import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ogf.saga.URL;
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
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.rpc.IOMode;
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.spi.rpc.RPCAdaptorBase;

public class RPCAdaptor extends RPCAdaptorBase {
 
    private final String func;
    
    private final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    
    private final XmlRpcClient client = new XmlRpcClient();
    
    private boolean closed = false;
    
    public RPCAdaptor(Session session, RPC wrapper, URL funcName)
            throws NotImplementedException, BadParameterException,
            NoSuccessException, DoesNotExistException {
        super(session, wrapper, funcName);
        String scheme = funcName.getScheme();
        if ("any".equals(scheme)) {
            scheme = "xmlrpc";
        } else if ("xmlrpc".equals(scheme)) {
            // OK
        } else {
            throw new NotImplementedException("Unrecognized scheme: " + scheme);
        }
        
        URL url = new URL(funcName.toString());
        url.setScheme("http");
        url.setHost(getHost());
        url.setPort(getPort());
        url.setPath("xmlrpc");
        
        func = getFunc();
        
        try {
            config.setServerURL(new java.net.URL(url.toString()));
        } catch (MalformedURLException e) {
            throw new NoSuccessException("internal error", e);
        }
        // config.setEnabledForExceptions(true);
        client.setConfig(config);
        
        // validate method name
        try {
            boolean ok = false;
            Object[] methods = (Object[]) client.execute("system.methodHelp", new Object[]{});
            for (Object o : methods) {
                if (o.equals(func)) {
                    ok = true;
                }
            }
            if (! ok) {
                throw new DoesNotExistException("Server not support function " + func);
            }
        } catch (Throwable e) {
            // ignored. Cannot test func name.
        }
    }
    
    private int getPort() {
        int port = 8080;
        try {
            int prt = funcName.getPort();
            if (prt >= 0) {
                port = prt;
            }
        } catch (Throwable e) {
            // ignored
        }
        return port;
    }
    
    private String getFunc() {
        String name = "system.methodHelp";
        try {
            String n = funcName.getPath();
            if (n != null) {
                name = n;
            }
        } catch(Throwable e) {
            // ignored
        }
        return name;
    }
    
    private String getHost() {
        String hostname = "localhost";
        try {
            String h = funcName.getHost();
            if (h != null) {
                hostname = h;
            }
        } catch(Throwable e) {
            // ignored
        }
        return hostname;
    }

    public void call(Parameter... parameters) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {

        synchronized(this) {
            if (closed) {
                throw new IncorrectStateException("Already closed");
            }
        }
        
        int outIndex = -1;
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getIOMode() != IOMode.IN) {
                if (outIndex >= 0) {
                    throw new NotImplementedException(
                            "More than one INOUT/OUT parameter not supported");
                }
               outIndex = i;
            }
        }
        Object[] rpcParameters;
        if (outIndex >= 0 && parameters[outIndex].getIOMode() == IOMode.OUT) {
            rpcParameters = new Object[parameters.length-1];
        } else {
            rpcParameters = new Object[parameters.length];
        }
        int index = 0;
        for (int i = 0; i < parameters.length; i++) {
            if (i == outIndex && parameters[i].getIOMode() == IOMode.OUT) {
                continue;
            } else {
                rpcParameters[index++] = parameters[i].getData();
            }
        }
        
        try {
            Object retval = client.execute(func, rpcParameters);
            if (outIndex >= 0) {
                parameters[outIndex].setData(retval);
            }
        } catch (XmlRpcException e) {
            throw new NoSuccessException("RPC failed", e);
        }
    }

    public synchronized void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        closed = true;
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getGroup");
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getOwner");
    }

    public void permissionsAllow(String arg0, int arg1)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsAllow");
    }

    public boolean permissionsCheck(String arg0, int arg1)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsCheck");
    }

    public void permissionsDeny(String arg0, int arg1)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsDeny");
    }

}
