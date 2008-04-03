package org.ogf.saga.adaptors.thirdParty.namespace;

import org.ogf.saga.URL;
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
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.spi.namespace.NSEntryAdaptorBase;

// Implements move with copy and remove.

public class NSEntryAdaptor extends NSEntryAdaptorBase {

    public NSEntryAdaptor(NSEntryWrapper wrapper, Session session, URL name,
            int flags) throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);
    }
    
    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        ((NSEntryWrapper) wrapper).copy(target, flags);
        ((NSEntryWrapper) wrapper).remove();
    }

    protected void nonResolvingCopy(URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    protected void nonResolvingMove(URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean isDir() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean isEntry() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean isLink() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void link(URL target, int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public URL readLink() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void remove(int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public String getGroup() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public String getOwner() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean permissionsCheck(String arg0, int arg1)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

}
