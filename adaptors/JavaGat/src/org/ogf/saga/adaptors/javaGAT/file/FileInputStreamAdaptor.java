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
import org.ogf.saga.proxies.file.FileInputStreamWrapper;
import org.ogf.saga.adaptors.javaGAT.util.Initialize;

public class FileInputStreamAdaptor extends org.ogf.saga.spi.file.FileInputStreamAdaptorBase {

    static {
        Initialize.initialize();
    }
    
    private org.gridlab.gat.io.FileInputStream in;
    
    public FileInputStreamAdaptor(FileInputStreamWrapper wrapper, Session session, URL source)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, DoesNotExistException, AlreadyExistsException, TimeoutException, NoSuccessException {
        
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
            in = GAT.createFileInputStream(gatContext, gatURI);
        } catch (GATObjectCreationException e) {
            throw new NoSuccessException("Could not create input stream", e);
        }
    }
    
    public int read() throws IOException {
        return in.read();
    }
    
    public int available() throws IOException {
        return in.available();
    }
    
    public void close() throws IOException {
        in.close();
        in = null;
    }
      
    protected void finalize() {
        try {
            if (in != null) {
                in.close();
            }
        } catch(Throwable e) {
            // ignored
        }
    }

    public void mark(int arg0) {
        in.mark(arg0);
    }
    
    public boolean markSupported() {
        return in.markSupported();
    }
    
    public int read(byte[] arg0, int arg1, int arg2) throws IOException {
        return in.read(arg0, arg1, arg2);
    }

    public int read(byte[] arg0) throws IOException {
        return in.read(arg0);
    }
    
    public void reset() throws IOException {
        in.reset();
    }

    public long skip(long arg0) throws IOException {
        return in.skip(arg0);
    }
    
    public Object clone() throws CloneNotSupportedException {
        FileInputStreamAdaptor clone = (FileInputStreamAdaptor) super.clone();
        clone.setWrapper(clone.wrapper);
        return clone;
    }

}
