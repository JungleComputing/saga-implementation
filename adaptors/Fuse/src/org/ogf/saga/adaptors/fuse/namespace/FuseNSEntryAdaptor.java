package org.ogf.saga.adaptors.fuse.namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ogf.saga.adaptors.fuse.AutoMounter;
import org.ogf.saga.adaptors.fuse.util.URLUtil;
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
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.spi.namespace.NSEntryAdaptorBase;
import org.ogf.saga.url.URL;

public class FuseNSEntryAdaptor extends NSEntryAdaptorBase {

    private NSEntry delegate;
    private String mountId;

    private Logger logger = LoggerFactory.getLogger(FuseNSEntryAdaptor.class);

    public FuseNSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        this(wrapper, session, name, flags, true);
    }

    public FuseNSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl session, URL name,
            int flags, boolean createDelegate) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);

        AutoMounter auto = AutoMounter.getInstance();
        URL entryUrl = getEntryURL();
        auto.validateUrl(entryUrl);
        mountId = auto.mount(entryUrl, session);
        
        if (createDelegate) {
            URL localURL = auto.resolveLocalURL(mountId, entryUrl);
            logger.debug("Creating local delegate with URL " + localURL);
            delegate = NSFactory.createNSEntry(session, localURL, flags);
        }
    }

    public void setNSEntryDelegate(NSEntry delegate) {
        this.delegate = delegate;
    }

    public String getMountId() {
        return mountId;
    }
    
    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, target);
        
        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, target, mountId, sessionImpl);
        
        delegate.copy(resolved, flags);
    }

    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.isDir();
    }

    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.isEntry();
    }

    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.isLink();
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException, DoesNotExistException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, target);
        
        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);

        delegate.link(resolved, flags);
    }

    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, target);
        
        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);
        
        delegate.move(resolved, flags);
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.permissionsAllow(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.permissionsDeny(id, permissions, flags);
    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl);

        return delegate.readLink();

//        URL local = delegate.readLink();
//        
//        try {
//            return URLUtil.createRelativeURL(entryUrl, local);
//        } catch (BadParameterException e) {
//            throw new NoSuccessException("could not resolve remote URL: "
//                    + e.getMessage());
//        }
    }

    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.remove(flags);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.getGroup();
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.getOwner();
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.permissionsCheck(id, permissions);
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        super.close(timeoutInSeconds);
        delegate.close(timeoutInSeconds);
    }

}
