package org.ogf.saga.adaptors.fuse.namespace;

import java.util.List;

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
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.spi.namespace.NSDirectoryAdaptorBase;
import org.ogf.saga.url.URL;

public class FuseNSDirectoryAdaptor extends NSDirectoryAdaptorBase {
	
	private NSDirectory delegate;
    private String mountId;
    
    public FuseNSDirectoryAdaptor(NSDirectoryWrapper wrapper, SessionImpl session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        this(wrapper, session, name, flags, true);
    }

    public FuseNSDirectoryAdaptor(NSDirectoryWrapper wrapper, SessionImpl session,
            URL name, int flags, boolean createDelegate)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);

        AutoMounter auto = AutoMounter.getInstance();
        URL entryUrl = getEntryURL();
        auto.validateUrl(entryUrl);
        mountId = auto.mount(entryUrl, session);
        
        if (createDelegate) {
            URL localUrl = auto.resolveLocalURL(mountId, entryUrl);
            try {
                NSDirectory delegate = NSFactory.createNSDirectory(session,
                        localUrl, flags);
                setNSDirectoryDelegate(delegate);
            } catch (DoesNotExistException e) {
                // throw an identical exception, but with the given URL instead
                // of the internal delegate URL in the message
                throw new DoesNotExistException(name + " does not exist");
            }
        }
    }

    public void setNSDirectoryDelegate(NSDirectory delegate) {
        this.delegate = delegate;
    }

    public String getMountId() {
        return mountId;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        FuseNSDirectoryAdaptor clone = (FuseNSDirectoryAdaptor) super.clone();
        clone.delegate = (NSDirectory) delegate.clone();
        return clone;
    }

    @Override
    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        super.close(timeoutInSeconds);
        delegate.close(timeoutInSeconds);
    }
    
    @Override
    public List<URL> listCurrentDir(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl);

        return delegate.list(flags);
    }

    @Override
    public List<URL> find(String pattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl);

        return delegate.find(pattern, flags);
    }

    public void changeDir(URL u) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, u);

        AutoMounter auto = AutoMounter.getInstance();
        
        URL resolved = auto.resolveLocalURL(entryUrl, u, mountId, sessionImpl);
        delegate.changeDir(resolved);
        
        URL newCwd = URLUtil.createRelativeURL(entryUrl, delegate.getURL());
        setEntryURL(newCwd);
    }

    public void copy(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, source, target);

        AutoMounter auto = AutoMounter.getInstance();

        URL resolvedSource = auto.resolveLocalURL(entryUrl, source, mountId,
                sessionImpl);
        URL resolvedTarget = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);
        
        delegate.copy(resolvedSource, resolvedTarget, flags);
    }

    public void copy(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, target);

        // The spec says that source MUST be a relative URL, and MAY contain
        // wildcards. So, we do not resolve the source.
        AutoMounter auto = AutoMounter.getInstance();
        URL resolvedTarget = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);
        delegate.copy(source, resolvedTarget, flags);
    }

    public boolean exists(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, name);

        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, name, mountId, sessionImpl);
        return delegate.exists(resolved);
    }

    public URL getEntry(int entry) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl);

        return delegate.getEntry(entry);
    }

    public int getNumEntries() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.getNumEntries();
    }

    public boolean isDir(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, name);
        
        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, name, mountId, sessionImpl);
        
        return delegate.isDir(resolved);
    }

    public boolean isEntry(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, name);
        
        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, name, mountId, sessionImpl);
        
        return delegate.isEntry(resolved);
    }

    public boolean isLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, name);
        
        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, name, mountId, sessionImpl);
        
        return delegate.isLink(resolved);
    }

    public void link(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, source, target);
        
        AutoMounter auto = AutoMounter.getInstance();

        URL resolvedSource = auto.resolveLocalURL(entryUrl, source, mountId,
                sessionImpl);
        URL resolvedTarget = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);
        
        delegate.link(resolvedSource, resolvedTarget, flags);
    }

    public void link(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, target);

        // The spec says that source MUST be a relative URL, and MAY contain
        // wildcards. So, we do not resolve the source.
        AutoMounter auto = AutoMounter.getInstance();
        URL resolvedTarget = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);

        delegate.link(source, resolvedTarget, flags);
    }

    public void move(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, source, target);

        AutoMounter auto = AutoMounter.getInstance();

        URL resolvedSource = auto.resolveLocalURL(entryUrl, source, mountId,
                sessionImpl);
        URL resolvedTarget = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);

        delegate.move(resolvedSource, resolvedTarget, flags);
    }

    public void move(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocalToLocal(entryUrl, target);

        // The spec says that source MUST be a relative URL, and MAY contain
        // wildcards. So, we do not resolve the source.
        AutoMounter auto = AutoMounter.getInstance();
        URL resolvedTarget = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);

        delegate.move(source, resolvedTarget, flags);
    }
    
    public void permissionsAllow(URL target, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, target);

        AutoMounter auto = AutoMounter.getInstance();
        URL resolvedTarget = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);

        delegate.permissionsAllow(resolvedTarget, id, perms, flags);
    }

    public void permissionsAllow(String target, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.permissionsAllow(target, id, perms, flags);
    }

    public void permissionsDeny(URL target, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException, IncorrectStateException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, target);

        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, target, mountId, sessionImpl);
        delegate.permissionsDeny(resolved, id, perms, flags);
    }

    public void permissionsDeny(String target, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException, IncorrectStateException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.permissionsDeny(target, id, perms, flags);
    }

    public URL readLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
    	throw new NotImplementedException("readLink(URL) is not implemented");
    }

    public void remove(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, target);

        AutoMounter auto = AutoMounter.getInstance();
        URL resolved = auto.resolveLocalURL(entryUrl, target, mountId, sessionImpl);
        delegate.remove(resolved, flags);
    }

    public void remove(String target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.remove(target, flags);
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
        URL resolved = auto.resolveLocalURL(entryUrl, target, mountId,
                sessionImpl);
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
            NoSuccessException, IncorrectURLException {
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

    public void permissionsAllow(String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.permissionsAllow(id, perms, flags);
    }

    public void permissionsDeny(String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        delegate.permissionsDeny(id, perms, flags);
    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl);

        return delegate.readLink();
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

    public boolean permissionsCheck(String id, int perms)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.permissionsCheck(id, perms);
    }

}
