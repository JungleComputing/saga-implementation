package org.ogf.saga.adaptors.local.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

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
import org.ogf.saga.proxies.file.FileOutputStreamWrapper;
import org.ogf.saga.spi.file.FileOutputStreamAdaptorBase;
import org.ogf.saga.url.URL;

public class LocalOutputStreamAdaptor extends FileOutputStreamAdaptorBase {

    private OutputStream out;
    private AdaptorTool tool;
    
    public LocalOutputStreamAdaptor(FileOutputStreamWrapper wrapper,
            SessionImpl session, URL source) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException,
            AlreadyExistsException, TimeoutException, NoSuccessException {
        this(wrapper, session, source, LocalAdaptorTool.getInstance());
    }
    
    protected LocalOutputStreamAdaptor(FileOutputStreamWrapper wrapper,
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
            out = tool.createFileOutputStream(path);
        } catch (FileNotFoundException e) {
            throw new DoesNotExistException(source.toString());
        }
        
        this.tool = tool;
    }
    
    public void close() throws IOException {
        tool.close(out);
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void write(int b) throws IOException {
        out.write(b);
    }

    public void write(byte[] b, int offset, int len) throws IOException {
        out.write(b, offset, len);
    }
    
}
