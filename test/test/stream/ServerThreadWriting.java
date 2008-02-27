package test.stream;

import java.io.IOException;

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
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.stream.Stream;

public class ServerThreadWriting extends ServerThread {

    private static Logger logger = Logger.getLogger(ServerThreadWriting.class);

    public ServerThreadWriting(String url) {
        super(url);
    }

    protected void processStream(Stream stream) throws NotImplementedException,
            BadParameterException, NoSuccessException, IncorrectStateException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, IOException,
            DoesNotExistException, InterruptedException {

        logger.debug("Server: Attempting to send a message");
        Buffer buffer = BufferFactory.createBuffer();
        buffer.setData("Hello World".getBytes());

        stream.write(buffer);
    }

}
