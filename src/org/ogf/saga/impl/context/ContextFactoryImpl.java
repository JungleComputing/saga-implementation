package org.ogf.saga.impl.context;

import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.TimeoutException;

public class ContextFactoryImpl extends org.ogf.saga.context.ContextFactory {

    @Override
    protected org.ogf.saga.context.Context doCreateContext(String type)
            throws IncorrectStateException, TimeoutException, NoSuccessException {
        return new ContextImpl(type);
    }
}
