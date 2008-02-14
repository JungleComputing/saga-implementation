package test.stream;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamService;

public abstract class ServerThread implements Runnable {

    private boolean stop = false;

    protected static final int BUFFER_SIZE = 100;

    private static Logger logger = Logger.getLogger(ServerThread.class);

    private String url;

    public ServerThread(String url) {
        this.url = url;
    }

    private Callback readable = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplemented, AuthorizationFailed {
            logger.debug("Stream Server: Stream is readable [METRIC]");
            return true;
        }
    };

    private Callback writeable = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplemented, AuthorizationFailed {
            logger.debug("Stream Server: Stream is writable [METRIC]");
            return true;
        }
    };

    private Callback clientConnect = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplemented, AuthorizationFailed {
            logger.debug("Stream Server: Client connected [METRIC]");
            return true;
        }
    };

    private Callback stateChanged = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplemented, AuthorizationFailed {

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
                throws NotImplemented, AuthorizationFailed {
            logger.debug("Stream Server: Stream exception [METRIC]");
            return true;
        }
    };

    private Callback dropped = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplemented, AuthorizationFailed {
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

    protected abstract void processStream(Stream stream) throws NotImplemented,
            BadParameter, NoSuccess, IncorrectState, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, IOException,
            DoesNotExist, InterruptedException;

}
