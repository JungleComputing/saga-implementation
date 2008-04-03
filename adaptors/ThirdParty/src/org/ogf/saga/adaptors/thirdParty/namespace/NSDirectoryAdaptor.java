package org.ogf.saga.adaptors.thirdParty.namespace;

import java.util.List;

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
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.spi.namespace.NSDirectoryAdaptorBase;

public class NSDirectoryAdaptor extends NSDirectoryAdaptorBase {

    public NSDirectoryAdaptor(NSDirectoryWrapper wrapper, Session session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);
    }

    public List<URL> listCurrentDir(int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    protected void nonResolvingCopy(URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    protected void nonResolvingMove(URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void changeDir(URL dir) throws NotImplementedException {
        throw new NotImplementedException(); 
    }

    public void copy(URL source, URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void copy(String source, URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean exists(URL name) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public URL getEntry(int entry) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public int getNumEntries() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean isDir(URL name) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean isEntry(URL name) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public boolean isLink(URL name) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void link(URL source, URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void link(String source, URL target, int flags)
            throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void move(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        ((NSDirectoryWrapper)wrapper).copy(source, target, flags);
        ((NSDirectoryWrapper)wrapper).remove(source);
    }

    public void move(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        ((NSDirectoryWrapper)wrapper).copy(source, target, flags);
        ((NSDirectoryWrapper)wrapper).remove(source);
    }
    
    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        getWrapper().copy(target, flags);
        getWrapper().remove();
    }

    public void permissionsAllow(URL target, String id, int permissions,
            int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void permissionsAllow(String target, String id, int permissions,
            int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void permissionsDeny(URL target, String id, int permissions,
            int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void permissionsDeny(String target, String id, int permissions,
            int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public URL readLink(URL name) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void remove(URL target, int flags) throws NotImplementedException {
        throw new NotImplementedException();
    }

    public void remove(String target, int flags)
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
