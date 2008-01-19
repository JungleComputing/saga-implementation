package test.stream;

import java.io.IOException;

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

public class ServerThreadReading extends ServerThread {

	protected void processStream(Stream stream) throws NotImplemented,
			BadParameter, NoSuccess, IncorrectState, AuthenticationFailed,
			AuthorizationFailed, PermissionDenied, Timeout, IOException,
			DoesNotExist, InterruptedException {
		Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory.createBuffer();

		// Thread.sleep(8000);

		System.out.println("Server: Attempting to read the message");
		for (int i = 0; i < 20; i++) {
			buffer.setData(new byte[BUFFER_SIZE]);
			int bytesCnt = stream.read(buffer, buffer.getSize());
			System.out.println("Server: Read " + bytesCnt + " bytes");
			System.out.println("Server: Message content:");
			System.out.println(new String(buffer.getData()).trim());
			Thread.sleep(1000);
		}
	}

}
