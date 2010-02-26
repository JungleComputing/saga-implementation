package org.ogf.saga.adaptors.fuse.file;

import java.util.List;

import org.ogf.saga.adaptors.fuse.AutoMounter;
import org.ogf.saga.adaptors.fuse.namespace.FuseNSEntryAdaptor;
import org.ogf.saga.adaptors.fuse.util.URLUtil;
import org.ogf.saga.buffer.Buffer;
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
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.SeekMode;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.file.FileWrapper;
import org.ogf.saga.spi.file.FileAdaptorBase;
import org.ogf.saga.url.URL;

public class FuseFileAdaptor extends FileAdaptorBase {

    private FuseNSEntryAdaptor fileEntry;
    private File delegate;

    public FuseFileAdaptor(FileWrapper wrapper, SessionImpl session, URL name,
            int flags) throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);

        fileEntry = new FuseNSEntryAdaptor(null, session, name, flags
                & Flags.ALLNAMESPACEFLAGS.getValue(), false);

        AutoMounter auto = AutoMounter.getInstance();
        String mountId = fileEntry.getMountId();
        URL localUrl = auto.resolveLocalURL(mountId, getEntryURL());
        delegate = FileFactory.createFile(localUrl, flags);

        fileEntry.setNSEntryDelegate(delegate);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FuseFileAdaptor clone = (FuseFileAdaptor) super.clone();
        clone.fileEntry = (FuseNSEntryAdaptor) fileEntry.clone();
        clone.fileEntry.setWrapper(clone.wrapper);
        clone.delegate = (File)delegate.clone();
        return clone;
    }
    
    @Override
    public void close(float timeoutInSeconds) throws NotImplementedException,
            NoSuccessException {
        if (isClosed()) {
            return;
        }
        
        super.close(timeoutInSeconds);
        fileEntry.close(timeoutInSeconds);
        delegate.close(timeoutInSeconds);
    }
    
    @Override
    public void copy(URL u, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        fileEntry.copy(u, flags);
    }

    @Override
    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return fileEntry.isDir();
    }

    @Override
    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return fileEntry.isEntry();
    }

    @Override
    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return fileEntry.isLink();
    }

    @Override
    public void link(URL u, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        fileEntry.link(u, flags);
    }

    @Override
    public void move(URL u, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        fileEntry.move(u, flags);
    }

    @Override
    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        fileEntry.permissionsAllow(id, permissions, flags);
    }

    @Override
    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        fileEntry.permissionsDeny(id, permissions, flags);
    }

    @Override
    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return fileEntry.readLink();
    }

    @Override
    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        fileEntry.remove(flags);
    }

    @Override
    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return fileEntry.getGroup();
    }

    @Override
    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return fileEntry.getOwner();
    }

    @Override
    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return fileEntry.permissionsCheck(id, permissions);
    }

    @Override
    public long getSize() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.getSize();
    }

    @Override
    public List<String> modesE() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.modesE();
    }

    @Override
    public int read(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.read(buffer, offset, len);
    }

    @Override
    public int readE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.readE(emode, spec, buffer);
    }

    @Override
    public long seek(long offset, SeekMode whence)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.seek(offset, whence);
    }

    @Override
    public int sizeE(String emode, String spec) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.sizeE(emode, spec);
    }

    @Override
    public int write(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.write(buffer, offset, len);
    }

    @Override
    public int writeE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException {
        URLUtil.checkNotLocal(getEntryURL());
        return delegate.writeE(emode, spec, buffer);
    }

}
