package test.stream;

import org.apache.log4j.Logger;
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
import org.ogf.saga.stream.Activity;
import org.ogf.saga.stream.Stream;

public class ServerThreadWait extends ServerThread {

    private static Logger logger = Logger.getLogger(ServerThreadWait.class);

    public ServerThreadWait(String url) {
        super(url);
    }

    protected void processStream(Stream stream) throws NotImplementedException,
            BadParameterException, NoSuccessException, IncorrectStateException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, SagaIOException,
            DoesNotExistException, InterruptedException {

        Thread.sleep(10000);

        logger.debug("Server: Calling wait...");

        int outcome = stream.waitFor(Activity.EXCEPTION.getValue()
                | Activity.READ.getValue(), 8.0f);

        if (outcome == 0)
            logger.debug("Server: nothing detected [WAIT]");

        if ((outcome & Activity.EXCEPTION.getValue()) != 0)
            logger.debug("Server: exceptional condition detected [WAIT]");
        if ((outcome & Activity.READ.getValue()) != 0)
            logger.debug("Server: readable detected [WAIT]");
        if ((outcome & Activity.WRITE.getValue()) != 0)
            logger.debug("Server: writable detected [WAIT]");

    }

}
