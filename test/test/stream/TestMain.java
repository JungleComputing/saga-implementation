package test.stream;

import org.apache.log4j.Logger;
import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.stream.Activity;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamService;

public class TestMain {

	private static String SERVER_URL = "advert://server";

	private static Logger logger = Logger.getLogger(TestMain.class);

	private static Callback readable = new Callback() {
		public boolean cb(Monitorable mt, Metric metric, Context ctx)
				throws NotImplemented, AuthorizationFailed {
			logger.debug("Stream Client: Stream is readable [METRIC]");
			return true;
		}
	};

	public static void main(String args[]) {

		Test.initialize();

		if (args.length > 0) {
			String arg = args[0];
			try {
				int testNum = Integer.parseInt(arg);
				
				System.out.println("=== RUNNING TEST " + testNum + " ===");
				
				if (testNum == 1)      test1();
				else if (testNum == 2) test2();
				else if (testNum == 3) test3();
				else if (testNum == 4) test4();
				else if (testNum == 5) test5();
				else if (testNum == 6) test6();
				else if (testNum == 7) test7();
				else if (testNum == 8) test8();
				else if (testNum == 9) test9();
				else if (testNum == 10) test10();
				else if (testNum == 11) test11();
				else if (testNum == 12) test12();
				else if (testNum == 13) test13();
				else if (testNum == 14) test14();
				else if (testNum == 15) test15();
				else if (testNum == 16) test16();
				else if (testNum == 17) test17();
				else if (testNum == 18) test18();
				else if (testNum == 19) test19();
				else if (testNum == 20) test20();
				
			} catch (Exception e) {
				test20();
			}
		} else
			test20();
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

		ServerThread server = new ServerThreadOneRead();
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
				logger.debug("Client: nothing detected [WAIT]");

			if ((outcome & Activity.EXCEPTION.getValue()) != 0)
				logger.debug("Client: exceptional condition detected [WAIT]");
			if ((outcome & Activity.READ.getValue()) != 0)
				logger.debug("Client: readable detected [WAIT]");
			if ((outcome & Activity.WRITE.getValue()) != 0)
				logger.debug("Client: writable detected [WAIT]");

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

			// Thread.sleep(3000);

			stream.close();
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		server.stopServer();
	}

	// tries to close not opened stream service

	public static void test7() {

		try {

			StreamService service = StreamFactory.createStreamService(new URL(
					"advert://server"));

			service.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// multiple calls to close should not raise an exception
	// + test for throwing Timeout exception in serve method

	public static void test8() {

		StreamService service = null;

		try {

			service = StreamFactory.createStreamService(new URL(
					"advert://server"));

			service.serve(1.0f);

			logger.debug("TEST");
		} catch (Timeout t) {
			logger.debug("OK: Thrown timeout exception");
		} catch (Exception e) {
			logger.debug("WRONG: Exception thrown");
			e.printStackTrace();
		}

		try {
			service.close();
			service.close();
			logger.debug("OK: multiple close invocations allowed");
		} catch (Exception e) {
			logger.debug("WRONG: Exception from close() ");
			e.printStackTrace();
		}

	}

	// getContext() method test

	public static void test9() {
		// the stream should be or have been in Open state before

		Stream stream = null;

		try {

			stream = StreamFactory.createStream(new URL("url://sample"));

			stream.getContext();
			logger.debug("FAIL: Should have thrown IncorrectState exception");

		} catch (IncorrectState e) {
			logger.debug("OK: Should have thrown IncorrectState exception");
		} catch (Exception e) {
			logger.debug("FAIL: Should have thrown IncorrectState exception");
			e.printStackTrace();
		}

	}

	// getContext() method test

	public static void test10() {

		Stream stream = null;

		try {

			stream = StreamFactory.createStream(new URL("advert://server"));

			stream.connect(); // erroneous call changes state to 'Error'
		} catch (NoSuccess e) {
			logger.debug("OK: Should have thrown NoSuccess exception");
		} catch (Exception e) {
			logger.debug("FAIL: Should have thrown NoSuccess exception");
			e.printStackTrace();
		}

		try {
			stream.getContext(); // should not give context because the
			// stream
			// wasn't opened

		} catch (IncorrectState e) {
			logger.debug("OK: Should have thrown IncorrectState exception");
		} catch (Exception e) {
			logger.debug("FAIL: Should have thrown IncorrectState exception");
			e.printStackTrace();
		}

	}

	// getContext() test -- we go from Open to some final state and expect
	// this method to finish successfully

	// TODO: 'Unknown' TYPE of the Context should be recognized

	public static void test11() {

		ServerThread server = new ServerThreadOneRead();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

			Buffer buffer = org.ogf.saga.impl.buffer.BufferFactory
					.createBuffer();
			buffer.setData("Hello World".getBytes());

			stream.write(buffer);

			Thread.sleep(5000);
			stream.write(buffer);

		} catch (NoSuccess e) {
			logger
					.debug("OK: Should have thrown NoSuccess -- the server is down");
		} catch (Exception e) {
			logger
					.debug("FAIL: Should have thrown NoSuccess -- the server is down");
			e.printStackTrace();
		}

		// we should be now in DROPPED state

		try {
			stream.getContext();
			logger.debug("OK: we should have received security context");
		} catch (Exception e) {
			logger.debug("FAIL: we should have received security context");
		}

	}

	// if we successfully connect to server we should not be able to do it
	// for the second time

	public static void test12() {

		ServerThread server = new ServerThreadOneRead();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

		} catch (Exception e) {
			logger
					.debug("FAIL: Should not have thrown exceptions before 2nd connect");
		}

		try {
			stream.connect();
		} catch (IncorrectState e) {
			logger.debug("OK: Should detect incorrect state");
		} catch (Exception e) {
			logger.debug("FAIL: Should detect incorrect state");
		}

		try {
			stream.close();
		} catch (IncorrectState e) {
			logger.debug("OK: Should detect incorrect state");
		} catch (Exception e) {
			logger.debug("FAIL: Should detect incorrect state");
		}

	}

