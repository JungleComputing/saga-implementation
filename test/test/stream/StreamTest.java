package test.stream;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.stream.Activity;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.stream.StreamService;

public class StreamTest extends TestCase {

    private static String SERVER_URL = "tcp://localhost:3333";
    private static final int SERVER_WAIT = 3000;

    private static Logger logger = Logger.getLogger(StreamTest.class);

    private static Callback readable = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
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

    public void testConnect1() {

        Stream stream = createStream();

        try {
            stream.connect();
            logger.error("FAIL: connect should throw NoSuccess");
            fail("connect should throw NoSuccess");
        } catch(NoSuccessException e) {
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
        } catch (IncorrectStateException e) {
            logger.debug("OK: connect threw IncorrectState");
        } catch(Throwable e) {
            logger.error("FAIL: connect should now throw IncorrectState, but threw " + e, e);
            fail("connect should now throw IncorrectState");
        }
    }
    
    private void close(Stream stream) {
        try {
            stream.close();
        } catch(Throwable e) {
            logger.error("close gave exception", e);
            fail("close gave exception: " + e);            		
        }
        try {
            Thread.sleep(2000);
        } catch(Throwable e) {
            // ignored
        }
    }
    
    private void checkServer(ServerThread server, Thread sThread) {
        
        try {
            sThread.join();
        } catch (InterruptedException e1) {
            // ignored
        }
        
        Throwable ex = server.getException();
        if (ex != null) {
            logger.error("FAIL: reader got exception", ex);
            fail("reader got exception " + ex);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignored
        }
    }

    // connect & disconnect without writing anything
    // while the server is reading

    public void testConnect2() {

        ServerThread server = new ServerThread(SERVER_URL);
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
        
        close(stream);
        
        checkServer(server, sThread);
    }

    // read 1 msg then check if the connection dropped condition is detected...

    public void testRead1() {
        ServerThread server = new ServerThreadWriting(SERVER_URL);
        Thread sThread = new Thread(server);

        sThread.start();

        Stream stream = createStream();

        try {
            Thread.sleep(SERVER_WAIT);

            stream.connect();
        } catch(Throwable e) {
            logger.error("FAIL: got exception ", e);
            fail("got exception" + e);
            return;
        } finally {
            server.stopServer();
        }
        
        try {
            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData(new byte[1024]);

            logger.debug("Attempting to read the message");
            int bytesCnt = stream.read(buffer, buffer.getSize());
            logger.debug("Read " + bytesCnt + " bytes");
            logger.debug("Message content:");
            logger.debug(new String(buffer.getData()).trim());
        } catch (Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
        }
        
        try {
            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData(new byte[1024]);
            logger.debug("Attempting to read a second message");
            int bytesCnt = stream.read(buffer, buffer.getSize());
            if (bytesCnt != 0) {
                logger.error("FAIL: return value of read != 0 at EOF");
                fail("return value of read != 0 at EOF");
            }
        } catch(Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
        }
        close(stream);
        
        checkServer(server, sThread);
    }

    // testing the correctness of wait on the client side
    // trying out writeability & exception --- none should be detected

    public void testWait1() {
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
            return;
        } finally { 
            server.stopServer();
        }
        try {
            int outcome = stream.waitFor(Activity.EXCEPTION.or(Activity.WRITE),
                    8.0f);

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
        } catch(NotImplementedException e) {
            logger.warn("WARNING: waitFor gave NotImplemented", e);
        } catch (Throwable e) {
            logger.error("Got exception", e);
            fail("Got exception " + e);
        }
        
        close(stream);
        checkServer(server, sThread);
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
            logger.error("Got exception", e);
            fail("Got exception " + e);
        } finally {
            server.stopServer();
        }
        try {
            Buffer buffer = BufferFactory.createBuffer();
            byte big[] = new byte[1000];
            buffer.setData(big);

            stream.write(buffer);
            stream.write(buffer);
        } catch (Throwable e) {
            logger.error("FAIL: got exception", e);
            fail("got exception " + e);
        }

        close(stream);
        checkServer(server, sThread);
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
        } catch (TimeoutException t) {
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
        } catch (IncorrectStateException e) {
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
        } catch (NoSuccessException e) {
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
        } catch (IncorrectStateException e) {
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
        
        checkServer(server, sThread);       
      
        try {
            stream.write(buffer);
        } catch (IncorrectStateException e) {
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

        ServerThread server = new ServerThreadWait(SERVER_URL);
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
        } catch (IncorrectStateException e) {
            logger.debug("OK: Should detect incorrect state");
        } catch (Throwable e) {
            logger.error("FAIL: 2nd connect should throw IncorrectState", e);
            fail("2nd connect should throw IncorrectState, not " + e);
        }
        
        checkServer(server, sThread);    
        close(stream);
    }

    // close should throw IncorrectState when the stream is in New
    // or Error state

    public void test13() {

        Stream stream = createStream();

        try {
            stream.close();
            logger.error("FAIL: successful close on unconnected stream");
            fail("Successful close on unconnected stream");
        } catch(IncorrectStateException e) {
            logger.debug("OK, close on NEW stream threw IncorrectState");
        } catch (Throwable e) {
            logger.error("FAIL: Threw exception", e);
            fail("threw exception " + e);
        }

        stream = createStream();
        
        try {
            stream.connect(); // should fail
            logger.error("FAIL: Connect should have failed");
            fail("Connect should have failed");
            return;
        } catch (NoSuccessException e) {
            logger.debug("OK: Connect not succeeded -- should switch to Error state");
        } catch (Throwable e) {
            logger.error("FAIL: Threw other exception", e);
            fail("threw wrong exception" + e);
            return;
        }
        
        close(stream);
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
        } catch (BadParameterException e) {
            logger.debug("OK: BadParameter thrown");
        } catch (Throwable e) {
            logger.debug("FAIL: BadParameter should be thrown", e);
            fail("threw wrong exception " + e);
        }
        
        checkServer(server, sThread);    
        close(stream);
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
        
        checkServer(server, sThread);
        close(stream);
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
        checkServer(server, sThread);
        close(stream);
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
        checkServer(server, sThread);
        close(stream);
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
        checkServer(server, sThread);
        close(stream);
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
            int val = stream.waitFor(0, 5.0f);
            logger.debug("OK: Survived with 'incorrect' input. Result = "
                            + val);

        } catch (Throwable e) {
            logger.error("FAIL: waitFor gave exception", e);
            fail("waitFor gave exception " + e);
        }
        checkServer(server, sThread);
        close(stream);
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
        checkServer(server, sThread);
        close(stream);
        
        try {
            stream.waitFor(0, 5.0f);
            logger.error("FAIL: Should have thrown IncorrectState");
            fail("waitFor should have thrown IncorrectState");
        } catch (IncorrectStateException e) {
            logger.debug("OK: Threw IncorrectState");
        } catch (Throwable e) {
            logger.error("FAIL: Should have thrown IncorrectState", e);
            fail("waitFor should have thrown IncorrectState, not " + e);

        }
    }
}
