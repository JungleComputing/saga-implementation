package test.stream;

import org.apache.log4j.Logger;
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
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

import test.misc.AdaptorTestResult;
import test.misc.AdaptorTestResultEntry;

public class StreamAdaptorTest {

    private static final int SERVER_WAIT = 3000;

    private static Logger logger = Logger.getLogger(StreamAdaptorTest.class);

    private final URL serverUrl;

    public static void main(String[] args) {
        System.setProperty("Stream.adaptor.name", args[0]);
        System.setProperty("StreamService.adaptor.name", args[0]);
        StreamAdaptorTest a = new StreamAdaptorTest(args[1]);
        a.test(args[0]).print();
    }

    public StreamAdaptorTest(String url) {
        URL u = null;
        try {
            u = URLFactory.createURL(url);
        } catch(Throwable e) {
            logger.error("URL error", e);
            System.exit(1);
        }
        serverUrl = u;
    }

    public AdaptorTestResult test(String adaptor) {
        AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor, serverUrl.getString());

        adaptorTestResult.put("connect   [1]", testConnect1());
        adaptorTestResult.put("connect   [2]", testConnect2());
        adaptorTestResult.put("connect   [3]", testConnect3());
        
        adaptorTestResult.put("read      [1]", testRead1());
        adaptorTestResult.put("read      [2]", testRead2());
        adaptorTestResult.put("read      [3]", testRead3());   
                
        adaptorTestResult.put("write     [1]", testWrite1());
        adaptorTestResult.put("write     [2]", testWrite2());
        adaptorTestResult.put("write     [3]", testWrite3()); 
        adaptorTestResult.put("write     [4]", testWrite4()); 
        
        adaptorTestResult.put("close     [1]", testClose1());
        adaptorTestResult.put("close     [2]", testClose2());
        adaptorTestResult.put("close     [3]", testClose3());   
        
        adaptorTestResult.put("waitfor   [1]", testWaitFor1());
        adaptorTestResult.put("waitfor   [2]", testWaitFor2());
        adaptorTestResult.put("waitfor   [3]", testWaitFor3());   
        
        adaptorTestResult.put("getContext[1]", testGetContext1());
        adaptorTestResult.put("getContext[2]", testGetContext2());
        adaptorTestResult.put("getContext[3]", testGetContext3());
 
