package org.ogf.saga.adaptors.socket.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.impl.url.URLUtil;
import org.ogf.saga.proxies.stream.StreamServerWrapper;
import org.ogf.saga.spi.stream.StreamServerAdaptorBase;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.url.URL;

public class StreamServerAdaptor extends StreamServerAdaptorBase {
        
    public static String[] getSupportedSchemes() {
        return new String[] { "tcp", ""};
    }
    
    private static Logger logger = LoggerFactory.getLogger(StreamServerAdaptor.class);

    private boolean active = false;
    private static float MINIMAL_TIMEOUT = 0.001f;

    private ServerSocket server;

    public StreamServerAdaptor(StreamServerWrapper wrapper,
            SessionImpl sessionImpl, URL url) throws NotImplementedException,
            BadParameterException, NoSuccessException, IncorrectURLException {

        super(wrapper, sessionImpl, url);
        active = true;

        // check URL

        if (URLUtil.refersToLocalHost(url.getHost())) {
            try {
                this.server = new ServerSocket();
                server.setReuseAddress(true);
                server.bind(new InetSocketAddress(url.getHost(), url.getPort()));
                logger.debug("ServerSocket bound");
            } catch (IOException e) {
                // Could not create server socket, or could not bind. But maybe this
                // object is only used for connect().
                if (logger.isDebugEnabled()) {
                    logger.debug("Got exception", e);
                }
                this.server = null;
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        StreamServerAdaptor clone = (StreamServerAdaptor) super.clone();
        return clone;
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        active = false;
        if (server != null) {
            try {
                server.close();
                logger.debug("Server closed");
            } catch (IOException e) {
                // ignored
            }
        }
    }

    public URL getUrl() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return url;
    }

    public Stream serve(float timeoutInSeconds) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        int invocationTimeout = -1;

        if (!active)
            throw new IncorrectStateException("The service is not active",
                    wrapper);
        if (server == null) {
            if (! URLUtil.refersToLocalHost(url.getHost())) {
                throw new NoSuccessException("serve() called on non-local URL " + url);
            }
            throw new NoSuccessException("could not create server on " + url);
        }
        if (timeoutInSeconds == 0.0)
            timeoutInSeconds = MINIMAL_TIMEOUT;

        if (timeoutInSeconds < 0.0)
            invocationTimeout = 0;
        else if (timeoutInSeconds > 0.0)
            invocationTimeout = (int) (timeoutInSeconds * 1000);

        try {
            server.setSoTimeout(invocationTimeout);
            Socket clientConnection = server.accept();
            clientConnectMetric.internalFire();
            // TODO: blocking or non-blocking ???
            return new ConnectedStreamImpl(sessionImpl, url, clientConnection);
        } catch (SocketException e) {
            throw new NoSuccessException(e, wrapper);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e, wrapper);
        } catch (IOException e) {
            throw new NoSuccessException(e, wrapper);
        }
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
