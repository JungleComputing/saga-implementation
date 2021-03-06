package test.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.ogf.saga.url.URL;

public class ServerThreadWriting extends ServerThread {

    private static Logger logger = LoggerFactory
            .getLogger(ServerThreadWriting.class);

    public ServerThreadWriting(URL url) {
        super(url);
    }

    protected void processStream(Stream stream) throws NotImplementedException,
            BadParameterException, NoSuccessException, IncorrectStateException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, SagaIOException,
            DoesNotExistException, InterruptedException {

        logger.debug("Server: Attempting to send a message");
        Buffer buffer = BufferFactory.createBuffer();
        buffer.setData("Hello World".getBytes());

        stream.write(buffer);
        Thread.sleep(1000);
    }

}
