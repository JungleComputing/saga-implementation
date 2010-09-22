package org.ogf.saga.adaptors.archive.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.ogf.saga.adaptors.archive.ArchiveAdaptorTool;
import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.namespace.LocalNSEntryAdaptor;
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
import org.ogf.saga.url.URL;

import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;

public class ArchiveFileAdaptor extends FileAdaptorBase {
    
    public static String[] getSupportedSchemes() {
        return ArchiveAdaptorTool.getSupportedSchemes();
    }

    private LocalNSEntryAdaptor entry;
    private FileInputStream in;
    private FileOutputStream out;
    private long inOffset = 0L;
    
    public ArchiveFileAdaptor(FileWrapper wrapper, SessionImpl session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);
        
        AdaptorTool tool = ArchiveAdaptorTool.getInstance();
        
        entry = new LocalNSEntryAdaptor(null, session, name, 
                flags & Flags.ALLNAMESPACEFLAGS.getValue(), false, tool); 
    
        if (Flags.READ.isSet(flags) && Flags.WRITE.isSet(flags)) {
            throw new NoSuccessException("READWRITE is not supported");
        }
        if (Flags.APPEND.isSet(flags)) {
            throw new NoSuccessException("APPEND is not supported");
        }
        if (Flags.TRUNCATE.isSet(flags)) {
            throw new NoSuccessException("TRUNCATE is not supported");
        }

        String path = getEntryURL().getPath();
        
        try {
            File file = tool.createFile(path);
            
            if (Flags.READ.isSet(flags)) {
                in = new FileInputStream(file);
            } else if (Flags.WRITE.isSet(flags)) {
                out = new FileOutputStream(file);
            }
        } catch (FileNotFoundException e) {
            throw new DoesNotExistException("Cannot open file", e);
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        ArchiveFileAdaptor clone = (ArchiveFileAdaptor) super.clone();
        clone.entry = (LocalNSEntryAdaptor) entry.clone();
        clone.entry.setWrapper(clone.wrapper);
        return clone;
    }
    
    @Override
    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        if (isClosed()) {
            return;
        }
        
        super.close(timeoutInSeconds);

        AdaptorTool tool = ArchiveAdaptorTool.getInstance();
        
        try {
            // N.B. either in or out is null, so we always close all streams 
            tool.close(in);
            tool.close(out);
        } catch (IOException e) {
            throw new NoSuccessException("Close failed", e);   
        } finally {
            entry.close(timeoutInSeconds);
        }
    }
    
    public long getSize() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.getLength();
    }
 
    public int read(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        byte[] b;
        try {
            b = buffer.getData();
        } catch (DoesNotExistException e) {
            if (len < 0) {
                throw new BadParameterException(
                        "len < 0 and buffer not allocated yet");
            }
            buffer.setSize(offset + len);
            try {
                b = buffer.getData();
            } catch (DoesNotExistException e2) {
                // This should not happen after setSize() with size >= 0.
                throw new NoSuccessException("Internal error", e2);
            }
        }

        int size = buffer.getSize();

        if (offset > size) {
            throw new BadParameterException("offset > buffer size");
        } else if (len < 0) {
            len = size - offset;
        } else if (offset + len > size) {
            throw new BadParameterException("offset+len > buffer size");
        }

        int result;

        try {
            result = in.read(b, offset, len);
        } catch (IOException e) {
            throw new SagaIOException("Read failed", e);
        }
        
        if (result < 0) {
            // EOF
            result = 0;
        }
        
        return result;
    }

    public int write(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        byte[] b;
        try {
            b = buffer.getData();
        } catch (DoesNotExistException e) {
            throw new BadParameterException("Buffer not allocated yet");
        }

        if (len < 0 || len + offset > buffer.getSize()) {
            len = buffer.getSize() - offset;
        }
        if (len < 0) {
            throw new BadParameterException("Offset too large");
        }

        try {
            out.write(b, offset, len);
        } catch (IOException e) {
            throw new SagaIOException("Write failed", e);
        }

        return len;
    }

    public long seek(long offset, SeekMode whence)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        if (in == null) {
            throw new NotImplementedException("Cannot seek on output stream");
        }
        
        switch (whence) {
        case START:
            break;
        case CURRENT:
            offset += inOffset;
            break;
        case END:
            offset += getSize();
            break;
        }

        if (offset < inOffset) {
            throw new NotImplementedException("Cannot seek backwards");
        }
        
        try {
            long skipped = in.skip(offset - inOffset);
            inOffset += skipped;
            return offset;
        } catch (IOException e) {
            throw new SagaIOException("Seek to " + offset + " failed", e);
        }
    }

    public List<String> modesE() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return Collections.emptyList();
    }

    public int readE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        throw new NotImplementedException("readE()", wrapper);
    }

    public int sizeE(String emode, String spec) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Extended I/O is not supported");
    }

    public int writeE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        throw new NotImplementedException("Extended I/O is not supported");
    }

    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        entry.copy(target, flags);
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

    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        entry.move(target, flags);
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

    public long getMTime() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.getMTime();
    }
    
}
