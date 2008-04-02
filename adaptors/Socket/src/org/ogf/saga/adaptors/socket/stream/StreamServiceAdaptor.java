package org.ogf.saga.adaptors.socket.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.proxies.stream.StreamServiceWrapper;
import org.ogf.saga.spi.stream.StreamServiceAdaptorBase;
import org.ogf.saga.stream.Stream;

public class StreamServiceAdaptor extends StreamServiceAdaptorBase {

    private boolean active = false;
    private static float MINIMAL_TIMEOUT = 0.001f;

    private ServerSocket server;

    public StreamServiceAdaptor(StreamServiceWrapper wrapper, Session session, URL url)
            throws NotImplementedException, BadParameterException, NoSuccessException {

        super(wrapper, session, url);
        active = true;

        // check URL

        String scheme = url.getScheme().toLowerCase();
        if (! scheme.equals("any") && !scheme.equals("tcp")) {
            throw new NotImplementedException(
                    "Only tcp scheme is supported in socket implementation");
        }

        try {
            this.server = new ServerSocket();
            server.bind(new InetSocketAddress(url.getHost(), url.getPort()));
        } catch (IOException e) {
            throw new NoSuccessException("Caught an exception when doing bind...", e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        StreamServiceAdaptor clone = (StreamServiceAdaptor) super.clone();
        return clone;
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        active = false;
        try {
            server.close();
        } catch(IOException e) {
            // ignored
        }
    }

    public URL getUrl() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return url;
    }

    public Stream serve(float timeoutInSeconds) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        int invocationTimeout = -1;

        if (!active)
            throw new IncorrectStateException("The service is not active");
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
            return new ConnectedStreamImpl(session, url, clientConnection);
        } catch (SocketException e) {
            throw new NoSuccessException(e);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        } catch (IOException e) {
            throw new NoSuccessException(e);
        }

    }

    public String getGroup() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException();
    }

    public String getOwner() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException();
    }

    public void permissionsAllow(String arg0, int arg1) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException();
    }

    public boolean permissionsCheck(String arg0, int arg1)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException();
    }

    public void permissionsDeny(String arg0, int arg1) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException();
    }

}