        return adaptorTestResult;
    }

    public AdaptorTestResultEntry testConnect1() {
        Stream stream;
        
        logger.debug("testConnect1 ...");
        // connect to server while it is down, should throw NoSuccess
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        long start = System.currentTimeMillis();
        try {
            stream.connect();
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Connect without server should throw NoSuccess"));
        } catch(NoSuccessException e) {
            // OK
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Connect without server should throw NoSucces, not "  + e, e));
        }

        // should now throw IncorrectState exception because stream should be
        // in error state
        try {
            stream.connect();
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Connect on stream in error state should throw IncorrectState"));
        } catch (IncorrectStateException e) {
            // OK
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Connect on stream in error state should throw IncorrectState, not "  + e, e));
        }

        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }
    
    private AdaptorTestResultEntry close(Stream stream, 
            AdaptorTestResultEntry e1) {
        AdaptorTestResultEntry e2 = null;
        try {
            stream.close();
        } catch(Throwable e) {
            e2 = new AdaptorTestResultEntry(false, 0,
                    new Exception("Close gave exception", e));
        }
        try {
            Thread.sleep(2000);
        } catch(Throwable e) {
            // ignored
        }
        return e1 == null ? e2 : e1;
    }
    
    private AdaptorTestResultEntry checkServer(ServerThread server,
            Thread sThread, AdaptorTestResultEntry e1) {

        AdaptorTestResultEntry e2 = null;
        
        try {
            sThread.join();
        } catch (InterruptedException e) {
            // ignored
        }
        
        Throwable ex = server.getException();
        if (ex != null) {
            e2 = new AdaptorTestResultEntry(false, 0,
                    new Exception("Reader gave exception", ex));
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignored
        }

        return e1 == null ? e2 : e1;
    }

    // connect & disconnect without writing anything
    // while the server is reading

    public AdaptorTestResultEntry testConnect2() {
        Stream stream;
        logger.debug("testConnect2 ...");
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        
        AdaptorTestResultEntry e1 = null;
        ServerThread server = new ServerThread(serverUrl);
        Thread sThread = new Thread(server);

        sThread.start();

        try {
            Thread.sleep(SERVER_WAIT);            
        } catch(Throwable e) {
            // ignored
        }

        long start = System.currentTimeMillis();

        if (e1 == null) {
            try {
                stream.connect();
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Connect gave exception", e));
            } finally {
                server.stopServer();
            }
        }

        long stop = System.currentTimeMillis();
        
        e1 = close(stream, e1);
        
        e1 = checkServer(server, sThread, e1);
        if (e1 != null) {
            return e1;
        }

        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // read 1 msg then check if the connection dropped condition is detected...

    public AdaptorTestResultEntry testRead1() {
        Stream stream;
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        
        ServerThread server = new ServerThreadWriting(serverUrl);
        Thread sThread = new Thread(server);
        AdaptorTestResultEntry e1 = null;
        sThread.start();

        try {
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        long start = System.currentTimeMillis();
        if (e1 == null) {
            try {
                stream.connect();
            } catch(Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Connect failed: " + e, e));
            } finally {
                server.stopServer();
            }
        }

        if (e1 == null) {
            try {
                Buffer buffer = BufferFactory.createBuffer();
                buffer.setData(new byte[1024]);

                logger.debug("Attempting to read the message");
                int bytesCnt = stream.read(buffer, buffer.getSize());
                logger.debug("Read " + bytesCnt + " bytes");
                logger.debug("Message content:");
                logger.debug(new String(buffer.getData()).trim());
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Read failed: " + e, e));
            }
        }

        if (e1 == null) {
            try {
                Buffer buffer = BufferFactory.createBuffer();
                buffer.setData(new byte[1024]);
                logger.debug("Attempting to read a second message");
                int bytesCnt = stream.read(buffer, buffer.getSize());
                if (bytesCnt != 0) {
                    e1 = new AdaptorTestResultEntry(false, 0,
                            new Exception("return value of read != 0 at EOF"));
                }
            } catch(Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Read[2] failed: " + e, e));
            }
        }

        long stop = System.currentTimeMillis();

        e1 = close(stream, e1);

        e1 = checkServer(server, sThread, e1);

        if (e1 != null) {
            return e1;
        }

        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // testing the correctness of wait on the client side
    // trying out writeability & exception --- none should be detected

    public AdaptorTestResultEntry testWaitFor1() {
        Stream stream;
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        ServerThread server = new ServerThreadWriting(serverUrl);
        Thread sThread = new Thread(server);
        AdaptorTestResultEntry e1 = null;
        
        sThread.start();

        
        if (e1 == null) {
            try {
                stream.getMetric(Stream.STREAM_READ)
                        .addCallback(readable);
            } catch(Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("addCallback failed", e));
            }
        }

        try {
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        if (e1 == null) {
            try {
                stream.connect();
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Connect failed", e));
            } finally { 
                server.stopServer();
            }
        }

        long start = System.currentTimeMillis();

        if (e1 == null) {
            try {
                int outcome = stream.waitFor(Activity.EXCEPTION.or(Activity.WRITE),
                        8.0f);

                if (outcome == 0) {
                    // OK
                    logger.debug("Client: nothing detected [WAIT]");
                } else if ((outcome & Activity.EXCEPTION.getValue()) != 0) {
                    e1 = new AdaptorTestResultEntry(false, 0,
                            new Exception("Client: exceptional condition detected"));
                } else if ((outcome & Activity.READ.getValue()) != 0) {
                    e1 = new AdaptorTestResultEntry(false, 0,
                            new Exception("Client: readable detected"));
                } else if ((outcome & Activity.WRITE.getValue()) != 0) {
                    e1 = new AdaptorTestResultEntry(false, 0,
                            new Exception("Client: writable detected"));
                } else {
                    e1 = new AdaptorTestResultEntry(false, 0,
                            new Exception("Client: outcome = outcome"));
                }
            } catch(NotImplementedException e) {
                logger.info("waitFor gave NotImplemented", e);
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0, e);
            }
        }

        long stop = System.currentTimeMillis();
        
        e1 = close(stream, e1);
        e1 = checkServer(server, sThread, e1);

        if (e1  != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // we will try to write more than the reading buffer can store
    // and see what happens

    // currently the buffer is 1024 bytes long

    public AdaptorTestResultEntry testWrite2() {
        Stream stream;
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        
        ServerThread server = new ServerThreadReading(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();
        AdaptorTestResultEntry e1 = null;

        try {
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        if (e1 == null) {
            try {
                stream.connect();
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Could not connect", e));
            } finally {
                server.stopServer();
            }
        }

        long start = System.currentTimeMillis();

        if (e1 == null) {
            try {
                Buffer buffer = BufferFactory.createBuffer();
                byte big[] = new byte[1000];
                buffer.setData(big);

                stream.write(buffer);
                stream.write(buffer);
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Write failed", e));
            }
        }

        long stop = System.currentTimeMillis();

        e1 = close(stream, e1);
        e1 = checkServer(server, sThread, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // tries to close not opened stream service

    public AdaptorTestResultEntry testClose2() {
        long start = System.currentTimeMillis();
        try {
            StreamService service = StreamFactory.createStreamService(
                    serverUrl);
            service.close();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // multiple calls to close should not raise an exception
    // + test for throwing Timeout exception in serve method

    public AdaptorTestResultEntry testClose3() {
        StreamService service = null;
        try {
            service = StreamFactory.createStreamService(serverUrl);
            service.serve(1.0f);
            logger.debug("TEST");
        } catch (TimeoutException t) {
            // OK
            logger.debug("OK: got timeout exception");
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }

        long start = System.currentTimeMillis();
        try {
            service.close();
            service.close();
            logger.debug("OK: multiple close invocations allowed");
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // getContext() method test

    public AdaptorTestResultEntry testGetContext1() {

        Stream stream;
        
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }

        long start = System.currentTimeMillis();

        // the stream should be or have been in Open state before
        try {
            stream.getContext();
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("getContext() should have thrown IncorrectState exception"));
        } catch (IncorrectStateException e) {
            // OK
            logger.debug("OK: Should have thrown IncorrectState exception");
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("getContext() should have thrown IncorrectState exception, not " + e, e));
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // connect() method test getContext().
    public AdaptorTestResultEntry testGetContext2() {

        Stream stream;

        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }

        long start = System.currentTimeMillis();
        try {
            stream.getContext();    // should not give context because the
                                    // stream wasn't opened
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Should have thrown IncorrectState exception"));
        } catch (IncorrectStateException e) {
            logger.debug("OK: Should have thrown IncorrectState exception");
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Should have thrown IncorrectState exception and not " + e, e));
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // getContext() test -- we go from Open to some final state and expect
    // this method to finish successfully

    public AdaptorTestResultEntry testGetContext3() {

        Stream stream;

        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }


        ServerThread server = new ServerThreadOneRead(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();

        Buffer buffer = null;

        try {
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }

        AdaptorTestResultEntry e1 = null;

        long start = System.currentTimeMillis();
        
        try {
            buffer = BufferFactory.createBuffer();
            buffer.setData("Hello World".getBytes());

            stream.write(buffer);
        } catch(Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, e);
        }
        
        e1 = checkServer(server, sThread, e1);       
      
        if (e1 == null) {
            try {
                stream.write(buffer);
                e1 = new AdaptorTestResultEntry(false, 0, new Exception("Should have thrown IncorrectState -- the server is down"));
            } catch (IncorrectStateException e) {
                logger.debug("OK: Should have thrown IncorrectState -- the server is down");
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0, new Exception("Should have thrown IncorrectState -- the server is down -- and not " + e, e));
            }
        }

        // we should be now in DROPPED state

        if (e1 == null) {
            try {
                stream.getContext();
                logger.debug("OK: we have received security context");
            } catch (Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0, e);
            }
        }
        if (e1 != null) {
            return e1;
        }

        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // if we successfully connect to server we should not be able to do it
    // for the second time

    public AdaptorTestResultEntry testConnect3() {

        Stream stream;
        logger.debug("testConnect3 ...");
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        

        ServerThread server = new ServerThreadWait(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();

        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }

        long start = System.currentTimeMillis();
        AdaptorTestResultEntry e1 = null;

        try {
            stream.connect();
            e1 = new AdaptorTestResultEntry(false, 0, new Exception("2nd connect should throw IncorrectState"));
        } catch (IncorrectStateException e) {
            logger.debug("OK: Should detect incorrect state");
        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, new Exception("2nd connect should throw IncorrectState and not " + e, e));
        }
        
        long stop = System.currentTimeMillis();

        e1 = checkServer(server, sThread, e1);    
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // close should throw IncorrectState when the stream is in New
    // or Error state

    public AdaptorTestResultEntry testClose1() {

        Stream stream;

        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }

        long start = System.currentTimeMillis();

        AdaptorTestResultEntry e1 = null;

        try {
            stream.close();
            e1 = new AdaptorTestResultEntry(false, 0,
                    new Exception("close on unconnected stream should give IncorrectState"));
        } catch(IncorrectStateException e) {
            logger.debug("OK, close on NEW stream threw IncorrectState");
        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0,
                    new Exception("close on unconnected stream should give IncorrectState"));
        }

        if (e1 == null) {
            try {
                stream = StreamFactory.createStream(serverUrl);
            } catch(Throwable e) {
                e1 = new AdaptorTestResultEntry(false, 0,
                        new Exception("Could not create stream", e));
            }
        }
        
        if (e1 == null) {
            try {
                stream.connect(); // should fail
                return new AdaptorTestResultEntry(false, 0,
                        new Exception("connect should fail"));
            } catch (NoSuccessException e) {
                logger.debug("OK: Connect not succeeded -- should switch to Error state");
            } catch (Throwable e) {
                return new AdaptorTestResultEntry(false, 0,
                        new Exception("connect should fail with NoSuccess, not " + e, e));
            }
        }
        
        long stop = System.currentTimeMillis();
        
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // read with negative length

    public AdaptorTestResultEntry testRead2() {
        
        Stream stream;

        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }

        ServerThread server = new ServerThreadWriting(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();
        
        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }

        AdaptorTestResultEntry e1 = null;
        long start = System.currentTimeMillis();

        try {
            Buffer b = BufferFactory.createBuffer();

            int nBytes = stream.read(b, -223);

            e1 = new AdaptorTestResultEntry(false, 0, new Exception("Should throw BadParameter"));

        } catch (BadParameterException e) {
            logger.debug("OK: BadParameter thrown");
        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, e);
        }

        long stop = System.currentTimeMillis();

        e1 = checkServer(server, sThread, e1);
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // read with length == 0

    public AdaptorTestResultEntry testRead3() {

        Stream stream;
        
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        
        ServerThread server = new ServerThreadWriting(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();

        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }

        AdaptorTestResultEntry e1 = null;
        long start = System.currentTimeMillis();
        
        try {
            Buffer b = BufferFactory.createBuffer();

            int nBytes = stream.read(b, 0);

            logger.debug("OK: Successful; " + nBytes + " bytes read");

            nBytes = stream.read(b, 50);

            logger.debug("OK: Successful; " + nBytes + " bytes read");

        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, e);
        }

        long stop = System.currentTimeMillis();

        e1 = checkServer(server, sThread, e1);
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // write with negative length

    public AdaptorTestResultEntry testWrite1() {
        Stream stream;
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        
        ServerThread server = new ServerThreadOneRead(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();

        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }

        AdaptorTestResultEntry e1 = null;
        long start = System.currentTimeMillis();
        
        try {
            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData("Hello world".getBytes());

            stream.write(buffer, -323);
            logger.debug("OK: Implementation figured out how to handle negative value of length");
        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, e);
        }

        long stop = System.currentTimeMillis();

        e1 = checkServer(server, sThread, e1);
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // write with 0 length

    public AdaptorTestResultEntry testWrite3() {
        Stream stream;
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }
        
        ServerThread server = new ServerThreadOneRead(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();

        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }

        long start = System.currentTimeMillis();

        AdaptorTestResultEntry e1 = null;

        try {
            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData("Hello world".getBytes());

            int nBytes = stream.write(buffer, 0);
            logger.debug("OK: Written " + nBytes + " bytes");
            nBytes = stream.write(buffer, buffer.getSize());
            logger.debug("OK: Written " + nBytes + " bytes");

        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();

        e1 = checkServer(server, sThread, e1);
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // write -- passing uninitialized buffer

    public AdaptorTestResultEntry testWrite4() {

        Stream stream;
        
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }

        ServerThread server = new ServerThreadOneRead(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();

        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }
        
        long start = System.currentTimeMillis();

        AdaptorTestResultEntry e1 = null;

        try {
            Buffer buffer = BufferFactory.createBuffer(1000);

            stream.write(buffer, -323);
            logger.debug("OK: Implementation figured out how to handle negative value of length");
        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, e);
        }

        long stop = System.currentTimeMillis();

        e1 = checkServer(server, sThread, e1);
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // calling wait with zeroed "what" flag

    public AdaptorTestResultEntry testWaitFor2() {
        Stream stream;
        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }

        ServerThread server = new ServerThreadWriting(serverUrl);
        Thread sThread = new Thread(server);
        sThread.start();

        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }
        
        long start = System.currentTimeMillis();
        AdaptorTestResultEntry e1 = null;
        try {
            int val = stream.waitFor(0, 5.0f);
            logger.debug("OK: Survived with 'incorrect' input. Result = "
                            + val);

        } catch (Throwable e) {
            e1 = new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        e1 = checkServer(server, sThread, e1);
        e1 = close(stream, e1);
        if (e1 != null) {
            return e1;
        }
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    // calling wait in state other than open
    public AdaptorTestResultEntry testWaitFor3() {
        ServerThread server = new ServerThreadWriting(serverUrl);
        Thread sThread = new Thread(server);
        Stream stream;

        sThread.start();

        try {
            stream = StreamFactory.createStream(serverUrl);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not create stream", e));
        }

        try {           
            Thread.sleep(SERVER_WAIT);
        } catch(Throwable e) {
            // ignored
        }

        try {
            stream.connect(); // should be successful
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Could not connect", e));
        } finally {
            server.stopServer();
        }

        AdaptorTestResultEntry e = checkServer(server, sThread, null);
        e = close(stream, e);

        if (e != null) {
            return e;
        }
        
        long start = System.currentTimeMillis();

        try {
            stream.waitFor(0, 5.0f);
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("waitFor should have thrown IncorrectState"));
        } catch (IncorrectStateException ex) {
            logger.debug("OK: Threw IncorrectState");
        } catch (Throwable ex) {
            return new AdaptorTestResultEntry(false, 0, ex);
        }

        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private static Callback readable = new Callback() {
        public boolean cb(Monitorable mt, Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
            logger.debug("Stream Client: Stream is readable [METRIC]");
            return true;
        }
    };
}

