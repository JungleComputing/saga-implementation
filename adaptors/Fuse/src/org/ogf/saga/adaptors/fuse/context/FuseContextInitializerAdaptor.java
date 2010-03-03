package org.ogf.saga.adaptors.fuse.context;

import java.util.List;

import org.ogf.saga.adaptors.fuse.AutoMounter;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.AdaptorBase;
import org.ogf.saga.impl.context.ContextImpl;
import org.ogf.saga.impl.context.ContextInitializerSPI;

public class FuseContextInitializerAdaptor  extends AdaptorBase<Object>
        implements ContextInitializerSPI {

    public FuseContextInitializerAdaptor() {
        super(null, null);
    }

    public void setDefaults(ContextImpl context, String type)
            throws NotImplementedException {
        
        AutoMounter auto = AutoMounter.getInstance();
        
        if (!auto.isAcceptedContextType(type)) {
            List<String> acceptedTypes = auto.getAllAcceptedContextTypes();
            
            throw new NotImplementedException("Unsupported context type: " 
                    + type + ", the FUSE adaptor only supports "
                    + acceptedTypes.toString());
        }
    }
}

    