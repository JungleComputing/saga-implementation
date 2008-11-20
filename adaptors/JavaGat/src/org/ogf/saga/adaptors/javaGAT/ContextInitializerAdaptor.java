package org.ogf.saga.adaptors.javaGAT;

import java.io.File;

import org.ogf.saga.context.Context;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.context.ContextImpl;
import org.ogf.saga.impl.context.ContextInitializerSPI;

public class ContextInitializerAdaptor  extends AdaptorBase<Object>
        implements ContextInitializerSPI {

    public ContextInitializerAdaptor() {
        super(null, null);
    }

    public void setDefaults(ContextImpl context, String type)
            throws NotImplementedException {
        try {
            if ("ftp".equals(type)) {
                // Default is anonymous
                context.setValueIfEmpty(Context.USERID, "anonymous");
                context.setValueIfEmpty(Context.USERPASS,
                        "anonymous@localhost");
            } else if ("ssh".equals(type) || "sftp".equals(type)) {
                // setValue(Context.USERID, "");
                // setValue(Context.USERPASS, "");
                // setValue(Context.USERKEY, "");
            } else if ("globus".equals(type) || "gridftp".equals(type)) {
                // Default: taken from .globus dir in user home.
                String home = System.getProperty("user.home");
                context.setValueIfEmpty(Context.USERKEY, home + File.separator
                        + ".globus" + File.separator + "userkey.pem");
                context.setValueIfEmpty(Context.USERCERT, home + File.separator
                        + ".globus" + File.separator + "usercert.pem");
                // attributes.setValue(Context.USERPASS, "");
            } else if ("preferences".equals(type)) {
                // nothing
            } else {
                throw new NotImplementedException(
                        "This adaptor does not recognize the " + type
                                + " context");
            }
        } catch(NotImplementedException e) {
            throw e;
        } catch (Throwable e) {
            // Should not happen.
            throw new Error("Internal error: got exception", e);
        }
    }
}
