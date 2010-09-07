package org.ogf.saga.adaptors.xmlrpc.rpc;

import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
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
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.rpc.RPCWrapper;
import org.ogf.saga.rpc.IOMode;
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.spi.rpc.RPCAdaptorBase;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class RPCAdaptor extends RPCAdaptorBase {
    
    public static String[] getSupportedSchemes() {
        return new String[] { "xmprpc", ""};
    }
    

    private final String func;

    private final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

    private final XmlRpcClient client = new XmlRpcClient();

    private boolean closed = false;

    public RPCAdaptor(RPCWrapper wrapper, SessionImpl sessionImpl, URL funcName)
            throws NotImplementedException, BadParameterException,
            NoSuccessException, DoesNotExistException, IncorrectURLException {
        super(sessionImpl, wrapper, funcName);

        URL url = URLFactory.createURL(funcName.toString());
        url.setScheme("http");
        url.setHost(getHost());
        url.setPort(getPort());
        url.setPath("/xmlrpc");

        func = getFunc();

        try {
            config.setServerURL(new java.net.URL(url.toString()));
        } catch (MalformedURLException e) {
            throw new NoSuccessException("internal error", e);
        }
        String extensions = SAGAEngine
                .getProperty("saga.adaptor.xmlrpc.enableExtensions");
        if ("".equals(extensions) || "true".equals(extensions)
                || "1".equals(extensions)) {
            // Note: this requires the apache xmlrpc server.
            config.setEnabledForExtensions(true);
        }
        // config.setEnabledForExceptions(true);
        client.setConfig(config);

        // validate method name
        boolean ok = false;
        try {
            Object[] methods = (Object[]) client.execute("system.listMethods",
                    new Object[] {});
            for (Object o : methods) {
                if (o.equals(func)) {
                    ok = true;
                }
            }
        } catch (Throwable e) {
            // system.listMethods does not seem to be supported.
            // Ignored. Cannot test func name.
            ok = true;
        }
        if (!ok) {
            throw new DoesNotExistException("Server does not support function "
                    + func);
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
        String name = "system.listMethods";
        try {
            String n = funcName.getPath();
            if (n != null) {
                // Skip the first '/'.
                name = n.substring(1);
            }
        } catch (Throwable e) {
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
        } catch (Throwable e) {
            // ignored
        }
        return hostname;
    }

    public void call(Parameter... parameters) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {

        synchronized (this) {
            if (closed) {
                throw new IncorrectStateException("Already closed", wrapper);
            }
        }

        int outIndex = -1;
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getIOMode() != IOMode.IN) {
                if (outIndex >= 0) {
                    throw new NotImplementedException(
                            "More than one INOUT/OUT parameter not supported",
                            wrapper);
                }
                outIndex = i;
            }
        }
        Object[] rpcParameters;
        if (outIndex >= 0 && parameters[outIndex].getIOMode() == IOMode.OUT) {
            rpcParameters = new Object[parameters.length - 1];
        } else {
            rpcParameters = new Object[parameters.length];
        }
        int index = 0;
        for (int i = 0; i < parameters.length; i++) {
            if (i == outIndex && parameters[i].getIOMode() == IOMode.OUT) {
                continue;
            }
            rpcParameters[index++] = parameters[i].getData();
        }

        try {
            Object retval = client.execute(func, rpcParameters);
            if (outIndex >= 0) {
                parameters[outIndex].setData(retval);
            }
        } catch (XmlRpcException e) {
            throw new NoSuccessException("RPC failed", e, wrapper);
        }
    }

    public synchronized void close(float timeoutInSeconds)
            throws NotImplementedException, NoSuccessException {
        closed = true;
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getGroup", wrapper);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getOwner", wrapper);
    }

    public void permissionsAllow(String arg0, int arg1)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsAllow", wrapper);
    }

    public boolean permissionsCheck(String arg0, int arg1)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsCheck", wrapper);
    }

    public void permissionsDeny(String arg0, int arg1)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsDeny", wrapper);
    }

}
