package org.ogf.saga.impl.context;

import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.Timeout;

public class ContextFactory extends org.ogf.saga.context.ContextFactory {

    @Override
    protected org.ogf.saga.context.Context doCreateContext(String type)
            throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        return new Context(type);
    }
}
