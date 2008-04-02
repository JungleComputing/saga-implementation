package org.ogf.saga.adaptors.javaGAT.file;

import org.ogf.saga.URL;
import org.ogf.saga.adaptors.javaGAT.namespace.NSDirectoryAdaptor;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;

class DirectoryEntry extends NSDirectoryAdaptor {

    public DirectoryEntry(NSDirectoryWrapper wrapper, Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException, BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);
    }
      
    protected void nonResolvingCopy(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException, IncorrectURLException,
            NotImplementedException {
        super.nonResolvingCopy(target, flags);
    }
    
    protected void nonResolvingMove(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException, NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, IncorrectURLException {
        super.nonResolvingMove(target, flags);
    }

}
