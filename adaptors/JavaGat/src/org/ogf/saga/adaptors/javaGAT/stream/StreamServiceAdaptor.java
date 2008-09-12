package org.ogf.saga.adaptors.javaGAT.stream;

import java.net.SocketTimeoutException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
import org.gridlab.gat.advert.AdvertService;
import org.gridlab.gat.advert.MetaData;
import org.gridlab.gat.io.Endpoint;
import org.gridlab.gat.io.Pipe;
import org.ogf.saga.adaptors.javaGAT.namespace.NSEntryAdaptor;
import org.ogf.saga.adaptors.javaGAT.util.Initialize;
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
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class StreamServiceAdaptor extends StreamServiceAdaptorBase {

    static {
        Initialize.initialize();
    }

    private static float MINIMAL_TIMEOUT = 0.001f;

    private static Logger logger = Logger.getLogger(StreamServiceAdaptor.class);
    
    private boolean active = false;
    private GATContext gatContext;
    
    public StreamServiceAdaptor(StreamServiceWrapper wrapper, Session session, URL url)
            throws NotImplementedException, BadParameterException {
        super(wrapper, session, url);
        gatContext = StreamAdaptor.initializeGatContext(session);
        active = true;
    }

    public Object clone() throws CloneNotSupportedException {
        StreamServiceAdaptor clone = (StreamServiceAdaptor) super.clone();
        clone.gatContext = (GATContext) this.gatContext.clone();
        return clone;
    }

    // if a stream_service was never opened (i.e.
    // serve() was never called), an 'IncorrectState'
    // exception is thrown.

    // any subsequent method call on the object
    // MUST raise an 'IncorrectState' exception
    // (apart from DESTRUCTOR and close()).

    // close() can be called multiple times, with no
    // side effects.

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        active = false;
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
            throw new IncorrectStateException("The service is not active", wrapper);
        if (timeoutInSeconds == 0.0)
            timeoutInSeconds = MINIMAL_TIMEOUT;

        if (timeoutInSeconds < 0.0)
            invocationTimeout = 0;
        else if (timeoutInSeconds > 0.0)
            invocationTimeout = (int) (timeoutInSeconds * 1000);
        
        AdvertService advertService = null;
        URI db = null;
        try {
            db = NSEntryAdaptor.cvtToGatURI(URLFactory.createURL(
                    StreamAdaptor.getAdvertName(gatContext)));
            advertService = GAT.createAdvertService(gatContext);
            Endpoint serverSide = GAT.createEndpoint(gatContext);
            advertService.add(serverSide, new MetaData(), url.getString());
            advertService.exportDataBase(db);
            Pipe pipe = serverSide.listen(invocationTimeout);
            clientConnectMetric.internalFire();
            return new ConnectedStreamImpl(session, url, pipe);
        } catch (GATObjectCreationException e) {
            throw new NoSuccessException("Errors in GAT", e);
        } catch (GATInvocationException e) {
            // detecting timeout - not working because of bug (check when it
            // will be resolved)
            // this is really a hack because
            // it depends on one of the GAT adaptor implementation
            // in other cases it just can't detect timeout

            logger.debug("Detecting timeout..." + e);
            for (Throwable t : e.getExceptions()) {
                if (t instanceof GATInvocationException) {
                    GATInvocationException gatNestedEx = (GATInvocationException) t;
                    for (Throwable t2 : gatNestedEx.getExceptions()) {
                        logger.debug("Another exception: " + t2.getClass());
                        if (t2 instanceof SocketTimeoutException) {
                            logger.debug("Timeout exception");
                            throw new TimeoutException(e, wrapper);
                        }
                    }
                    break;
                }
            }
            // we have no other clues
            throw new NoSuccessException(e, wrapper);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Incorrect URL for javagat advert service?", e, wrapper);
        } catch (URISyntaxException e) {
            throw new NoSuccessException("Incorrect URL for javagat advert service?", e, wrapper);
        } finally {
            try {
                advertService.delete(url.getString());
                advertService.exportDataBase(db);
            } catch (Throwable e) {
                // ignored
            }
        }
    }

    public String getGroup() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getGroup", wrapper);
    }

    public String getOwner() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getOwner", wrapper);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsAllow", wrapper);
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsCheck", wrapper);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsDeny", wrapper);
    }

}
