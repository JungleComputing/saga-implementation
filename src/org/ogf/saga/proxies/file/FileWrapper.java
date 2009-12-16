package org.ogf.saga.proxies.file;

import java.util.List;

import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.engine.SAGAEngine;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.file.File;
import org.ogf.saga.file.IOVec;
import org.ogf.saga.file.SeekMode;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.FileSPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public class FileWrapper extends NSEntryWrapper implements File {

    private FileSPI proxy;
    private int fileFlags;

    FileWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        super(session, name, false);
        
        fileFlags = includeImpliedFlags(flags);
        
        sanityCheck(flags);

        Object[] parameters = { this, session, name, fileFlags };
        try {
            proxy = (FileSPI) SAGAEngine.createAdaptorProxy(FileSPI.class,
                    new Class[] { FileWrapper.class,
                            org.ogf.saga.impl.session.SessionImpl.class,
                            URL.class, Integer.TYPE }, parameters);
            super.setProxy(proxy);
        } catch (org.ogf.saga.error.SagaException e) {
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
            }
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            if (e instanceof AuthenticationFailedException) {
                throw (AuthenticationFailedException) e;
            }
            if (e instanceof AuthorizationFailedException) {
                throw (AuthorizationFailedException) e;
            }
            if (e instanceof PermissionDeniedException) {
                throw (PermissionDeniedException) e;
            }
            if (e instanceof BadParameterException) {
                throw (BadParameterException) e;
            }
            if (e instanceof AlreadyExistsException) {
                throw (AlreadyExistsException) e;
            }
            if (e instanceof DoesNotExistException) {
                throw (DoesNotExistException) e;
            }
            if (e instanceof TimeoutException) {
                throw (TimeoutException) e;
            }
            if (e instanceof NoSuccessException) {
                throw (NoSuccessException) e;
            }
            throw new NoSuccessException("Constructor failed", e);
        }
    }


    private void sanityCheck(int flags) throws BadParameterException {

        // First, check for unrecognized flags.
        if ((fileFlags | Flags.ALLFILEFLAGS.getValue()) != Flags.ALLFILEFLAGS
                .getValue()) {
            throw new BadParameterException(
                    "Illegal flags for File constructor: " + flags);
        }

        // Sanity check 1: append and truncate?
        if (Flags.APPEND.isSet(fileFlags) && Flags.TRUNCATE.isSet(fileFlags)) {
            throw new BadParameterException("TRUNCATE and APPEND?");
        }
        // Other sanity checks deleted: no longer applicable with new Saga specs.
    }

    public long getSize() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        checkNotClosed();
        return proxy.getSize();
    }

    public Task<File, Long> getSize(TaskMode mode)
            throws NotImplementedException {
        return proxy.getSize(mode);
    }

    public Object clone() throws CloneNotSupportedException {
        FileWrapper clone = (FileWrapper) super.clone();
        clone.proxy = (FileSPI) SAGAEngine.createAdaptorCopy(FileSPI.class,
                proxy, clone);
        clone.setProxy(proxy);
        return clone;
    }

    public List<String> modesE() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        checkNotClosed();
        return proxy.modesE();
    }

    public Task<File, List<String>> modesE(TaskMode mode)
            throws NotImplementedException {
        return proxy.modesE(mode);
    }

    public int read(Buffer buffer) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return read(buffer, 0, buffer.getSize());
    }

    public int read(Buffer buffer, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return read(buffer, 0, len);
    }

    public int read(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        checkNotClosed();
        if (!Flags.READ.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to read",
                    this);
        }

        return proxy.read(buffer, offset, len);
    }

    public Task<File, Integer> read(TaskMode mode, Buffer buffer)
            throws NotImplementedException {
        return read(mode, buffer, -1);
    }

    public Task<File, Integer> read(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return read(mode, buffer, 0, len);
    }

    public Task<File, Integer> read(TaskMode mode, Buffer buffer, int offset,
            int len) throws NotImplementedException {
        return proxy.read(mode, buffer, offset, len);
    }

    public int readE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        checkNotClosed();
        if (!Flags.READ.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to read",
                    this);
        }
        return proxy.readE(emode, spec, buffer);
    }

    public Task<File, Integer> readE(TaskMode mode, String emode, String spec,
            Buffer buffer) throws NotImplementedException {
        return proxy.readE(mode, emode, spec, buffer);
    }

    public int readP(String pattern, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        checkNotClosed();
        if (!Flags.READ.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to read",
                    this);
        }
        return proxy.readP(pattern, buffer);
    }

    public Task<File, Integer> readP(TaskMode mode, String pattern,
            Buffer buffer) throws NotImplementedException {
        return proxy.readP(mode, pattern, buffer);
    }

    public void readV(IOVec[] iovecs) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        checkNotClosed();
        if (!Flags.READ.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to read",
                    this);
        }
        proxy.readV(iovecs);
    }

    public Task<File, Void> readV(TaskMode mode, IOVec[] iovecs)
            throws NotImplementedException {
        return proxy.readV(mode, iovecs);
    }

    public long seek(long offset, SeekMode whence)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        checkNotClosed();
        if (!Flags.READ.isSet(fileFlags) && !Flags.WRITE.isSet(fileFlags)) {
            throw new IncorrectStateException(
                    "seek() called but not opened for READ or WRITE", this);
        }

        return proxy.seek(offset, whence);
    }

    public Task<File, Long> seek(TaskMode mode, long offset, SeekMode whence)
            throws NotImplementedException {
        return proxy.seek(mode, offset, whence);
    }

    public int sizeE(String emode, String spec) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        checkNotClosed();
        return proxy.sizeE(emode, spec);
    }

    public Task<File, Integer> sizeE(TaskMode mode, String emode, String spec)
            throws NotImplementedException {
        return proxy.sizeE(mode, emode, spec);
    }

    public int sizeP(String pattern) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        checkNotClosed();
        return proxy.sizeP(pattern);
    }

    public Task<File, Integer> sizeP(TaskMode mode, String pattern)
            throws NotImplementedException {
        return proxy.sizeP(mode, pattern);
    }

    public int write(Buffer buffer) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return write(buffer, 0, -1);
    }

    public int write(Buffer buffer, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        return write(buffer, 0, len);
    }

    public int write(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        checkNotClosed();
        if (!Flags.WRITE.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to write",
                    this);
        }
        return proxy.write(buffer, offset, len);
    }

    public Task<File, Integer> write(TaskMode mode, Buffer buffer)
            throws NotImplementedException {
        return write(mode, buffer, 0, -1);
    }

    public Task<File, Integer> write(TaskMode mode, Buffer buffer, int len)
            throws NotImplementedException {
        return write(mode, buffer, 0, len);
    }

    public Task<File, Integer> write(TaskMode mode, Buffer buffer, int offset,
            int len) throws NotImplementedException {
        return proxy.write(mode, buffer, offset, len);
    }

    public int writeE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        checkNotClosed();
        if (!Flags.WRITE.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to write",
                    this);
        }
        return proxy.writeE(emode, spec, buffer);
    }

    public Task<File, Integer> writeE(TaskMode mode, String emode, String spec,
            Buffer buffer) throws NotImplementedException {
        return proxy.writeE(mode, emode, spec, buffer);
    }

    public int writeP(String pattern, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        checkNotClosed();
        if (!Flags.WRITE.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to write",
                    this);
        }
        return proxy.writeP(pattern, buffer);
    }

    public Task<File, Integer> writeP(TaskMode mode, String pattern,
            Buffer buffer) throws NotImplementedException {
        return proxy.writeP(mode, pattern, buffer);
    }

    public void writeV(IOVec[] iovecs) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        checkNotClosed();
        if (!Flags.WRITE.isSet(fileFlags)) {
            throw new PermissionDeniedException("No permission to write",
                    this);
        }
        proxy.writeV(iovecs);
    }

    public Task<File, Void> writeV(TaskMode mode, IOVec[] iovecs)
            throws NotImplementedException {
        return proxy.writeV(mode, iovecs);
    }
}
