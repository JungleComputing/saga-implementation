package org.ogf.saga.adaptors.javaGAT.logicalfile;

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

// Serves to make constructor visible
class DirectoryEntry extends NSDirectoryAdaptor {

    DirectoryEntry(NSDirectoryWrapper wrapper, Session session, URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException, DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException, TimeoutException, NoSuccessException,
            AlreadyExistsException {
        super(wrapper, session, name, flags);
    }

    protected void unresolvedCopy(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException, IncorrectURLException,
            NotImplementedException {
        super.unresolvedCopy(target, flags);
    }

    protected void unresolvedMove(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException, NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, IncorrectURLException {
        super.unresolvedMove(target, flags);
    }
}
