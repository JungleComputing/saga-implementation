package org.ogf.saga.adaptors.javaGAT.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.io.RandomAccessFile;
import org.ogf.saga.adaptors.javaGAT.util.Initialize;
import org.ogf.saga.buffer.Buffer;
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
import org.ogf.saga.file.SeekMode;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.FileWrapper;
import org.ogf.saga.spi.file.FileAdaptorBase;
import org.ogf.saga.spi.file.FileSPI;
import org.ogf.saga.url.URL;

// Aaarggghh, javagat only has a local RandomAccessFile adaptor!!!
// So, this is no good, but there is nothing else ...
// input/output streams don't support seeks ...
// Partial solution: for input if the RandomAccessFile cannot be created,
// create a file input stream. Forward seeks can then be implemented with skip().
// Likewise, for output create a file output stream. No seeks in this case.

public class FileAdaptor extends FileAdaptorBase implements FileSPI {

    private static Logger logger = LoggerFactory.getLogger(FileAdaptor.class);

    static {
        Initialize.initialize();
    }

    private int flags;
    private long offset = 0L;
    private RandomAccessFile rf = null;
    private FileEntry entry;
    private InputStream in = null;
    private OutputStream out = null;

    public FileAdaptor(FileWrapper wrapper, SessionImpl sessionImpl, URL name,
            int flags) throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {

        super(wrapper, sessionImpl, name, flags);
        entry = new FileEntry(sessionImpl, name, flags
                & Flags.ALLNAMESPACEFLAGS.getValue());
        this.flags = flags;

        // Determine read/write
        String rfflags = null;
        if (Flags.READ.isSet(flags)) {
            rfflags = "r";
        }
        if (Flags.WRITE.isSet(flags)) {
            rfflags = "rw";
        }

        if (rfflags != null) {
            // Open the file, if needed.
            try {
                rf = GAT.createRandomAccessFile(entry.getGatContext(), entry
                        .getGatURI(), rfflags);
            } catch (GATObjectCreationException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("GAT.createRandomAccessFile failed");
                }
            }
        }

        if (rf != null) {
            // Truncate if needed.
            if (Flags.TRUNCATE.isSet(flags)) {
                try {
                    rf.setLength(0L);
                } catch (IOException e) {
                    throw new NoSuccessException("Truncate failed", e);
                }
            } else if (Flags.APPEND.isSet(flags)) {
                offset = entry.size();
                try {
                    rf.seek(offset);
                } catch (IOException e) {
                    throw new NoSuccessException("Append failed", e);
                }
            }
        } else {
            if (Flags.READ.isSet(flags)) {
                if (Flags.WRITE.isSet(flags)) {
                    throw new NoSuccessException("READWRITE not supported");
                }
                in = entry.getInputStream();
            } else if (Flags.WRITE.isSet(flags)) {
                boolean append = Flags.APPEND.isSet(flags);
                out = entry.getOutputStream(append);
                offset = entry.size();
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        FileAdaptor clone = (FileAdaptor) super.clone();
        clone.entry = (FileEntry) entry.clone();
        clone.entry.setWrapper(clone.wrapper);
        return clone;
    }

    public long getSize() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        if (isClosed()) {
            if (logger.isDebugEnabled()) {
                logger.debug("File already closed!");
            }
            throw new IncorrectStateException("Already closed", wrapper);
        }
        return entry.size();
    }

