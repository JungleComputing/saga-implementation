package org.ogf.saga.adaptors.javaGAT.logicalfile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.gridlab.gat.GAT;
import org.gridlab.gat.URI;
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
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.proxies.logicalfile.LogicalFileWrapper;
import org.ogf.saga.spi.logicalfile.LogicalFileAdaptorBase;

public class LogicalFileAdaptor extends LogicalFileAdaptorBase {
    
    // TODO: clone
    
    private FileEntry entry;
    
    private org.gridlab.gat.io.LogicalFile gatLogicalFile;
    private HashSet<URL> urls = new HashSet<URL>();
    
    public LogicalFileAdaptor(LogicalFileWrapper wrapper, Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException, BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);
        entry = new FileEntry(session, name, flags & Flags.ALLNAMESPACEFLAGS.getValue());
       
        try {
            gatLogicalFile = GAT.createLogicalFile(entry.getGatContext(),
                    entry.getGatURI().toString(),
                    org.gridlab.gat.io.LogicalFile.OPEN);
        } catch (Throwable e) {
            throw new NoSuccessException("GAT.createLogicalFile threw exceptio", e);
        }
        
        try {
            List<URI> uris = gatLogicalFile.getURIs();
            for (URI uri : uris) {
                urls.add(entry.cvtToSagaURL(uri));
            }
        } catch (Throwable e) {
            throw new NoSuccessException("getURIs threw exception", e);
        }       
    }
    
    public Object clone() throws CloneNotSupportedException {
        LogicalFileAdaptor clone = (LogicalFileAdaptor) super.clone();
        clone.entry = (FileEntry) entry.clone();
        clone.entry.setWrapper(clone.wrapper);
        return clone;
    }
    
    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        entry.close(timeoutInSeconds);
        super.close(timeoutInSeconds);
    }

    public synchronized void addLocation(URL name) throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("addLocation() called on closed LogicalFile");
        }
        if (! Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException("addLocation() called on LogicalFile not opened for writing");
        }
        
        name = name.normalize();
        if (urls.contains(name)) {
            // Should not throw AlreadyExists. Ignore, silently.
            return;
        }
        URI uri = FileEntry.cvtToGatURI(name);
        try {
            gatLogicalFile.addURI(uri);
        } catch (Throwable e) {
            throw new NoSuccessException("GAT failure: ", e);
        }
        urls.add(name);
    }

    public synchronized List<URL> listLocations() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("listLocations() called on closed LogicalFile");
        }
        if (! Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException("listLocations() called on LogicalFile not opened for reading");
        }
        return new ArrayList<URL>(urls);
    }

    public synchronized void removeLocation(URL name) throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("removeLocation() called on closed LogicalFile");
        }
        if (! Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException("removeLocation() called on LogicalFile not opened for writing");
        }
        
        name = name.normalize();
        if (! urls.contains(name)) {
            throw new DoesNotExistException("url " + name + " not found");
        }
        URI uri = FileEntry.cvtToGatURI(name);
        try {
            gatLogicalFile.removeURI(uri);
        } catch (Throwable e) {
            throw new NoSuccessException("GAT failure: ", e);
        }
        urls.remove(name);        
    }

    public synchronized void replicate(URL name, int flags) throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("removeLocation() called on closed LogicalFile");
        }
        if (! Flags.WRITE.isSet(logicalFileFlags) || ! Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException("replicate() called on LogicalFile not opened for reading/writing");
        }
        if (Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException("replicate() call with RECURSIVE flag");
        }
        
        name = name.normalize();
        
        if (urls.contains(name)) {
            // ignore silently
            return;
        }
        
        URL[] array = urls.toArray(new URL[urls.size()]);
        if (array.length == 0) {
            throw new IncorrectStateException("replicate() called on empty LogicalFile");
        }
        
        // Create directory entry so that we can copy.
        // TODO: what if this fails?
        NSDirectory dir = NSFactory.createNSDirectory(
                session, getCWD(), Flags.NONE.getValue());
        
        // Pick the first location. Exceptions passed on to user.
        // If this fails, the location is not added.
        dir.copy(array[0], name, flags);
        dir.close();
        
        addLocation(name);
        
        // alternatively, use the gat replicate. But then we need to check flags
        // first, for instance by creating a NSEntry.
    }

    public synchronized void updateLocation(URL nameOld, URL nameNew) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("removeLocation() called on closed LogicalFile");
        }
        if (! Flags.WRITE.isSet(logicalFileFlags) || ! Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException("updateLocation() called on LogicalFile not opened for reading/writing");
        }

        nameNew = nameNew.normalize();
        
        if (urls.contains(nameNew)) {
            throw new AlreadyExistsException("URL " + nameNew + " already exists in LogicalFile");
        }
        
        removeLocation(nameOld);
        addLocation(nameNew);
    }

    @Override
    protected void nonResolvingCopy(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException, IncorrectURLException,
            NotImplementedException {
        entry.nonResolvingCopy(target, flags);
    }

    @Override
    protected void nonResolvingMove(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException, NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, IncorrectURLException {
        entry.nonResolvingMove(target, flags);
    }

    public boolean isDir() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
         return entry.isDir();
    }

    public boolean isEntry() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isEntry();
    }

    public boolean isLink() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isLink();
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        entry.link(target, flags);
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException, BadParameterException, TimeoutException, NoSuccessException {
        entry.permissionsAllow(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        entry.permissionsDeny(id, permissions, flags);
    }

    public URL readLink() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.readLink();
    }

    public void remove(int flags) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        entry.remove(flags); 
    }

    public String getGroup() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getGroup();
    }

    public String getOwner() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getOwner();
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException, NoSuccessException {
        return entry.permissionsCheck(id, permissions);
    }
}
