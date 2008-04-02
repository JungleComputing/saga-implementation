package org.ogf.saga.adaptors.socket.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.ogf.saga.URL;
import org.ogf.saga.attributes.Attributes;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.proxies.stream.StreamWrapper;
import org.ogf.saga.spi.stream.StreamAdaptorBase;
import org.ogf.saga.stream.Activity;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamInputStream;
import org.ogf.saga.stream.StreamOutputStream;
import org.ogf.saga.stream.StreamState;

public class StreamAdaptor extends StreamAdaptorBase implements ErrorInterface {

    private Socket socket = null;
    private boolean wasOpen = false;
    private Thread listeningReaderThread;
    private StreamListener listeningReader;
    private StreamExceptionalSituation streamListenerException = null;

    private static float MINIMAL_TIMEOUT = 0.001f;
    private static int NUM_WAIT_TRIES = 10;

    private static Logger logger = Logger.getLogger(StreamAdaptor.class);

    public StreamAdaptor(StreamWrapper wrapper, Session session, URL url)
            throws NotImplementedException, BadParameterException {
        super(wrapper, session, url);

        // check URL

        String scheme = url.getScheme().toLowerCase();
        if (! scheme.equals("any") && !scheme.equals("tcp"))
            throw new BadParameterException(
                    "Only tcp scheme is supported in socket implementation");
    }

