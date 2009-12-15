package org.ogf.saga.adaptors.local.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.List;

import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.LocalAdaptorTool;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileAdaptor extends FileAdaptorBase {

    private static final String FILE_ACCESS_NOT_IMPLEMENTED = 
        "Access of file contents is not implemented";
    private static final Logger logger = LoggerFactory
            .getLogger(LocalFileAdaptor.class);
    
    private LocalNSEntryAdaptor entry;
    private FileChannel channel;
    private WeakReference<ByteBuffer> cachedByteBuffer;
    
    public LocalFileAdaptor(FileWrapper wrapper, SessionImpl session, URL name,
            int flags) throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {

        super(wrapper, session, name, flags);

        AdaptorTool tool = LocalAdaptorTool.getInstance();

        entry = new LocalNSEntryAdaptor(null, session, name, flags
                & Flags.ALLNAMESPACEFLAGS.getValue(), false, tool);

        // determine open mode
        String mode = null;

        if (Flags.WRITE.isSet(flags)) {
            mode = "rw";
        } else if (Flags.READ.isSet(flags)) {
            mode = "r";
        }

        if (mode != null) {
            try {
                // open the file, if needed.
                RandomAccessFile raf = entry.createRandomAccessFile(mode);
                channel = raf.getChannel();
            } catch (FileNotFoundException e) {
                throw new NoSuccessException("Cannot open file", e);
            }

            if (channel != null) {
                // truncate if needed.
                if (Flags.TRUNCATE.isSet(flags)) {
                    try {
                        channel.truncate(0);
                    } catch (IOException e) {
                        throw new NoSuccessException("Truncate failed", e);
                    }
                } else if (Flags.APPEND.isSet(flags)) {
                    try {
                        long size = channel.size();
                        channel.position(size);
                    } catch (IOException e) {
                        throw new NoSuccessException("Append failed", e);
                    }
                }
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        LocalFileAdaptor clone = (LocalFileAdaptor) super.clone();
        clone.entry = (LocalNSEntryAdaptor) entry.clone();
        clone.entry.setWrapper(clone.wrapper);
        return clone;
    }
    
    @Override
    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        if (isClosed()) {
            return;
        }
        
        super.close(timeoutInSeconds);
        entry.close(timeoutInSeconds);
        
        if (cachedByteBuffer != null) {
            cachedByteBuffer.clear();
        }
    
        try {
            if (channel != null) {
                channel.close();
                channel = null;
            }
        } catch (IOException e) {
            throw new NoSuccessException("Close failed", e);
        }
    }
    
    public long getSize() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        try {
            return channel.size();
        } catch (ClosedChannelException e) {
            throw new IncorrectStateException(
                    "Underlying file channel is closed", e);
        } catch (IOException e) {
            throw new NoSuccessException("I/O error", e);
        }
    }

    public List<String> modesE() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException(FILE_ACCESS_NOT_IMPLEMENTED);
    }

    private ByteBuffer createByteBuffer(byte[] bytes) {
        ByteBuffer result;
        
        if (cachedByteBuffer == null) {
            result = cacheByteBuffer(bytes);
        } else {
            result = cachedByteBuffer.get();
            
            if (result == null ) {
                result = cacheByteBuffer(bytes);
            } else if (result.array() != bytes) {
                result = cacheByteBuffer(bytes);
            } else {
                logger.debug("Using cached byte buffer");
            }
        }
        
        return result;
    }
    
    private ByteBuffer cacheByteBuffer(byte[] bytes) {
        ByteBuffer result = ByteBuffer.wrap(bytes);
        cachedByteBuffer = new WeakReference<ByteBuffer>(result);
        return result;
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
                // should not happen after setSize() with size >= 0.
                throw new NoSuccessException("Internal error", e2);
            }
        }

        int size = buffer.getSize();

        if (offset > size) {
            throw new BadParameterException("offset (" + offset
                    + ") > buffer size (" + size + ")");
        } else if (offset + len > size) {
            throw new BadParameterException("offset + len (" + (offset + len) 
                    + ") > buffer size (" + size + ")");
        } else if (len < 0) {
            len = size - offset;
        }

        ByteBuffer byteBuf = createByteBuffer(b);
        byteBuf.limit(offset + len);
        byteBuf.position(offset);
        
        int bytesRead;
        try {
            bytesRead = channel.read(byteBuf);
        } catch (IOException e) {
            throw new SagaIOException(e);
        }
        
        if (bytesRead < 0) {
            // EOF
            bytesRead = 0;
        }

        return bytesRead;
    }

    public int readE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        throw new NotImplementedException("Extended I/O is not supported");
    }

    public long seek(long offset, SeekMode whence)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        
        try {
            switch (whence) {
            case START:
                channel.position(offset);
                break;
            case CURRENT:
                long position = channel.position();
                channel.position(position + offset);
                break;
            case END:
                long end = channel.size();
                channel.position(end + offset);
                break;
            }
            
            return channel.position();
        } catch (IOException e) {
            throw new SagaIOException("Seek failed", e);
        }
    }

    public int sizeE(String emode, String spec) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Extended I/O is not supported");
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
            throw new BadParameterException("buffer not allocated yet");
        }

        if (offset + len > buffer.getSize() || len < 0) {
            len = buffer.getSize() - offset;
        }
        if (len < 0) {
            throw new BadParameterException("offset (" + offset + ") too large");
        }

        ByteBuffer byteBuf = createByteBuffer(b);
        byteBuf.limit(offset + len);
        byteBuf.position(offset);
                
        int bytesWritten = 0;
        try {
            bytesWritten = channel.write(byteBuf);
        } catch (IOException e) {
            throw new SagaIOException(e, wrapper);
        }

        return bytesWritten;
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
}
