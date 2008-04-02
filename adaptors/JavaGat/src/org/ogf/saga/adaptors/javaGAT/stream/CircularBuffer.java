package org.ogf.saga.adaptors.javaGAT.stream;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.io.Pipe;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.buffer.Buffer;

class CircularBuffer extends java.io.InputStream {
    
    private static final Logger logger = Logger.getLogger(CircularBuffer.class);

    private static final int DEFAULT_CAPACITY = 4096;

    public static int REASON_DROPPED = 1;
    public static int REASON_ERROR = 2;
    public static int REASON_CLOSED = 3;

    private byte[] buf;
    private int beg, end;
    private boolean empty;
    private final Pipe pipe;

    private int terminationReason = 0;

    CircularBuffer(Pipe pipe) {
        this(pipe, DEFAULT_CAPACITY);
    }

    CircularBuffer(Pipe pipe, int capacity) {
        buf = new byte[capacity];
        this.pipe = pipe;
        beg = 0;
        end = 0;
        empty = true;
    }

    public synchronized int getSize() {
        if (empty)
            return 0;
        if (beg < end)
            return end - beg;
        else
            return buf.length - beg + end;
    }

    public synchronized int read(org.ogf.saga.buffer.Buffer b, int len)
            throws IncorrectStateException, NoSuccessException, BadParameterException,
            SagaIOException, NotImplementedException {
        if (! (b instanceof Buffer)) {
            throw new BadParameterException("Wrong buffer type");
        }
        Buffer buffer = (Buffer) b;
        
        byte[] res;
        try {
            res = buffer.getData();
        } catch(DoesNotExistException e) {
            if (len < 0) {
                throw new BadParameterException("read: len < 0 and buffer not allocated yet");
            }
            buffer.setSize(len);
            try {
                res = buffer.getData();
            } catch(DoesNotExistException e2) {
                // This should not happen after setSize() with size >= 0.
                throw new SagaRuntimeException("Internal error", e2);
            }
        }
        
        if (len < 0) {
            len = buffer.getSize();
        } else if (len > buffer.getSize()) {
            if (buffer.isImplementationManaged()) {
                buffer.setSize(len);
                try {
                    res = buffer.getData();
                } catch (DoesNotExistException e) {
                    throw new SagaRuntimeException("Internal error", e);
                }
            } else {
                throw new BadParameterException("buffer too small");
            }
        }

        try {
            int retval = read(res, 0, len);
            return (retval < 0) ? 0 : retval;
        } catch (IOException e) {
            throw new SagaIOException(e);
        }
    }
    
    public synchronized int read(byte[] res, int off, int len) throws IOException {
        int size = getSize();
        
        if (res == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > res.length) || (len < 0) ||
                   ((off + len) > res.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        while (terminationReason == 0 && size == 0) {
            try {
                wait();
                size = getSize();
            } catch (InterruptedException e) {
            }
        }        

        if (size == 0) {
            if (terminationReason == REASON_ERROR)
                throw new IOException("Read");
            else if (terminationReason == REASON_DROPPED) {
                return -1;
            } else if (terminationReason == REASON_CLOSED) {
                return -1;
            }
        }

        if (size < len)
            len = size;
        
        if (logger.isDebugEnabled()) {
            logger.debug("Reading " + len + " bytes, size = " + size);
        }
        
        int index = beg;
        int cnt = buf.length - beg;
        if (cnt > len) {
            cnt = len;
        }
        
        System.arraycopy(buf, index, res, off, cnt);
        index += cnt;
        if (cnt < len) {
            index = len - cnt;
            System.arraycopy(buf, 0, res, cnt+off, index);
        }
        
        beg = index;

        if (size == len)
            empty = true;
        notifyAll();
        return len;
    }

    // if error occurs we have to wake up all the (reading) threads
    // and let them finish job

    public synchronized void onError(int terminationReason) {
        this.terminationReason = terminationReason;
        notifyAll();
    }
    
    public void close() {
        onError(REASON_CLOSED);
    }

    public int readFromStream() throws IOException,
            InterruptedException {
        int len;
        InputStream inputStream;
        try {
            inputStream = pipe.getInputStream();
        } catch (GATInvocationException e) {
            IOException ex = new IOException("GatInvocationException");
            ex.initCause(e);
            throw ex;
        }
        synchronized(this) {
            // Wait until space becomes available.
            len = buf.length - getSize();
            while (len <= 0) {
                wait();
                len = buf.length - getSize();
            }
        }
        int retval;
        if (end == buf.length) {
            retval = inputStream.read(buf, 0, len);
        } else {
            if (buf.length - end < len) {
                len = buf.length - end;
            }
            retval = inputStream.read(buf, end, len);
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("readFromStream: read " + retval + " bytes");
        }
        
        synchronized(this) {
            if (retval > 0) {
                if (end == buf.length) {
                    end = retval;
                } else {
                    end += retval;
                }
                empty = false;
                notifyAll();
            }
        }
        return retval;
    }

    public int read() throws IOException {
        byte[] b = new byte[1];
        int len = read(b,0,1);
        if (len < 0) {
            return -1;
        }
        return b[0] & 0377;
    }
    
    public int available() {
        return getSize();
    }
}
