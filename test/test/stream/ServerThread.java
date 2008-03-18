package test.stream;

import org.apache.log4j.Logger;
import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamService;

public class ServerThread implements Runnable {

    private boolean stop = false;

    protected static final int BUFFER_SIZE = 100;

    private static Logger logger = Logger.getLogger(ServerThread.class);

    private String url;
    
    Throwable throwable = null;

    public ServerThread(String url) {
        this.url = url;
    }

    private Callback readable = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
            logger.debug("Stream Server: Stream is readable [METRIC]");
            return true;
        }
    };

    private Callback writeable = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
            logger.debug("Stream Server: Stream is writable [METRIC]");
            return true;
        }
    };

    private Callback clientConnect = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
            logger.debug("Stream Server: Client connected [METRIC]");
            return true;
        }
    };

    private Callback stateChanged = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {

            try {
                logger.debug("Stream Server: State changed --> "
                        + metric.getAttribute(Metric.VALUE) + " [METRIC]");
            } catch (Exception e) {
                logger
                        .debug("Exception! in callback (changeState) should not happen");
            }
            return true;
        }

    };

    private Callback exception = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
            logger.debug("Stream Server: Stream exception [METRIC]");
            return true;
        }
    };

    private Callback dropped = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
            logger.debug("Stream Server: Connection dropped [METRIC]");
            return true;
        }
    };

    public void run() {
        StreamService service = null;
        try {
            service = StreamFactory.createStreamService(new URL(url));
            service.getMetric(StreamService.STREAMSERVER_CLIENTCONNECT)
                    .addCallback(clientConnect);
            while (!stop) {
                logger.debug("Server Thread: listening...");
                Stream stream = service.serve(30.0f);

                stream.getMetric(Stream.STREAM_READ).addCallback(readable);
                stream.getMetric(Stream.STREAM_WRITE).addCallback(writeable);
                stream.getMetric(Stream.STREAM_EXCEPTION).addCallback(exception);
                stream.getMetric(Stream.STREAM_STATE).addCallback(stateChanged);
                stream.getMetric(Stream.STREAM_DROPPED).addCallback(dropped);
                
                try {
                    processStream(stream);
                } finally {
                    stream.close();
                }
            }
            service.close();
        } catch (Throwable e) {
            logger.debug("Caught exception: Aborting server....", e);
            setException(e);
        } finally {
            try {
                service.close();
            } catch(Throwable e) {
                // ignored
            }
        }
        logger.debug("stopping server...");
    }

    public void stopServer() {
        this.stop = true;
    }
    
    public synchronized Throwable getException() {
        return throwable;
    }
    
    private synchronized void setException(Throwable e) {
        throwable = e;
    }

    protected void processStream(Stream stream) throws NotImplementedException,
            BadParameterException, NoSuccessException, IncorrectStateException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, SagaIOException,
            DoesNotExistException, InterruptedException {
        // The default just keeps the server around for 10 seconds.
        Thread.sleep(10000);
    }

}