    public List<String> modesE() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("modesE", wrapper);
    }

    public int read(Buffer buffer, int off, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {

        if (isClosed()) {
            throw new IncorrectStateException("File already closed", wrapper);
        }

        if (!Flags.READ.isSet(flags)) {
            throw new PermissionDeniedException("No permission to read",
                    wrapper);
        }

        byte[] b;
        try {
            b = buffer.getData();
        } catch (DoesNotExistException e) {
            if (len < 0) {
                throw new BadParameterException(
                        "read: len < 0 and buffer not allocated yet", wrapper);
            }
            buffer.setSize(off + len);
            try {
                b = buffer.getData();
            } catch (DoesNotExistException e2) {
                // This should not happen after setSize() with size >= 0.
                throw new NoSuccessException("Internal error", e2, wrapper);
            }
        }

        int sz = buffer.getSize();

        if (off > sz) {
            throw new BadParameterException("read: offset > buffer size",
                    wrapper);
        }
        if (off + len > sz) {
            throw new BadParameterException(
                    "read: specified len > buffer size", wrapper);
        } else if (len < 0) {
            len = sz - off;
        }

        int result;

        try {
            if (rf != null) {
                result = rf.read(b, off, len);
            } else {
                result = in.read(b, off, len);
            }
        } catch (IOException e) {
            throw new SagaIOException(e, wrapper);
        }
        if (result < 0) {
            // EOF
            result = 0;
        }
        offset += result;
        return result;
    }

    public int readE(String arg0, String arg1, Buffer arg2)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        throw new NotImplementedException("readE", wrapper);
    }

    public long seek(long offset, SeekMode whence)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {

        if (isClosed()) {
            throw new IncorrectStateException("File already closed", wrapper);
        }

        if (!Flags.READ.isSet(flags) && !Flags.WRITE.isSet(flags)) {
            throw new IncorrectStateException(
                    "seek() called but not opened for READ or WRITE", wrapper);
        }

        switch (whence) {
        case START:
            break;
        case CURRENT:
            offset += this.offset;
            break;
        case END:
            offset += getSize();
            break;
        default:
            // Cannot happen
            break;
        }

        if (rf != null) {
            try {
                rf.seek(offset);
                this.offset = rf.getFilePointer();
            } catch (IOException e) {
                throw new SagaIOException(e, wrapper);
            }
            return this.offset;
        }
        if (in != null) {
            if (offset >= this.offset) {
                try {
                    long skipped = in.skip(offset - this.offset);
                    this.offset += skipped;
                    return offset;
                } catch (IOException e) {
                    throw new SagaIOException(e, wrapper);
                }
            } else
                throw new NotImplementedException(
                        "Backwards seek not implemented", wrapper);
        } else {
            throw new NotImplementedException(
                    "Seek on output stream not implemented", wrapper);
        }
    }

    public int sizeE(String arg0, String arg1) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("sizeE", wrapper);
    }

    public int write(Buffer buffer, int off, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {

        if (isClosed()) {
            throw new IncorrectStateException("File already closed", wrapper);
        }

        if (!Flags.WRITE.isSet(flags)) {
            throw new PermissionDeniedException("No permission to write",
                    wrapper);
        }

        byte[] b;
        try {
            b = buffer.getData();
        } catch (DoesNotExistException e) {
            throw new BadParameterException("write: buffer not allocated yet",
                    wrapper);
        }

        if (len + off > buffer.getSize() || len < 0) {
            len = buffer.getSize() - off;
        }
        if (len < 0) {
            throw new BadParameterException("write: offset too large", wrapper);
        }

        try {
            if (rf != null) {
                rf.write(b, off, len);
            } else {
                out.write(b, off, len);
            }
        } catch (IOException e) {
            throw new SagaIOException(e, wrapper);
        }
        offset += len;
        return len;
    }

    public int writeE(String arg0, String arg1, Buffer arg2)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        throw new NotImplementedException("writeE", wrapper);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {

        if (isClosed()) {
            return;
        }

        super.close(timeoutInSeconds);
        entry.close(timeoutInSeconds);
        try {
            if (rf != null) {
                rf.close();
                rf = null;
            } else if (in != null) {
                in.close();
                in = null;
            } else if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            throw new NoSuccessException("close() failed", e, wrapper);
        }
    }

    public void copy(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException,
            IncorrectURLException, NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, DoesNotExistException {
        entry.copy(target, flags);
    }

    public void move(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException,
            NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, IncorrectURLException, DoesNotExistException {
        entry.move(target, flags);
    }

    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.isDir();
    }

    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.isEntry();
    }

    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.isLink();
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        entry.link(target, flags);
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsAllow(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsDeny(id, permissions, flags);

    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.readLink();
    }

    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        entry.remove(flags);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getGroup();
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getOwner();
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return entry.permissionsCheck(id, permissions);
    }
}
