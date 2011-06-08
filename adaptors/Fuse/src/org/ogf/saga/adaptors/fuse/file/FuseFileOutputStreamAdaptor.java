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
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.proxies.file.FileOutputStreamWrapper;
import org.ogf.saga.spi.file.FileOutputStreamAdaptorBase;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuseFileOutputStreamAdaptor extends FileOutputStreamAdaptorBase {

private Logger logger = LoggerFactory.getLogger(FuseFileOutputStreamAdaptor.class);
    
    private String mountId;
    private FileOutputStream delegate;
    
    public FuseFileOutputStreamAdaptor(FileOutputStreamWrapper wrapper,
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
        delegate = FileFactory.createFileOutputStream(MY_FACTORY, session, localURL);
    }
    
    public void close() throws IOException {
        delegate.close();
    }

    public void flush() throws IOException {
        delegate.flush();
    }

    public void write(int b) throws IOException {
        delegate.write(b);
    }

    public void write(byte[] b, int offset, int len) throws IOException {
        delegate.write(b, offset, len);
    }
    
}
