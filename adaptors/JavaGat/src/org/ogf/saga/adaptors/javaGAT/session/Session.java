package org.ogf.saga.adaptors.javaGAT.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.URI;
import org.gridlab.gat.security.CertificateSecurityContext;
import org.gridlab.gat.security.CredentialSecurityContext;
import org.gridlab.gat.security.PasswordSecurityContext;
import org.gridlab.gat.security.SecurityContext;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.context.ContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Question: where to call GAT.end() ??? When there are no sessions left?
// But the default session is never removed ???

/**
 * Corresponds to a JavaGat Context.
 */
public class Session implements
        org.ogf.saga.impl.session.AdaptorSessionInterface, Cloneable {
    
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    private static int numSessions = 0;

    private GATContext gatContext = new GATContext();

    private HashMap<ContextImpl, SecurityContext> contextImpls = new HashMap<ContextImpl, SecurityContext>();

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

    public synchronized void addContext(ContextImpl contextImpl)
            throws NotImplementedException {

        try {
            if ("preferences"
                    .equals(contextImpl.getAttribute(ContextImpl.TYPE))) {
                String[] attribs = contextImpl.listAttributes();
                for (String s : attribs) {
                    // TODO: exclude all keys predefined in the Context
                    // interface???
                    try {
                        gatContext
                                .addPreference(s, contextImpl.getAttribute(s));
                    } catch (Exception e) {
                        // ignore
                    }
                }
                return;
            }
        } catch (Throwable e) {
            // ignore
        }

        if (!contextImpls.containsKey(contextImpl)) {
            SecurityContext c = cvt2GATSecurityContext(contextImpl);
            if (c != null) {
                gatContext.addSecurityContext(c);
                contextImpls.put(contextImpl, c);
            }
        }
    }

    public synchronized void close() throws NotImplementedException {
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
        synchronized (clone) {
            clone.gatContext = (GATContext) gatContext.clone();
            clone.contextImpls = new HashMap<ContextImpl, SecurityContext>(
                    contextImpls);
        }
        return clone;
    }

    public void close(float timeoutInSeconds) throws NotImplementedException {
        close();
    }

    public synchronized void removeContext(ContextImpl contextImpl)
            throws NotImplementedException, DoesNotExistException {

        SecurityContext c = contextImpls.remove(contextImpl);

        if (c != null) {
            gatContext.removeSecurityContext(c);
        }
    }

    static SecurityContext cvt2GATSecurityContext(ContextImpl ctxt) {
        String type = ctxt.getValue(ContextImpl.TYPE);
        if ("ftp".equals(type)) {
            SecurityContext c = new PasswordSecurityContext(
                    ctxt.getValue(ContextImpl.USERID),
                    ctxt.getValue(ContextImpl.USERPASS));
            c.addNote("adaptors", "ftp");
            return c;
        } else if ("globus".equals(type) || "gridftp".equals(type)) {
            String proxy = ctxt.getValue(ContextImpl.USERPROXY);
            if (proxy != null) {
                // JavaGAT does not have a security context that refers to
                // a proxy file, but it does have a CredentialSecurityContext
                // that has the credential itself as a byte array. So, we
                // try to read the proxy file here.
                try {
                    long length = new File(proxy).length();
                    FileInputStream f = new FileInputStream(proxy);
                    byte[] buf = new byte[(int) length];
                    int len = f.read(buf, 0, buf.length);
                    if (len > 0) {

                        SecurityContext c = new CredentialSecurityContext(buf);
                        c.addNote("adaptors", "globus,wsgt4new");
                        return c;
                    } else {
                        logger.info("read from proxy file gave " + len);
                    }
                } catch (FileNotFoundException e) {
                    logger.info("Could not open proxy file " + proxy, e);
                } catch (IOException e) {
                    logger.info("Could not read proxy file " + proxy, e);
                }
            }
            try {
                SecurityContext c = new CertificateSecurityContext(
                        new URI(ctxt.getValue(ContextImpl.USERKEY)),
                        new URI(ctxt.getValue(ContextImpl.USERCERT)),
                        ctxt.getValue(ContextImpl.USERPASS));
                c.addNote("adaptors", "globus,wsgt4new");
                return c;
            } catch (URISyntaxException e) {
                // what to do? nothing?
            }
        } else if ("ssh".equals(type) || "sftp".equals(type)) {
            String userId = ctxt.getValue(ContextImpl.USERID);
            if (userId == null || userId.equals("")) {
                userId = System.getProperty("user.name");
            }
            if (!ctxt.getValue(ContextImpl.USERKEY).equals("")) {
                try {
                    SecurityContext c = new CertificateSecurityContext(
                            new URI(ctxt.getValue(ContextImpl.USERKEY)),
                            new URI(ctxt.getValue(ContextImpl.USERCERT)), userId,
                            ctxt.getValue(ContextImpl.USERPASS));
                    c.addNote("adaptors", "commandlinessh,sshtrilead,sftptrilead");
                    return c;
                } catch (URISyntaxException e) {
                    // what to do? nothing?
                }
            } else if (!ctxt.getValue(ContextImpl.USERPASS).equals("")) {
                SecurityContext c = new PasswordSecurityContext(userId,
                        ctxt.getValue(ContextImpl.USERPASS));
                c.addNote("adaptors", "commandlinessh,sshtrilead,sftptrilead");
                return c;
            }
        }
        return null;
    }
}
