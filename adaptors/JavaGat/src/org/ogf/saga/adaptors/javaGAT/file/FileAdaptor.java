package org.ogf.saga.adaptors.javaGAT.file;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
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
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.FileWrapper;
import org.ogf.saga.spi.file.FileAdaptorBase;
import org.ogf.saga.spi.file.FileSPI;
import org.ogf.saga.url.URL;

// Aaarggghh, javagat only has a local RandomAccessFile adaptor!!!
// So, this is no good, but there is nothing else ...
// input/output streams don't support seeks ...
// So, make a copy to a local file??? and write on close()??? TODO!
// Or, implement with streams, don't support read/write, and only support
// forward seeks.

public class FileAdaptor extends FileAdaptorBase implements FileSPI {
    
    private static Logger logger = Logger.getLogger(FileAdaptor.class);
    
    static {
        Initialize.initialize();
    }
    
    private int flags;
    private long offset = 0L;
    private RandomAccessFile rf;
    private FileEntry entry;

    public FileAdaptor(FileWrapper wrapper, Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException, BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        
        super(wrapper, session, name, flags);
        entry = new FileEntry(session, name, flags & Flags.ALLNAMESPACEFLAGS.getValue());
        this.flags = flags;
        
        // Determine read/write
        String rfflags = "r";
        if (Flags.WRITE.isSet(flags)) {
            rfflags = "rw";
        }
        
        // Open the file.
        try {
            rf = GAT.createRandomAccessFile(entry.getGatContext(), entry.getGatURI(),
                    rfflags);
        } catch (GATObjectCreationException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("GAT.createFile failed");
            }
            throw new NoSuccessException(e);
        }
        
        // Deal with append
        if (Flags.APPEND.isSet(flags)) {
            if (Flags.TRUNCATE.isSet(flags)) {
                throw new BadParameterException("TRUNCATE and APPEND?");
            }
        }
        
        // Truncate if needed.
        if (Flags.TRUNCATE.isSet(flags)) {
            if (! Flags.WRITE.isSet(flags)) {
                throw new BadParameterException("TRUNCATE but no WRITE?");
            }
            try {
                rf.setLength(0L);
            } catch(IOException e) {
                throw new NoSuccessException("Truncate failed", e);
            }
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        FileAdaptor clone = (FileAdaptor) super.clone();
        clone.entry = (FileEntry) entry.clone();
        clone.entry.setWrapper(clone.wrapper);
        return clone;
    }
    
    public long getSize() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("File already closed!");
            }
            throw new IncorrectStateException("Already closed");
        }
        try {
            return rf.length();
        } catch(IOException e) {
            throw new NoSuccessException("RandomAccessFile.length() gave IOException", e);
        }
    }

   public List<String> modesE() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("Not implemented!");
    }
        
    public int read(Buffer buffer, int off, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException, SagaIOException {
        
        if (closed) {
            throw new IncorrectStateException("File already closed");
        }
        
        if (!Flags.READ.isSet(flags)) {
            throw new PermissionDeniedException("No permission to read");
        }
                     
        byte[] b;
        try {
            b = buffer.getData();
        } catch(DoesNotExistException e) {
            if (len < 0) {
                throw new BadParameterException("read: len < 0 and buffer not allocated yet");
            }
            buffer.setSize(off + len);
            try {
                b = buffer.getData();
            } catch(DoesNotExistException e2) {
                // This should not happen after setSize() with size >= 0.
                throw new NoSuccessException("Internal error", e2);
            }
        }
        
        int sz = buffer.getSize();
        
        if (off > sz) {
            throw new BadParameterException("read: offset > buffer size");
        }
        if (off + len > sz) {
            throw new BadParameterException("read: specified len > buffer size");
        } else if (len < 0) {
            len = sz - off;
        }
               
        int result;
        try {
            result = rf.read(b, off, len);
        } catch (IOException e) {
            throw new SagaIOException(e);
        }
        if (result < 0) {
            // EOF
            result = 0;
        }
        offset += result;
        return result;
    }

    public int readE(String arg0, String arg1, Buffer arg2)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        throw new NotImplementedException("Not implemented!");
    }

    public long seek(long offset, SeekMode whence) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException, SagaIOException {
        
        if (closed) {
            throw new IncorrectStateException("File already closed");
        }
        
        switch(whence) {
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
        
        try {
            rf.seek(offset);
            this.offset = rf.getFilePointer();
        } catch (IOException e) {
            throw new SagaIOException(e);
        }
        return this.offset;
    }

    public int sizeE(String arg0, String arg1) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Not implemented!");
    }

    public int write(Buffer buffer, int off, int len) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException, SagaIOException {
        
        if (closed) {
            throw new IncorrectStateException("File already closed");
        }
        
        if (!Flags.WRITE.isSet(flags)) {
            throw new PermissionDeniedException("No permission to write");
        }
        
        if (Flags.APPEND.isSet(flags)) { 
            seek(0L, SeekMode.END);
        }
        
        byte[] b;
        try {
            b = buffer.getData();
        } catch(DoesNotExistException e) {
            throw new BadParameterException("write: buffer not allocated yet");
        }

        if (len + off > buffer.getSize() || len < 0) {
            len = buffer.getSize() - off;
        }
        if (len < 0) {
            throw new BadParameterException("writeV: offset too large");
        }
        
        try {
            rf.write(b, off, len);
        } catch (IOException e) {
            throw new SagaIOException(e);
        }
        offset += len;
        return len;
    }

    public int writeE(String arg0, String arg1, Buffer arg2)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        throw new NotImplementedException("Not implemented!");
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        
        if (closed) {
            return;
        }
        
        super.close(timeoutInSeconds);
        entry.close(timeoutInSeconds);
        try {
            rf.close();
        } catch (IOException e) {
            throw new NoSuccessException("RandomAccessFile close() failed", e);
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

    public boolean isDir() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isDir();
    }

    public boolean isEntry() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isEntry();
    }

    public boolean isLink() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isLink();
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        entry.link(target, flags);        
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, BadParameterException, TimeoutException, NoSuccessException {
        entry.permissionsAllow(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        entry.permissionsDeny(id, permissions, flags);
        
    }

    public URL readLink() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.readLink();
    }

    public void remove(int flags) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        entry.remove(flags);
    }

    public String getGroup() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getGroup();
    }

    public String getOwner() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getOwner();
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        return entry.permissionsCheck(id, permissions);
    }
}

