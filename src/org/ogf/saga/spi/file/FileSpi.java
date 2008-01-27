package org.ogf.saga.spi.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.file.IOVec;
import org.ogf.saga.file.SeekMode;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.Falls;
import org.ogf.saga.proxies.file.FileWrapper;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.spi.namespace.NSEntrySpi;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class FileSpi extends NSEntrySpi implements FileSpiInterface {

    protected int fileFlags;
    protected final HashMap<String, Falls> fallsCache = new HashMap<String, Falls>();

    public FileSpi(FileWrapper wrapper, Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, BadParameter, DoesNotExist,
            PermissionDenied, AuthorizationFailed, AuthenticationFailed,
            Timeout, NoSuccess, AlreadyExists {
        super(wrapper, session, name, flags
                & Flags.ALLNAMESPACEFLAGS.getValue());
        fileFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
        if ((fileFlags | Flags.ALLFILEFLAGS.getValue()) != Flags.ALLFILEFLAGS
                .getValue()) {
            throw new BadParameter("Illegal flags for File constructor: "
                    + flags);
        }
    }

    protected void checkBufferType(Buffer buffer) {
        if (!(buffer instanceof org.ogf.saga.impl.buffer.Buffer)) {
            throw new SagaError("Wrong buffer type: "
                    + buffer.getClass().getName());
        }
    }

    protected void checkIOVecsType(IOVec[] iovecs) {
        for (IOVec iovec : iovecs) {
            if (!(iovec instanceof org.ogf.saga.proxies.file.IOVec)) {
                throw new SagaError("Wrong iovec type: "
                        + iovec.getClass().getName());
            }
        }
    }

    public void readV(IOVec[] arg0) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        checkIOVecsType(arg0);
        for (IOVec iov : arg0) {
            org.ogf.saga.proxies.file.IOVec iovec = (org.ogf.saga.proxies.file.IOVec) iov;
            int lenIn = iovec.getLenIn();
            int offset = iovec.getOffset();
            iovec.setLenOut(read(iovec, offset, lenIn));
        }
    }

    public void writeV(IOVec[] arg0) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        checkIOVecsType(arg0);
        for (IOVec iov : arg0) {
            org.ogf.saga.proxies.file.IOVec iovec = (org.ogf.saga.proxies.file.IOVec) iov;
            int lenIn = iovec.getLenIn();
            int offset = iovec.getOffset();
            iovec.setLenOut(write(iovec, offset, lenIn));
        }
    }

    /**
     * This method splits a FALLS pattern read up into read() and seek() calls.
     * Needless to say, this is very slow and should only be used if nothing better
     * is available.
     * @param falls the FALLS pattern.
     * @param buf the buffer to store into.
     * @param bufOffset the buffer offset to use. 
     * @return the number of bytes read.
     */
    private int readByFallsPattern(Falls falls, Buffer buf, int bufOffset)
            throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess, IOException {
        int rep = falls.getRep();
        int from = falls.getFrom();
        int to = falls.getTo();
        int stride = falls.getStride();
        Falls nested = falls.getNested();
        long current = seek(from, SeekMode.CURRENT);

        for (int i = 0; i < rep; i++) {
            if (nested != null) {
                bufOffset += readByFallsPattern(nested, buf, bufOffset);
            } else {
                bufOffset += read(buf, bufOffset, (to - from + 1));
            }
            current += stride;
            seek(current, SeekMode.START);
        }
        return bufOffset;
    }
    
    public int readP(String fallsPattern, Buffer buf) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IOException {

        Falls falls = fallsCache.get(fallsPattern);
        if (falls == null) {
            falls = new Falls(fallsPattern);
            fallsCache.put(fallsPattern, falls);
        }
        
        int size = falls.getSize();

        // Make sure the buffer is large enough.
        try {
            buf.getData();
        } catch(DoesNotExist e) {
            if (buf.getSize() < 0) {
                buf.setSize(size);
            }
        }
        if (buf.getSize() < size) {
            // It was'nt.
            throw new BadParameter("buffer too small for the specified pattern");
        }
        
        return readByFallsPattern(falls, buf, 0);
    }

    public int sizeP(String fallsPattern) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, IncorrectState, PermissionDenied,
            BadParameter, Timeout, NoSuccess {
        Falls falls = fallsCache.get(fallsPattern);
        if (falls == null) {
            falls = new Falls(fallsPattern);
            fallsCache.put(fallsPattern, falls);
        }
        return falls.getSize();
    }
    /**
     * This method splits a FALLS pattern write up into write() and seek() calls.
     * Needless to say, this is very slow and should only be used if nothing better
     * is available.
     * @param falls the FALLS pattern.
     * @param buf the buffer to write from.
     * @param bufOffset the buffer offset to use. 
     * @return the number of bytes written.
     */
    private int writeByFallsPattern(Falls falls, Buffer buf, int bufOffset)
            throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, Timeout, NoSuccess, IOException {
        int rep = falls.getRep();
        int from = falls.getFrom();
        int to = falls.getTo();
        int stride = falls.getStride();
        Falls nested = falls.getNested();
        long current = seek(from, SeekMode.CURRENT);

        for (int i = 0; i < rep; i++) {
            if (nested != null) {
                bufOffset += writeByFallsPattern(nested, buf, bufOffset);
            } else {
                bufOffset += write(buf, bufOffset, (to - from + 1));
            }
            current += stride;
            seek(current, SeekMode.START);
        }
        return bufOffset;
    }
    
    public int writeP(String fallsPattern, Buffer buf) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        Falls falls = fallsCache.get(fallsPattern);
        if (falls == null) {
            falls = new Falls(fallsPattern);
            fallsCache.put(fallsPattern, falls);
        }
        
        int size = falls.getSize();

        // Make sure the buffer is large enough.
        try {
            buf.getData();
        } catch(DoesNotExist e) {
            throw new BadParameter("Buffer has no data");
        }
        if (buf.getSize() < size) {
            // It was'nt.
            throw new BadParameter("buffer too small for the specified pattern");
        }
        
        return writeByFallsPattern(falls, buf, 0);
    }

    public Task<Long> getSize(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Long>(wrapper, session, mode,
                "getSize", new Class[] {});
    }

    public Task<Integer> read(TaskMode mode, Buffer buffer, int offset, int len)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "read", new Class[] { Buffer.class, Integer.TYPE, Integer.TYPE }, buffer, len);
    }

    public Task<Integer> write(TaskMode mode, Buffer buffer, int offset, int len)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "write", new Class[] { Buffer.class, Integer.TYPE, Integer.TYPE }, buffer,
                len);
    }

    public Task<Long> seek(TaskMode mode, long arg1, SeekMode arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Long>(wrapper, session, mode,
                "seek", new Class[] { Long.TYPE, SeekMode.class }, arg1, arg2);
    }

    public Task<List<String>> modesE(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<String>>(wrapper, session,
                mode, "modesE", new Class[] {});
    }

    public Task<Integer> sizeE(TaskMode mode, String arg1, String arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "sizeE", new Class[] { String.class, String.class }, arg1, arg2);
    }

    public Task<Integer> readE(TaskMode mode, String arg1, String arg2,
            Buffer arg3) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "readE",
                new Class[] { String.class, String.class, Buffer.class }, arg1,
                arg2, arg3);
    }

    public Task<Integer> writeE(TaskMode mode, String arg1, String arg2,
            Buffer arg3) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "writeE", new Class[] { String.class, String.class,
                        Buffer.class }, arg1, arg2, arg3);
    }

    public Task<Integer> sizeP(TaskMode mode, String arg1)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "sizeP", new Class[] { String.class }, arg1);
    }

    public Task<Integer> readP(TaskMode mode, String arg1, Buffer arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "readP", new Class[] { String.class, Buffer.class }, arg1, arg2);
    }

    public Task<Integer> writeP(TaskMode mode, String arg1, Buffer arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session, mode,
                "writeP", new Class[] { String.class, Buffer.class }, arg1,
                arg2);
    }

    public Task readV(TaskMode mode, IOVec[] arg1) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode, "readV",
                new Class[] { IOVec[].class }, (Object) arg1);
    }

    public Task writeV(TaskMode mode, IOVec[] arg1) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "writeV", new Class[] { IOVec[].class }, (Object) arg1);
    }
}
