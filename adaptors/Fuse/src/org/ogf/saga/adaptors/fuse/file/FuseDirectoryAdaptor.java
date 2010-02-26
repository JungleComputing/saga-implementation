package org.ogf.saga.adaptors.fuse.file;

import java.util.List;

import org.ogf.saga.adaptors.fuse.AutoMounter;
import org.ogf.saga.adaptors.fuse.namespace.FuseNSDirectoryAdaptor;
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
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.DirectoryWrapper;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.spi.file.DirectoryAdaptorBase;
import org.ogf.saga.url.URL;

public class FuseDirectoryAdaptor extends DirectoryAdaptorBase {

    private FuseNSDirectoryAdaptor dir;
    private Directory delegate;

    public FuseDirectoryAdaptor(DirectoryWrapper wrapper, SessionImpl session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);

        dir = new FuseNSDirectoryAdaptor(null, session, name, flags
                & Flags.ALLNAMESPACEFLAGS.getValue(), false);

        AutoMounter auto = AutoMounter.getInstance();
        String mountId = dir.getMountId();
        URL localUrl = auto.resolveLocalURL(mountId, getEntryURL());
        delegate = FileFactory.createDirectory(session, localUrl, flags);

        dir.setNSDirectoryDelegate(delegate);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FuseDirectoryAdaptor clone = (FuseDirectoryAdaptor) super.clone();
        clone.dir = (FuseNSDirectoryAdaptor) dir.clone();
        clone.dir.setWrapper(clone.wrapper);
        clone.delegate = (Directory)delegate.clone();
        return clone;
    }
    
    @Override
    public void setWrapper(NSDirectoryWrapper wrapper) {
        super.setWrapper(wrapper);
        dir.setWrapper(wrapper);
    }
    
    @Override
    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        if (isClosed()) {
            return;
        }
        
        super.close(timeoutInSeconds);
        dir.close(timeoutInSeconds);
        delegate.close(timeoutInSeconds);
    }
    
    @Override
    public List<URL> listCurrentDir(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        return dir.listCurrentDir(flags);
    }

    @Override
    public List<URL> find(String pattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return dir.find(pattern, flags);
    }
    
    @Override
    public long getSize(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, name);

        AutoMounter auto = AutoMounter.getInstance();
        String mountId = dir.getMountId();
        URL local = auto.resolveLocalURL(entryUrl, name, mountId, sessionImpl);

        return delegate.getSize(local, flags);
    }

    @Override
    public boolean isFile(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        URL entryUrl = getEntryURL();
        URLUtil.checkNotLocal(entryUrl, name);

        AutoMounter auto = AutoMounter.getInstance();
        String mountId = dir.getMountId();
        URL local = auto.resolveLocalURL(entryUrl, name, mountId, sessionImpl);
        
        return delegate.isFile(local);
    }

    @Override
    public void changeDir(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.changeDir(name);
        setEntryURL(dir.getURL());
    }

    @Override
    public void copy(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.copy(source, target, flags);
    }

    @Override
    public void copy(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.copy(source, target, flags);
    }

    @Override
    public boolean exists(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        return dir.exists(name);
    }

    @Override
    public URL getEntry(int entry) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return dir.getEntry(entry);
    }

    @Override
    public int getNumEntries() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return dir.getNumEntries();
    }

    @Override
    public boolean isDir(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return dir.isDir(name);
    }

    @Override
    public boolean isEntry(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return dir.isEntry(name);
    }

    @Override
    public boolean isLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return dir.isLink(name);
    }

    @Override
    public void link(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.link(source, target, flags);
    }

    @Override
    public void link(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.link(source, target, flags);
    }

    @Override
    public void move(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.move(source, target, flags);
    }

    @Override
    public void move(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.move(source, target, flags);
    }

    @Override
    public void permissionsAllow(URL name, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        dir.permissionsAllow(name, id, perms, flags);
    }

    @Override
    public void permissionsAllow(String name, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        dir.permissionsAllow(name, id, perms, flags);
    }

    @Override
    public void permissionsDeny(URL name, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException, IncorrectStateException {
        dir.permissionsDeny(name, id, perms, flags);
    }

    @Override
    public void permissionsDeny(String name, String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException, IncorrectStateException {
        dir.permissionsDeny(name, id, perms, flags);
    }

    @Override
    public URL readLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return dir.readLink(name);
    }

    @Override
    public void remove(URL name, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.remove(name, flags);
    }

    @Override
    public void remove(String name, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        dir.remove(name, flags);
    }

    @Override
    public void copy(URL name, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        dir.copy(name, flags);
    }

    @Override
    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return dir.isDir();
    }

    @Override
    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return dir.isEntry();
    }

    @Override
    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return dir.isLink();
    }

    @Override
    public void link(URL name, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        dir.link(name, flags);
    }

    @Override
    public void move(URL name, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        dir.move(name, flags);
    }

    @Override
    public void permissionsAllow(String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        dir.permissionsAllow(id, perms, flags);
    }

    @Override
    public void permissionsDeny(String id, int perms, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        dir.permissionsDeny(id, perms, flags);
    }

    @Override
    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return dir.readLink();
    }

    @Override
    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        dir.remove(flags);
    }

    @Override
    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return dir.getGroup();
    }

    @Override
    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return dir.getOwner();
    }

    @Override
    public boolean permissionsCheck(String id, int perms)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return dir.permissionsCheck(id, perms);
    }

}
