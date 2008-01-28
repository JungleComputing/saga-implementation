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

public class ServerThreadWriting extends ServerThread {

	private static Logger logger = Logger.getLogger(ServerThreadWriting.class);
	
	public ServerThreadWriting(String url) {
		super(url);
	}
	
	protected void processStream(Stream stream) throws NotImplemented,
			BadParameter, NoSuccess, IncorrectState, AuthenticationFailed,
			AuthorizationFailed, PermissionDenied, Timeout, IOException,
			DoesNotExist, InterruptedException {

		Thread.sleep(5000);

		logger.debug("Server: Attempting to send a message");
		Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory.createBuffer();
		buffer.setData("Hello World".getBytes());
		
		stream.write(buffer);
	}

}