	// close should throw IncorrectState when the stream is in New
	// or Error state

	public static void test13() {

		Stream stream = null;

		try {
			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.close();
			logger.debug("FAIL: Successful close");
		} catch (IncorrectState e) {
			logger.debug("OK: Threw IncorrectState exception");
		} catch (Exception e) {
			logger.debug("FAIL: Threw other exception");
		}

		try {
			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should fail
			logger.debug("FAIL: Connect should have failed");
		} catch (NoSuccess e) {
			logger
					.debug("OK: Connect not succeded -- should switch to Error state");
		} catch (Exception e) {
			logger.debug("FAIL: Threw other exception");
		}

		try {
			stream.close();
		} catch (IncorrectState e) {
			logger.debug("OK: Threw IncorrectState exception");
		} catch (Exception e) {
			logger.debug("FAIL: Threw other exception");
		}
	}

	// read with negative length

	public static void test14() {

		ServerThread server = new ServerThreadWriting();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

			Buffer b = BufferFactory.createBuffer();

			int nBytes = stream.read(b, -223);

			logger.debug("OK: Successful; " + nBytes + " bytes read");

		} catch (BadParameter e) {
			logger.debug("OK: BadParameter thrown");
		} catch (Exception e) {
			logger
					.debug("FAIL: It should be successful or BadParameter should be thrown");
		}

		try {
			stream.close();
		} catch (Exception e) {
			logger.debug("FAIL: Failure while closing connection to server");
		}

	}

	// read with length == 0

	public static void test15() {
		ServerThread server = new ServerThreadWriting();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

			Buffer b = BufferFactory.createBuffer();

			int nBytes = stream.read(b, 0);

			logger.debug("OK: Successful; " + nBytes + " bytes read");

			nBytes = stream.read(b, 50);

			logger.debug("OK: Successful; " + nBytes + " bytes read");

		} catch (BadParameter e) {
			logger.debug("OK: BadParameter thrown");
		} catch (Exception e) {
			logger
					.debug("FAIL: It should be successful or BadParameter should be thrown");
		}

		try {
			stream.close();
		} catch (Exception e) {
			logger.debug("FAIL: Failure while closing connection to server");
		}
	}

	// write with negative length

	public static void test16() {
		ServerThread server = new ServerThreadOneRead();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

			Buffer buffer = BufferFactory.createBuffer();
			buffer.setData("Hello world".getBytes());

			stream.write(buffer, -323);
			logger
					.debug("OK: Implementation figured out how to handle negative value of length");
		} catch (BadParameter e) {
			logger.debug("OK: BadParameter was thrown");
		} catch (Exception e) {
			logger.debug("FAIL: Situation was not dealt with properly");
		}

		try {
			stream.close();
		} catch (Exception e) {
			logger.debug("FAIL: Failure while closing connection to server");
		}
	}

	// write with 0 length

	public static void test17() {
		ServerThread server = new ServerThreadOneRead();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

			Buffer buffer = BufferFactory.createBuffer();
			buffer.setData("Hello world".getBytes());

			int nBytes = stream.write(buffer, 0);
			logger.debug("OK: Written " + nBytes + " bytes");
			nBytes = stream.write(buffer, buffer.getSize());
			logger.debug("OK: Written " + nBytes + " bytes");

		} catch (Exception e) {
			logger.debug("FAIL: Situation was not dealt with properly");
		}

		try {
			stream.close();
		} catch (Exception e) {
			logger.debug("FAIL: Failure while closing connection to server");
		}
	}

	// write -- passing uninitialized buffer

	public static void test18() {
		ServerThread server = new ServerThreadOneRead();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

			Buffer buffer = BufferFactory.createBuffer();

			stream.write(buffer, -323);
			logger
					.debug("OK: Implementation figured out how to handle negative value of length");
		} catch (BadParameter e) {
			logger.debug("OK: BadParameter was thrown");
		} catch (Exception e) {
			logger.debug("FAIL: Situation was not dealt with properly");
		}

		try {
			stream.close();
		} catch (Exception e) {
			logger.debug("FAIL: Failure while closing connection to server");
		}
	}

	// calling wait with zeroed "what" flag

	public static void test19() {
		ServerThread server = new ServerThreadWriting();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful

			int val = stream.waitStream(0, 5.0f);

			logger
					.debug("OK: Survived with 'incorrect' input. Result = "
							+ val);

		} catch (Exception e) {
			logger.debug("FAIL: Thrown exception");
		}

		try {
			stream.close();
		} catch (Exception e) {
			logger.debug("FAIL: Failure while closing connection to server");
		}

	}

	// calling wait in state other than open

	public static void test20() {
		ServerThread server = new ServerThreadWriting();
		Thread sThread = new Thread(server);

		sThread.start();

		Stream stream = null;

		try {

			Thread.sleep(1400);

			stream = StreamFactory.createStream(new URL("advert://server"));
			stream.connect(); // should be successful
			stream.close();

			stream.waitStream(0, 5.0f);

			logger.debug("FAIL: Should have thrown IncorrectState");

		} catch (IncorrectState e) {
			logger.debug("OK: Threw IncorrectState");
		} catch (Exception e) {
			logger.debug("FAIL: Should have thrown IncorrectState");
		}
	}
}
