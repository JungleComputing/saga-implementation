package org.ogf.saga.adaptors.local.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.LocalAdaptorTool;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.file.FileInputStreamWrapper;
import org.ogf.saga.spi.file.FileInputStreamAdaptorBase;
import org.ogf.saga.url.URL;

public class LocalInputStreamAdaptor extends FileInputStreamAdaptorBase {

    private InputStream in;
    
    public LocalInputStreamAdaptor(FileInputStreamWrapper wrapper,
            SessionImpl session, URL source) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException,
            AlreadyExistsException, TimeoutException, NoSuccessException {
        this(wrapper, session, source, LocalAdaptorTool.getInstance());
    }
    
    protected LocalInputStreamAdaptor(FileInputStreamWrapper wrapper,
            SessionImpl session, URL source, AdaptorTool tool)
            throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException,
            AlreadyExistsException, TimeoutException, NoSuccessException {
    
        super(session, wrapper);

        tool.checkURL(source);
        
        String path = source.getPath();
        try {
            in = tool.createFileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new DoesNotExistException(source.toString());
        }
    }
    
    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    public void mark(int readLimit) {
        in.mark(readLimit);
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public int read() throws IOException {
        return in.read();
    }

    public int read(byte[] b, int offset, int len) throws IOException {
        return in.read(b, offset, len);
    }

    public void reset() throws IOException {
        in.reset();
    }

    public long skip(long cnt) throws IOException {
        return in.skip(cnt);
    }

}