    public Object clone() throws CloneNotSupportedException {
        // TODO
        StreamAdaptor clone = (StreamAdaptor) super.clone();
        clone.streamListenerException = null;
        return clone;
    }


    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        if (StreamStateUtils.equalsStreamState(streamState, StreamState.NEW)) {
            throw new IncorrectStateException("close() called on new stream");
        }
        try {
            if (listeningReader != null) {
                listeningReaderThread.interrupt();
                socket.close();
            }
            if (! StreamStateUtils.isFinalState(streamState)) {
                StreamStateUtils.setStreamState(streamState, StreamState.CLOSED);
                onStateChange(StreamState.CLOSED);
            }
        } catch (IOException e) {
            throw new NoSuccessException("close", e);
        }
    }
    
    protected void finalize() {
        try {
            close(0.0f);
        } catch(Throwable e) {
            // ignored
        }
    }

    public void connect() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        // on any failure we change state to Error

        try {
            StreamStateUtils.checkStreamState(streamState, StreamState.NEW);
            logger.debug("Successful check for OPEN state [CONNECT]");
        } catch (IncorrectStateException e) {
            logger.debug("Unsuccessful check for OPEN state [CONNECT]");
            if (! StreamStateUtils.isFinalState(streamState)) {
                StreamStateUtils.setStreamState(streamState, StreamState.ERROR);
                onStateChange(StreamState.ERROR);
            }
            throw e;
        } catch (NoSuccessException e) {
            logger.debug("Other error while verifying OPEN state [CONNECT]");
            StreamStateUtils.setStreamState(streamState, StreamState.ERROR);
            onStateChange(StreamState.ERROR);
            throw e;
        }

        try {
            socket = new Socket();
            setCurrentAttributes();
            socket.connect(new InetSocketAddress(url.getHost(), url.getPort()));

            StreamStateUtils.setStreamState(streamState, StreamState.OPEN);
            onStateChange(StreamState.OPEN);
            wasOpen = true;
            this.listeningReader = new StreamListener(socket, streamRead,
                    1024, this);
            this.listeningReaderThread = new Thread(this.listeningReader,
                    "clientListener");
            this.listeningReaderThread.start();
        } catch (IOException e) {
            StreamStateUtils.setStreamState(streamState, StreamState.ERROR);
            onStateChange(StreamState.ERROR);
            throw new NoSuccessException("IO error", e);
        } catch (NoSuchElementException e) {
            StreamStateUtils.setStreamState(streamState, StreamState.ERROR);
            onStateChange(StreamState.ERROR);
            throw new NoSuccessException("Incorrect entry information", e);
        }
    }
    
    private void setSendBufferSize(String sz) throws IOException {
        if (sz == null || sz.equals("")) {
            return;
        }
        int size = Integer.parseInt(sz);
        if (size != 0) {
            socket.setSendBufferSize(size);
        }
    }
    
    private void setNoDelay(String v) throws IOException {
        if (v == null || v.equals("")) {
            return;
        }
        socket.setTcpNoDelay(v.equals(Attributes.TRUE));
    }
    
    private void setCurrentAttributes() {
        try {
            String bufSizeStr = this.getAttribute(Stream.BUFSIZE);
            setSendBufferSize(bufSizeStr);
        } catch (Throwable e) {
            // ignored
        }
        try {
            String v = this.getAttribute(Stream.NODELAY);
            setNoDelay(v);
        } catch (Throwable e) {
            // ignored
        }
    }
    
    public void setAttribute(String key, String value) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        super.setAttribute(key, value);
        if (socket == null) {
            return;
        }
        if (Stream.BUFSIZE.equals(key)) {
            try {
                setSendBufferSize(value);
            } catch(Throwable e) {
                // ignored
            }
        }
        if (Stream.NODELAY.equals(key)) {
            try {
                setNoDelay(value);
            } catch(Throwable e) {
                // ignored
            }
        }
    }

    public Context getContext() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        if (!wasOpen)
            throw new IncorrectStateException("This stream was never opened");

        return ContextFactory.createContext("Unknown");
    }

    public URL getUrl() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return url;
    }

    public int read(Buffer buffer, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException, SagaIOException {

        StreamStateUtils.checkStreamState(streamState, StreamState.OPEN);

        if (len < 0)
            throw new BadParameterException("Length should be non-negative");

        int bytesRead = 0;

        try {
            bytesRead = listeningReader.read(buffer, len);
        } catch (NoSuccessException e) {
            StreamStateUtils.setStreamState(streamState, StreamState.ERROR);
            onStateChange(StreamState.ERROR);
            throw e;
        }

        // we have successfully read some data but there are is no data left
        // and there has been an exceptional situation at the buffer in the
        // meantime
        // so that it is unable to gather more data

        synchronized (this) {

            if (streamListenerException != null && listeningReader.isEmpty()) {
                StreamStateUtils.setStreamState(streamState,
                        streamListenerException.getTargetState());
                onStateChange(streamListenerException.getTargetState());
                // it should be dropped metric detection
                // we shouldn't throw this as our invocation should return
                // successfully
                // throw new NoSuccess("connection problem",
                // streamListenerException);
            }
        }

        return bytesRead;
    }

    public int waitFor(int what, float timeoutInSeconds)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, NoSuccessException {

        int cause = 0;
        float actualTimeout = 0.0f;
        int waitTime = 0;
        boolean forever = false;
        boolean once = false;

        if (timeoutInSeconds == 0.0f)
            once = true;
        else if (timeoutInSeconds < 0.0f) {
            forever = true;
            actualTimeout = 1.0f;
        } else if (timeoutInSeconds < MINIMAL_TIMEOUT * NUM_WAIT_TRIES)
            actualTimeout = MINIMAL_TIMEOUT * NUM_WAIT_TRIES;
        else
            actualTimeout = timeoutInSeconds;

        waitTime = (int) (actualTimeout * 1000);
        waitTime /= NUM_WAIT_TRIES;

        // System.out.println("WAIT: actualTimeout = " + actualTimeout);
        // System.out.println("WAIT: time waiting = " + waitTime);

        StreamStateUtils.checkStreamState(streamState, StreamState.OPEN);

        try {
            do {
                for (int i = 0; i < NUM_WAIT_TRIES; i++) {

                    if ((what & Activity.EXCEPTION.getValue()) != 0) {
                        if (streamState.getAttribute(Metric.VALUE).equals(
                                StreamState.ERROR.toString()))
                            return Activity.EXCEPTION.getValue();
                    }

                    // can we tell that the stream can be read from ?
                    // when there is > 0 bytes we can
                    // when there is = 0 bytes, it may be readable and may not

                    if ((what & Activity.READ.getValue()) != 0) {
                        if (!listeningReader.isEmpty()) {
                            cause |= Activity.READ.getValue();
                            forever = false; // we got some information and
                            // we exit the loops
                            break;
                        }

                    }

                    if (once)
                        break;

                    Thread.sleep(waitTime);
                }
            } while (forever);
        } catch (SagaException e) {
            throw new NoSuccessException("waitFor", e);
        } catch (InterruptedException e) {
            throw new NoSuccessException("waitFor -- thread interrupted", e);
        }

        if ((what & Activity.WRITE.getValue()) != 0) {
            throw new NotImplementedException("waitFor: writeable");
        }

        return cause;
    }

    public int write(Buffer buffer, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException, SagaIOException {
        StreamStateUtils.checkStreamState(streamState, StreamState.OPEN);

        byte[] data;
        try {
            data = buffer.getData();
        } catch(DoesNotExistException e) {
            throw new BadParameterException("The buffer contains no data");
        }
        if (len > data.length) {
            len = data.length;
        } else if (len < 0) {
            len = data.length;
        }
        try {
            // outputstream write method doesn't give any info
            // how many bytes it has written
            socket.getOutputStream().write(data, 0, len);
        } catch (IOException e) {
            StreamStateUtils.setStreamState(streamState, StreamState.ERROR);
            onStateChange(StreamState.ERROR);
            throw new SagaIOException(e);
        }
        return len;
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

    public synchronized void signalReaderException(StreamExceptionalSituation e) {
        this.streamListenerException = e;
        if (listeningReader.isEmpty()) {
            try {
                StreamStateUtils.setStreamState(streamState,
                        streamListenerException.getTargetState());
                onStateChange(streamListenerException.getTargetState());
                this.streamListenerException = null; // important to be
                // synchronized
            } catch (NoSuccessException ex) {
                // shouldn't happen
                logger.debug("oops");
            }
        }
    }

    private void onStateChange(StreamState newState) {
        streamState.internalFire();
        if (newState == StreamState.ERROR)
            streamException.internalFire();
        else if (newState == StreamState.DROPPED)
            streamDropped.internalFire();
    }
    
    public StreamInputStream getInputStream() throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException, NoSuccessException, SagaIOException {
        StreamStateUtils.checkStreamState(streamState, StreamState.OPEN);
        return new org.ogf.saga.impl.stream.InputStream(session, listeningReader.getInputStream());
    }

    public StreamOutputStream getOutputStream() throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException, NoSuccessException, SagaIOException {
        StreamStateUtils.checkStreamState(streamState, StreamState.OPEN);
        try {
            return new org.ogf.saga.impl.stream.OutputStream(session, socket.getOutputStream());
        } catch (IOException e) {
            throw new SagaIOException(e);
        }
    }
}
