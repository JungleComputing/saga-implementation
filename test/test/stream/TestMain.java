package test.stream;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.stream.Activity;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;

public class TestMain {

	private static String SERVER_URL = "advert://server";

	private static Callback readable = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {
			System.out.println("Stream Client: Stream is readable [METRIC]");
			return true;
		}
	};

	public static void main(String args[]) {

		Test.initialize();

		test3();

	}

	// connect to server while it is down, should throw NoSuccess

	public static void test1() {

		Stream stream = null;

		try {

			stream = StreamFactory.createStream(new URL(SERVER_URL));
			stream.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// should now throw IncorrectState exception
		// because it should enter error state

		try {
			stream.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// connect & disconnect without writing anything
	// while the server is reading

	public static void test2() {

		ServerThread server = new ServerThread();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1500);

			stream = StreamFactory.createStream(new URL(SERVER_URL));
			stream.connect();

			Thread.sleep(1000);

			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		server.stopServer();

	}

	public static void test3() {
		ServerThread server = new ServerThreadReading();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1500);

			stream = StreamFactory.createStream(new URL(SERVER_URL));
			stream.connect();

			Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory
					.createBuffer();
			buffer.setData("Hello World".getBytes());

			stream.write(buffer);

			// Thread.sleep(10000);

			stream.close();
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		server.stopServer();
	}

	public static void test4() {
		ServerThread server = new ServerThreadWriting();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1500);

			stream = StreamFactory.createStream(new URL(SERVER_URL));

			stream.getMetric(org.ogf.saga.stream.Stream.STREAM_READ)
					.addCallback(readable);

			stream.connect();

			int outcome = stream.waitStream(Activity.EXCEPTION.getValue()
					| Activity.WRITE.getValue(), 8.0f);

			if (outcome == 0)
				System.out.println("Client: nothing detected [WAIT]");

			if ((outcome & Activity.EXCEPTION.getValue()) != 0)
				System.out
						.println("Client: exceptional condition detected [WAIT]");
			if ((outcome & Activity.READ.getValue()) != 0)
				System.out.println("Client: readable detected [WAIT]");
			if ((outcome & Activity.WRITE.getValue()) != 0)
				System.out.println("Client: writable detected [WAIT]");

			stream.close();
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		server.stopServer();
	}

	public static void test5() {
		ServerThread server = new ServerThreadWait();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1500);

			stream = StreamFactory.createStream(new URL(SERVER_URL));

			stream.connect();

			Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory
					.createBuffer();
			buffer.setData("Hello World".getBytes());

			stream.write(buffer);

			Thread.sleep(3000);

			stream.close();
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		server.stopServer();
	}

	// we will try to write more than the reading buffer can store
	// and see what happens

	// currently the buffer is 1024 bytes long
	
	public static void test6() {
		ServerThread server = new ServerThreadReading();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1500);

			stream = StreamFactory.createStream(new URL(SERVER_URL));

			stream.connect();

			Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory
					.createBuffer();
			byte big[] = new byte[1000];
			buffer.setData(big);

			stream.write(buffer);
			stream.write(buffer);

			//Thread.sleep(3000);

			stream.close();
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		server.stopServer();
	}

}
