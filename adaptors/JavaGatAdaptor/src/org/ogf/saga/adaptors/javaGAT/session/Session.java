package org.ogf.saga.adaptors.javaGAT.session;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.URI;
import org.gridlab.gat.security.CertificateSecurityContext;
import org.gridlab.gat.security.PasswordSecurityContext;
import org.gridlab.gat.security.SecurityContext;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.context.Context;

// Question: where to call GAT.end() ??? When there are no sessions left?
// But the default session is never removed ???

/**
 * Corresponds to a JavaGat Context.
 */
public class Session implements org.ogf.saga.impl.session.AdaptorSessionInterface {

    private static int numSessions = 0;

    private GATContext gatContext = new GATContext();
    
    private HashMap<Context, SecurityContext> contexts
            = new HashMap<Context, SecurityContext>();

    public Session() {
        synchronized (Session.class) {
            numSessions++;
        }
    }

    // This should not be public, but then everything needs to be in a single
    // package ...
    public GATContext getGATContext() {
        return gatContext;
    }

    public synchronized void addContext(Context context) throws NotImplementedException {
        
        try {
            if ("preferences".equals(context.getAttribute(Context.TYPE))) {
                String[] attribs = context.listAttributes();
                for (String s : attribs) {
                    // TODO: exclude all keys predefined in the Context
                    // interface???
                    try {
                        gatContext.addPreference(s, context.getAttribute(s));
                    } catch (Exception e) {
                        // ignore
                    }
                }
                return;
            }
        } catch (Exception e) {
            // ignore
        }
       
        if (! contexts.containsKey(context)) {
            SecurityContext c = cvt2GATSecurityContext(context);
            if (c != null) {
                gatContext.addSecurityContext(c);
                contexts.put(context, c);
            }
        }
    }

    public void close() throws NotImplementedException {
        if (gatContext != null) {
            gatContext = null;
            synchronized (Session.class) {
                numSessions--;
                if (numSessions == 0) {
                    GAT.end();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized Object clone() throws CloneNotSupportedException {
        Session clone = (Session) super.clone();
        clone.gatContext = (GATContext) gatContext.clone();
        clone.contexts = new HashMap<Context, SecurityContext>(contexts);
        return clone;
    }

    public void close(float timeoutInSeconds) throws NotImplementedException {
        close();
    }

    public synchronized void removeContext(Context context)
            throws NotImplementedException, DoesNotExistException {
        
        SecurityContext c = contexts.remove(context);
        
        if (c != null) {
            gatContext.removeSecurityContext(c);
        }
    }
    
    SecurityContext cvt2GATSecurityContext(Context ctxt) {
        String type = ctxt.getValue(Context.TYPE);
        if ("ftp".equals(type)) {
            return new PasswordSecurityContext(ctxt.getValue(Context.USERID),
                    ctxt.getValue(Context.USERPASS));
        } else if ("globus".equals(type) || "gridftp".equals(type)) {
            try {
                return new CertificateSecurityContext(
                        new URI(ctxt.getValue(Context.USERKEY)),
                        new URI(ctxt.getValue(Context.USERCERT)),
                        ctxt.getValue(Context.USERPASS));
            } catch (URISyntaxException e) {
                // what to do? nothing?
            }
        } else if ("ssh".equals(type) || "sftp".equals(type)) {
            String userId = ctxt.getValue(Context.USERID);
            if (userId == null || userId.equals("")) {
                userId = System.getProperty("user.name");
            }
            if (!ctxt.getValue(Context.USERKEY).equals("")) {
                try {
                    return new CertificateSecurityContext(
                            new URI(ctxt.getValue(Context.USERKEY)),
                            new URI(ctxt.getValue(Context.USERCERT)),
                            userId,
                            ctxt.getValue(Context.USERPASS));
                } catch (URISyntaxException e) {
                    // what to do? nothing?
                }
            } else if (!ctxt.getValue(Context.USERPASS).equals("")) {
                return new PasswordSecurityContext(userId,
                        ctxt.getValue(Context.USERPASS));
            }
        }
        return null;
    }
}
