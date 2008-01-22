package test.stream;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.stream.Stream;

public class ServerThreadOneRead extends ServerThread {

	private static Logger logger = Logger.getLogger(ServerThreadOneRead.class);	
	
	protected void processStream(Stream stream) throws NotImplemented,
			BadParameter, NoSuccess, IncorrectState, AuthenticationFailed,
			AuthorizationFailed, PermissionDenied, Timeout, IOException,
			DoesNotExist, InterruptedException {
		Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory.createBuffer();

		buffer.setData(new byte[BUFFER_SIZE]);

		// Thread.sleep(8000);

		logger.debug("Server: Attempting to read the message");
		int bytesCnt = stream.read(buffer, buffer.getSize());
		logger.debug("Server: Read " + bytesCnt + " bytes");
		logger.debug("Server: Message content:");
		logger.debug(new String(buffer.getData()).trim());
	}

}
