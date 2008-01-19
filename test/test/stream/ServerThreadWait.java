package test.stream;

import java.io.IOException;

import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.stream.Activity;
import org.ogf.saga.stream.Stream;

public class ServerThreadWait extends ServerThread {

	protected void processStream(Stream stream) throws NotImplemented,
			BadParameter, NoSuccess, IncorrectState, AuthenticationFailed,
			AuthorizationFailed, PermissionDenied, Timeout, IOException,
			DoesNotExist, InterruptedException {

		Thread.sleep(10000);

		System.out.println("Server: Calling wait...");
		
		int outcome = stream.waitStream(Activity.EXCEPTION.getValue() | Activity.READ.getValue(),
				8.0f);

		if (outcome == 0)
			System.out.println("Server: nothing detected [WAIT]");

		if ((outcome & Activity.EXCEPTION.getValue()) != 0)
			System.out
					.println("Server: exceptional condition detected [WAIT]");
		if ((outcome & Activity.READ.getValue()) != 0)
			System.out.println("Server: readable detected [WAIT]");
		if ((outcome & Activity.WRITE.getValue()) != 0)
			System.out.println("Server: writable detected [WAIT]");		

	}

}
