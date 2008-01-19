package test.stream;

import java.io.IOException;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
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

public class ServerThread implements Runnable {

	private boolean stop = false;

	protected static final int BUFFER_SIZE = 100;

	private Callback readable = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {
			System.out.println("Stream Server: Stream is readable [METRIC]");
			return true;
		}
	};

	private Callback writeable = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {
			System.out.println("Stream Server: Stream is writable [METRIC]");
			return true;
		}
	};

	private Callback clientConnect = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {
			System.out.println("Stream Server: Client connected [METRIC]");
			return true;
		}
	};

	private Callback stateChanged = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {

			try {
				System.out.println("Stream Server: State changed --> "
						+ metric.getAttribute(Metric.VALUE) + " [METRIC]");
			} catch (Exception e) {
				System.out
						.println("Exception! in callback (changeState) should not happen");
			}
			return true;
		}

	};

	private Callback exception = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {
			System.out.println("Stream Server: Stream exception [METRIC]");
			return true;
		}
	};

	private Callback dropped = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {
			System.out.println("Stream Server: Connection dropped [METRIC]");
			return true;
		}
	};

	public void run() {

		try {
			StreamService service = StreamFactory.createStreamService(new URL(
					"advert://server"));
			service
					.getMetric(
							org.ogf.saga.stream.StreamService.STREAMSERVER_CLIENTCONNECT)
					.addCallback(clientConnect);
			while (!stop) {
				System.out.println("Server Thread: listening...");
				Stream stream = service.serve(30.0f);

				stream.getMetric(org.ogf.saga.stream.Stream.STREAM_READ)
						.addCallback(readable);
				stream.getMetric(org.ogf.saga.stream.Stream.STREAM_WRITE)
						.addCallback(writeable);
				stream.getMetric(org.ogf.saga.stream.Stream.STREAM_EXCEPTION)
						.addCallback(exception);
				stream.getMetric(org.ogf.saga.stream.Stream.STREAM_STATE)
						.addCallback(stateChanged);
				stream.getMetric(org.ogf.saga.stream.Stream.STREAM_DROPPED)
						.addCallback(dropped);

				processStream(stream);

				stream.close();
			}
			service.close();
		} catch (Exception e) {
			System.out.println("Caught exception: Aborting server....");
			e.printStackTrace();
		}
		System.out.println("stopping server...");
	}

	public void stopServer() {
		this.stop = true;
	}

	protected void processStream(Stream stream) throws NotImplemented,
			BadParameter, NoSuccess, IncorrectState, AuthenticationFailed,
			AuthorizationFailed, PermissionDenied, Timeout, IOException,
			DoesNotExist, InterruptedException {
		Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory.createBuffer();

		buffer.setData(new byte[BUFFER_SIZE]);

		// Thread.sleep(8000);

		System.out.println("Server: Attempting to read the message");
		int bytesCnt = stream.read(buffer, buffer.getSize());
		System.out.println("Server: Read " + bytesCnt + " bytes");
		System.out.println("Server: Message content:");
		System.out.println(new String(buffer.getData()).trim());
	}

}
