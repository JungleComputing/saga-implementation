package org.ogf.saga.adaptors.fuse.file;

import java.io.IOException;

import org.ogf.saga.adaptors.fuse.AutoMounter;
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
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.file.FileInputStreamWrapper;
import org.ogf.saga.spi.file.FileInputStreamAdaptorBase;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuseFileInputStreamAdaptor extends FileInputStreamAdaptorBase {

    private Logger logger = LoggerFactory.getLogger(FuseFileInputStreamAdaptor.class);
    
    private String mountId;
    private FileInputStream delegate;
    
    public FuseFileInputStreamAdaptor(FileInputStreamWrapper wrapper,
            SessionImpl session, URL source) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException,
            AlreadyExistsException, TimeoutException, NoSuccessException {
    
        super(session, wrapper);

        AutoMounter auto = AutoMounter.getInstance();
        mountId = auto.mount(source, session);

        URL localURL = auto.resolveLocalURL(mountId, source);
        logger.debug("Creating local delegate with URL " + localURL);
        delegate = FileFactory.createFileInputStream(MY_FACTORY, session, localURL);
    }
    
    public int available() throws IOException {
        return delegate.available();
    }

    public void close() throws IOException {
        delegate.close();
    }

    public void mark(int readLimit) {
        delegate.mark(readLimit);
    }

    public boolean markSupported() {
        return delegate.markSupported();
    }

    public int read() throws IOException {
        return delegate.read();
    }

    public int read(byte[] b, int offset, int len) throws IOException {
        return delegate.read(b, offset, len);
    }

    public void reset() throws IOException {
        delegate.reset();
    }

    public long skip(long cnt) throws IOException {
        return delegate.skip(cnt);
    }

}
