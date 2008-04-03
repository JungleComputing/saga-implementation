package test.stream;

import org.apache.log4j.Logger;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.stream.Stream;

public class ServerThreadReading extends ServerThread {

    private static Logger logger = Logger.getLogger(ServerThreadReading.class);

    public ServerThreadReading(String url) {
        super(url);
    }

    protected void processStream(Stream stream) throws NotImplementedException,
            BadParameterException, NoSuccessException, IncorrectStateException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, SagaIOException,
            DoesNotExistException, InterruptedException {
        Buffer buffer = BufferFactory.createBuffer();

        // Thread.sleep(8000);

        logger.debug("Server: Attempting to read the message");
        for (int i = 0; i < 20; i++) {
            buffer.setData(new byte[BUFFER_SIZE]);
            int bytesCnt = stream.read(buffer, buffer.getSize());
            logger.debug("Server: Read " + bytesCnt + " bytes");
        }
    }

}