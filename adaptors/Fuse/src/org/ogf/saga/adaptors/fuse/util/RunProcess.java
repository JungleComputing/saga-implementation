package org.ogf.saga.adaptors.fuse.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modification of ibis.util.RunProcess. This version does not wait until the
 * output and error stream end, since this blocks the execution of the
 * xtfs_mount command. Instead, it captures the streams until the process exits,
 * and discards all remaining output.
 * 
 * @author mathijs
 */
public class RunProcess {

    private static final int INIT_BUF_SIZE = 4096; // bytes
    private static final int SKIP_BYTES = 4096; // bytes

    private static Logger logger = LoggerFactory.getLogger(RunProcess.class);

    private static ExecutorService executor = null;

    private final ProcessBuilder builder;
    private int exitStatus;
    private Process proc;
    private OutputBuffer procOut;
    private OutputBuffer procErr;
    private String input = null;

    /**
     * Creates a RunProcess object for the specified command.
     * 
     * @param command
     *            the specified command and arguments.
     */
    public RunProcess(String... command) {
        builder = new ProcessBuilder(command);
        init();
    }

    /**
     * Creates a RunProcess object for the specified command.
     * 
     * @param command
     *            the specified command and arguments.
     */
    public RunProcess(List<String> command) {
        builder = new ProcessBuilder(command);
        init();
    }

    private void init() {
        synchronized (getClass()) {
            if (executor == null) {
                ThreadFactory f = new OutputReaderThreadFactory();
                executor = Executors.newCachedThreadPool(f);
            }
        }

        exitStatus = -1;
        procOut = null;
        procErr = null;
    }

    public void setStdin(String input) {
    	this.input = input;
    }
    
    public void run() {
        try {
            proc = builder.start();
        } catch (Exception e) {
            String msg = "Could not execute command '" + builder.command()
                    + "': " + e;
            if (getRedirectErrorStream()) {
            	procOut = new OutputBuffer(msg);
            } else {
            	procErr = new OutputBuffer(msg);
            }
            return;
        }

        procOut = new OutputBuffer();
        procErr = new OutputBuffer();

        OutputReader outReader = new OutputReader(procOut, 
        		proc.getInputStream());
        OutputReader errReader = null;
        if (!builder.redirectErrorStream()) {
            errReader = new OutputReader(procErr, proc.getErrorStream());
        }

        Thread outThread = new Thread(outReader, "OutputReader "
                + builder.command());
        outThread.setDaemon(true);
        outThread.start();

        if (errReader != null) {
            Thread errThread = new Thread(errReader, "ErrorReader "
                    + builder.command());
            errThread.setDaemon(true);
            errThread.start();
        }
        
        if (input != null) {
        	PrintStream out = new PrintStream(proc.getOutputStream());
        	out.println(input);
        	out.close();
        }
        
        boolean interrupted = false;
        do {
            try {
                interrupted = false;
                exitStatus = proc.waitFor();
            } catch (InterruptedException e) {
                interrupted = true;
            }
        } while (interrupted);

        outReader.discardRemaining();
        if (errReader != null) {
            errReader.discardRemaining();
        }

        close(proc.getOutputStream());
        proc.destroy();
    }

    private static void close(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            logger.debug("Error when closing stream", e);
        }
    }

    /**
     * Returns the output buffer of the process.
     * 
     * @return the output buffer.
     */
    public byte[] getStdout() {
        if (procOut == null) {
            return new byte[0];
        }
        byte b[] = new byte[procOut.getSize()];
        System.arraycopy(procOut.getBytes(false), 0, b, 0, procOut.getSize());
        return b;
    }

    /**
     * Returns the error output buffer of the process.
     * 
     * @return the error output buffer.
     */
    public byte[] getStderr() {
        if (procErr == null) {
            return new byte[0];
        }
        byte b[] = new byte[procErr.size];
        System.arraycopy(procErr.getBytes(false), 0, b, 0, procErr.getSize());
        return b;
    }

    /**
     * Returns the exit status of the process.
     * 
     * @return the exit status.
     */
    public int getExitStatus() {
        return exitStatus;
    }

    /**
     * @see ProcessBuilder#command()
     */
    public List<String> command() {
        return builder.command();
    }

    /**
     * @see ProcessBuilder#command(List)
     */
    public RunProcess command(List<String> command) {
        builder.command(command);
        return this;
    }

    /**
     * @see ProcessBuilder#command(String...)
     */
    public RunProcess command(String... command) {
        builder.command(command);
        return this;
    }

    /**
     * @see ProcessBuilder#directory()
     */
    public File directory() {
        return builder.directory();
    }

    /**
     * @see ProcessBuilder#directory(File)
     */
    public ProcessBuilder directory(File directory) {
        return builder.directory(directory);
    }

    /**
     * @see ProcessBuilder#environment()
     */
    public Map<String, String> environment() {
        return builder.environment();
    }

    /**
     * @see ProcessBuilder#redirectErrorStream()
     */
    public boolean getRedirectErrorStream() {
        return builder.redirectErrorStream();
    }

    /**
     * @see ProcessBuilder#redirectErrorStream(boolean)
     */
    public void setRedirectErrorStream(boolean redirectErrorStream) {
        builder.redirectErrorStream(redirectErrorStream);
    }

    /**
     * @see ProcessBuilder#start()
     */
    public Process start() throws IOException {
        return builder.start();
    }

    private class OutputBuffer {

        private byte[] bytes;
        private int size;

        OutputBuffer() {
            bytes = new byte[INIT_BUF_SIZE];
            size = 0;
        }

        OutputBuffer(String s) {
            bytes = s.getBytes();
            size = bytes.length;
        }

        synchronized byte[] getBytes(boolean increase) {
            if (increase && bytes.length == size) {
                byte[] newBuf = new byte[2 * bytes.length];
                System.arraycopy(bytes, 0, newBuf, 0, bytes.length);
                bytes = newBuf;
            }
            return bytes;
        }

        synchronized int getSize() {
            return size;
        }

        synchronized void increase(long bytes) {
            this.size += bytes;
        }

    }

    /**
     * Separate thread to read an output stream of the command.
     */
    private class OutputReader implements Runnable {

        private OutputBuffer buf;
        private InputStream in;
        private volatile boolean discard;

        OutputReader(OutputBuffer buf, InputStream in) {
            this.buf = buf;
            this.in = in;
            discard = false;
        }

        synchronized void discardRemaining() {
            discard = true;
        }

        public void run() {
            boolean done = false;

            while (!done) {
                long readBytes = 0;

                try {
                    if (discard) {
                        readBytes = in.skip(SKIP_BYTES);
                    } else {
                        byte[] bytes = buf.getBytes(true);
                        int size = buf.getSize();
                        readBytes = in.read(bytes, size, bytes.length - size);
                    }
                } catch (IOException e) {
                    readBytes = -1;
                }

                synchronized (this) {
                    if (!discard && readBytes != -1) {
                        buf.increase(readBytes);
                    }
                }

                done = readBytes < 0;
            }

            close(in);
        }
    }

    private class OutputReaderThreadFactory implements ThreadFactory {

        private int counter;

        OutputReaderThreadFactory() {
            counter = 0;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("OutputReader-" + counter);

            counter++;

            return t;
        }

    }

}
