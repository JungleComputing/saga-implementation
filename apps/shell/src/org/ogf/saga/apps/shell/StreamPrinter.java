package org.ogf.saga.apps.shell;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamPrinter implements Runnable {

    private static final int BUF_SIZE = 1024;  // bytes;
    
    private Logger logger = LoggerFactory.getLogger(StreamPrinter.class);
    private InputStream stream;
    private byte[] buf;
    
    public StreamPrinter() {
        buf = new byte[BUF_SIZE];
        stream = null;
    }
    
    @Override
    public void run() {
        while (stream == null) {
            try {
                synchronized(this) {
                    wait();
                }
            } catch (InterruptedException e) {
                logger.debug("Interrupted", e);
            }
        }
        
        boolean done = false;
                
        while(!done) {
            try {
                int read = stream.read(buf);
                if (read < 0) {
                    done = true;
                } else {
                    String s = new String(buf, 0, read);
                    System.out.print(s);
                }
            } catch (IOException e) {
                done = true;
            }
        }
                
        try {
            stream.close();
        } catch (IOException e) {
            logger.debug("Error while closing stream", e);
        }
    }

    public synchronized void setStream(InputStream stream) {
        this.stream = stream;
        notifyAll();
    }
    
}
