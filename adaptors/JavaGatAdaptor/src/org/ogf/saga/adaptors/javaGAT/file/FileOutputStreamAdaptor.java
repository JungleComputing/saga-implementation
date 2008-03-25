package org.ogf.saga.adaptors.javaGAT.file;

import java.io.IOException;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
import org.ogf.saga.URL;
import org.ogf.saga.adaptors.javaGAT.namespace.NSEntryAdaptor;
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
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.proxies.file.FileOutputStreamWrapper;

public class FileOutputStreamAdaptor extends org.ogf.saga.spi.file.FileOutputStreamAdaptorBase {
    
    private org.gridlab.gat.io.FileOutputStream out;

    public FileOutputStreamAdaptor(FileOutputStreamWrapper wrapper, Session session,
            URL source, boolean append) 
            throws NotImplementedException, IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            DoesNotExistException, AlreadyExistsException, TimeoutException, NoSuccessException {
        
        super(session, wrapper);
        
        org.ogf.saga.adaptors.javaGAT.session.Session gatSession;

        synchronized(session) {
            gatSession = (org.ogf.saga.adaptors.javaGAT.session.Session)
                    session.getAdaptorSession("JavaGAT");
            if (gatSession == null) {
                gatSession = new org.ogf.saga.adaptors.javaGAT.session.Session();
                session.putAdaptorSession("JavaGAT", gatSession);
            }
        }

        GATContext gatContext = gatSession.getGATContext();
        URI gatURI = NSEntryAdaptor.cvtToGatURI(source);
        
        try {
            out = GAT.createFileOutputStream(gatContext, gatURI, append);
        } catch (GATObjectCreationException e) {
            throw new NoSuccessException("Could not create output stream", e);
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        FileOutputStreamAdaptor clone = (FileOutputStreamAdaptor) super.clone();
        clone.setWrapper(clone.wrapper);
        return clone;
    }
    
    public void write(int b) throws IOException {
        out.write(b);
    }
    
    public void close() throws IOException {
            if (out != null) {
            out.close();
        }
        out = null;
    }
    
   
    protected void finalize() {
        try {
            close();
        } catch(Throwable e) {
            // ignored
        }
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }
    
    public void write(byte[] b) throws IOException {
        out.write(b);
    }
}
