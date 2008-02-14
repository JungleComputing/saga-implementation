package test.stream;

import junit.framework.TestCase;

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

public class TestMain extends TestCase {

    private static String SERVER_URL = "tcp://localhost:3333";
    private static final int SERVER_WAIT = 3000;

    private static Logger logger = Logger.getLogger(TestMain.class);

    private static Callback readable = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplemented, AuthorizationFailed {
            logger.debug("Stream Client: Stream is readable [METRIC]");
            return true;
        }
    };
    
    private Stream createStream() {
        try {
            return StreamFactory.createStream(new URL(SERVER_URL));
        } catch(Throwable e) {
            logger.error("FAIL: createStream failed", e);
            fail();
        }
        return null;
    }


    // connect to server while it is down, should throw NoSuccess

    public void test1() {

        Stream stream = createStream();

        try {
            stream.connect();
            logger.error("FAIL: connect should throw NoSuccess");
            fail("connect should throw NoSuccess");
        } catch(NoSuccess e) {
            logger.debug("OK: connect threw NoSuccess");
        } catch (Throwable e) {
            logger.error("FAIL: connect should throw NoSuccess, but threw " + e, e);
            fail("connect should throw NoSuccess");
        }

        // should now throw IncorrectState exception
        // because it should enter error state
        
        try {
            stream.connect();
            logger.error("FAIL: connect should now throw IncorrectState");
            fail("connect should now throw IncorrectState");
        } catch (IncorrectState e) {
            logger.debug("OK: connect threw IncorrectState");
        } catch(Throwable e) {
            logger.error("FAIL: connect should now throw IncorrectState, but threw " + e, e);
            fail("connect should now throw IncorrectState");
        }
    }

    // connect & disconnect without writing anything
    // while the server is reading

    public void test2() {

        ServerThread server = new ServerThreadOneRead(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();

        try {
            Thread.sleep(SERVER_WAIT);            
            stream.connect();
        } catch (Throwable e) {
            logger.error("FAIL: got exception ", e);
            fail("got exception" + e);
            return;
        } finally {
            server.stopServer();
        }
        
        try {
            Thread.sleep(1000);
            stream.close();
        } catch (Throwable e) {
            logger.error("FAIL: got exception ", e);
            fail("got exception" + e);
        }
    }

    // send 1 msg then check if the connection dropped condition is detected...

    public void test3() {
        // TODO: reverse
        ServerThread server = new ServerThreadReading(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();

        try {
            Thread.sleep(SERVER_WAIT);

            stream.connect();
        } catch(Throwable e) {
            e.printStackTrace();
        } finally {
            server.stopServer();
        }
        
        try {
            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData("Hello World".getBytes());

            stream.write(buffer);
            stream.close();
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // testing the correctness of wait on the client side
    // trying out writeability & exception --- none should be detected

    public void test4() {
        ServerThread server = new ServerThreadWriting(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();

        try {
            stream.getMetric(Stream.STREAM_READ)
                    .addCallback(readable);
            Thread.sleep(SERVER_WAIT);
            stream.connect();
        } catch (Throwable e) {
            logger.error("Got exception", e);
            fail("Got exception " + e);
        } finally { 
            server.stopServer();
        }
        try {
            int outcome = stream.waitStream(Activity.EXCEPTION.getValue()
                    | Activity.WRITE.getValue(), 8.0f);

            if (outcome == 0) {
                logger.debug("Client: nothing detected [WAIT]");
            } else if ((outcome & Activity.EXCEPTION.getValue()) != 0) {
                logger.error("Client: exceptional condition detected [WAIT]");
                fail("Client: exceptional condition detected [WAIT]");
            } else if ((outcome & Activity.READ.getValue()) != 0) {
                logger.error("Client: readable detected [WAIT]");
                fail("Client: readable detected [WAIT]");
            } else if ((outcome & Activity.WRITE.getValue()) != 0) {
                logger.error("Client: writable detected [WAIT]");
                fail("Client: writable detected [WAIT]");
            } else {
                logger.error("Client: outcome = " + outcome);
                fail("Client: outcome = " + outcome);
            }
        } catch(NotImplemented e) {
            logger.warn("WARNING: waitStream gave NotImplemented", e);
        } catch (Throwable e) {
            logger.error("Got exception", e);
            fail("Got exception " + e);
        }
        
        try {
            stream.close();
        } catch (Throwable e) {
            logger.error("Got exception", e);
            fail("Got exception " + e);
        }

    }

    // testing readability detection on server side by waitStream call

    public void test5() {
        ServerThread server = new ServerThreadWait(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();

        try {
            Thread.sleep(SERVER_WAIT);
            stream.connect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stopServer();
        }
        try {

            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData("Hello World".getBytes());

            stream.write(buffer);

            Thread.sleep(3000);

            stream.close();
            
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // we will try to write more than the reading buffer can store
    // and see what happens

    // currently the buffer is 1024 bytes long

    public void test6() {
        ServerThread server = new ServerThreadReading(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        try {
            Thread.sleep(SERVER_WAIT);
            stream.connect();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            server.stopServer();
        }
        try {
            Buffer buffer = BufferFactory.createBuffer();
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
    }

    // tries to close not opened stream service

    public void test7() {

        try {

            StreamService service = StreamFactory.createStreamService(new URL(
                    SERVER_URL));

            service.close();
        } catch (Throwable e) {
            logger.error("FAIL: StreamService.close threw exception", e);
            fail("StreamService.close threw exception");
        }

    }

    // multiple calls to close should not raise an exception
    // + test for throwing Timeout exception in serve method

    public void test8() {

        StreamService service = null;

        try {

            service = StreamFactory.createStreamService(new URL(SERVER_URL));

            service.serve(1.0f);

            logger.debug("TEST");
        } catch (Timeout t) {
            logger.debug("OK: got timeout exception");
        } catch (Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
        }

        try {
            service.close();
            service.close();
            logger.debug("OK: multiple close invocations allowed");
        } catch (Throwable e) {
            logger.error("FAIL: exception from close()", e);
            fail("got exception " + e);
        }

    }

    // getContext() method test

    public void test9() {
        // the stream should be or have been in Open state before

        Stream stream = createStream();

        try {
            stream.getContext();
            logger.error("FAIL: Should have thrown IncorrectState exception");
            fail("getContext should have thrown IncorrectState");
        } catch (IncorrectState e) {
            logger.debug("OK: Should have thrown IncorrectState exception");
        } catch (Throwable e) {
            logger.error("FAIL: Should have thrown IncorrectState exception", e);
            fail("getContext gave " + e);
        }
    }

    // connect() method test with no service present, and getContext().

    public void test10() {

        Stream stream = createStream();

        try {
            stream.connect(); // erroneous call changes state to 'Error'
            logger.error("FAIL: connect should have thrown NoSuccess");
            fail("connect should have thrown NoSuccess");
        } catch (NoSuccess e) {
            logger.debug("OK: Should have thrown NoSuccess exception");
        } catch (Throwable e) {
            logger.error("FAIL: Should have thrown NoSuccess exception", e);
            fail("connect should have thrown NoSuccess and not " + e);
        }

        try {
            stream.getContext();    // should not give context because the
                                    // stream wasn't opened
            logger.error("FAIL: getContext should have thrown IncorrectState");
            fail("getContext should have thrown IncorrectState");
        } catch (IncorrectState e) {
            logger.debug("OK: Should have thrown IncorrectState exception");
        } catch (Throwable e) {
            logger.error("FAIL: Should have thrown IncorrectState exception", e);
            fail("getContext should have thrown IncorrectState and not " + e);
        }
    }

    // getContext() test -- we go from Open to some final state and expect
    // this method to finish successfully

    public void test11() {

        ServerThread server = new ServerThreadOneRead(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        Buffer buffer = null;

        try {
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }
        
        try {
            buffer = BufferFactory.createBuffer();
            buffer.setData("Hello World".getBytes());

            stream.write(buffer);
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
        }
        
        try {
            Thread.sleep(5000);
            stream.write(buffer);
        } catch (IncorrectState e) {
            logger.debug("OK: Should have thrown IncorrectState -- the server is down");
        } catch (Throwable e) {
            logger.error("FAIL: Should have thrown IncorrectState -- the server is down", e);
            fail("should have thrown IncorrectState -- the server is down, but got " + e);
        }

        // we should be now in DROPPED state

        try {
            stream.getContext();
            logger.debug("OK: we have received security context");
        } catch (Throwable e) {
            logger.error("FAIL: we should have received security context", e);
            fail("getContext gave " + e);
        }

    }

    // if we successfully connect to server we should not be able to do it
    // for the second time

    public void test12() {

        ServerThread server = new ServerThreadOneRead(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }

        try {
            stream.connect();
            logger.error("FAIL: 2nd connect should throw IncorrectState");
            fail("2nd connect should throw IncorrectState");
        } catch (IncorrectState e) {
            logger.debug("OK: Should detect incorrect state");
        } catch (Throwable e) {
            logger.error("FAIL: 2nd connect should throw IncorrectState", e);
            fail("2nd connect should throw IncorrectState, not " + e);
        }

        try {
            stream.close();
            Thread.sleep(1000);
        } catch (Throwable e) {
            logger.error("FAIL: Should detect incorrect state", e);
            fail("close() should give IncorrectState, not " + e);
        }
    }

    // close should throw IncorrectState when the stream is in New
    // or Error state

    public void test13() {

        Stream stream = createStream();

        try {
            stream.close();
            logger.error("FAIL: successful close on unconnected stream");
            fail("Successful close on unconnected stream");
        } catch (IncorrectState e) {
            logger.debug("OK: Threw IncorrectState exception");
        } catch (Throwable e) {
            logger.error("FAIL: Threw other exception", e);
            fail("threw wrong exception " + e);
        }

        stream = createStream();
        
        try {
            stream.connect(); // should fail
            logger.error("FAIL: Connect should have failed");
            fail("Connect should have failed");
            return;
        } catch (NoSuccess e) {
            logger.debug("OK: Connect not succeeded -- should switch to Error state");
        } catch (Throwable e) {
            logger.error("FAIL: Threw other exception", e);
            fail("threw wrong exception" + e);
            return;
        }

        try {
            stream.close();
        } catch (IncorrectState e) {
            logger.debug("OK: Threw IncorrectState exception");
        } catch (Throwable e) {
            logger.error("FAIL: Threw other exception", e);
            fail("threw wrong exception" + e);
        }
    }

    // read with negative length

    public void test14() {

        ServerThread server = new ServerThreadWriting(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }
        try {
            Buffer b = BufferFactory.createBuffer();

            int nBytes = stream.read(b, -223);

            logger.error("FAIL: Successful; " + nBytes + " bytes read");
            fail("read(b, -223) should throw an exception");
        } catch (BadParameter e) {
            logger.debug("OK: BadParameter thrown");
        } catch (Throwable e) {
            logger.debug("FAIL: BadParameter should be thrown", e);
            fail("threw wrong exception " + e);
        }

        try {
            stream.close();
        } catch (Throwable e) {
            logger.debug("FAIL: Failure while closing connection to server");
            fail("Failure while closing connection to server");
        }
    }

    // read with length == 0

    public void test15() {
        ServerThread server = new ServerThreadWriting(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }
        
        try {
            Buffer b = BufferFactory.createBuffer();

            int nBytes = stream.read(b, 0);

            logger.debug("OK: Successful; " + nBytes + " bytes read");

            nBytes = stream.read(b, 50);

            logger.debug("OK: Successful; " + nBytes + " bytes read");

        } catch (Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
        }

        try {
            stream.close();
        } catch (Throwable e) {
            logger.error("FAIL: Failure while closing connection to server", e);
            fail("Failure while closing connection to server, " + e);
        }
    }

    // write with negative length

    public void test16() {
        ServerThread server = new ServerThreadOneRead(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }
        
        try {
            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData("Hello world".getBytes());

            stream.write(buffer, -323);
            logger.debug("OK: Implementation figured out how to handle negative value of length");
        } catch (Throwable e) {
            logger.debug("FAIL: Situation was not dealt with properly");
        }

        try {
            stream.close();
        } catch (Throwable e) {
            logger.error("FAIL: Failure while closing connection to server", e);
            fail("close()");
        }
    }

    // write with 0 length

    public void test17() {
        ServerThread server = new ServerThreadOneRead(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        
        
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }

        try {
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

    public void test18() {
        ServerThread server = new ServerThreadOneRead(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }
        
        try {
            Buffer buffer = BufferFactory.createBuffer(1000);

            stream.write(buffer, -323);
            logger.debug("OK: Implementation figured out how to handle negative value of length");
        } catch (Throwable e) {
            logger.error("FAIL: Situation was not dealt with properly", e);
            fail("got exception " + e);
        }

        try {
            stream.close();
        } catch (Throwable e) {
            logger.error("FAIL: Failure while closing connection to server", e);
            fail("close gave exception " + e);
        }
    }

    // calling wait with zeroed "what" flag

    public void test19() {
        ServerThread server = new ServerThreadWriting(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }
        
        try {
            int val = stream.waitStream(0, 5.0f);
            logger.debug("OK: Survived with 'incorrect' input. Result = "
                            + val);

        } catch (Throwable e) {
            logger.error("FAIL: waitStream gave exception", e);
            fail("waitStream gave exception " + e);
        }

        try {
            stream.close();
        } catch (Throwable e) {
            logger.error("FAIL: Failure while closing connection to server", e);
            fail("close gave exception " + e);
        }

    }

    // calling wait in state other than open

    public void test20() {
        ServerThread server = new ServerThreadWriting(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();
        Stream stream = createStream();
        try {           
            Thread.sleep(SERVER_WAIT);
            stream.connect(); // should be successful
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
            return;
        } finally {
            server.stopServer();
        }

        try {
            stream.close();
        } catch(Throwable e) {
            logger.error("FAIL: close failed", e);
            fail("close threw exception " + e);
            return;
        }
        
        try {
            stream.waitStream(0, 5.0f);
            logger.error("FAIL: Should have thrown IncorrectState");
            fail("waitStream should have thrown IncorrectState");
        } catch (IncorrectState e) {
            logger.debug("OK: Threw IncorrectState");
        } catch (Throwable e) {
            logger.error("FAIL: Should have thrown IncorrectState", e);
            fail("waitStream should have thrown IncorrectState, not " + e);

        }
    }
}
