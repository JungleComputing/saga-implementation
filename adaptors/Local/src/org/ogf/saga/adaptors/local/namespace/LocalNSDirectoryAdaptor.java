package org.ogf.saga.adaptors.local.namespace;

import java.io.File;
import java.util.List;

import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.LocalAdaptorTool;
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
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.spi.namespace.NSDirectoryAdaptorBase;
import org.ogf.saga.url.URL;

public class LocalNSDirectoryAdaptor extends NSDirectoryAdaptorBase {

    protected static enum OPERATION { COPY, MOVE };
    
    protected LocalNSEntryAdaptor entry;
    
    public LocalNSDirectoryAdaptor(NSDirectoryWrapper wrapper, SessionImpl session,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        this(wrapper, session, name, flags, LocalAdaptorTool.getInstance());
    }
    
    public LocalNSDirectoryAdaptor(NSDirectoryWrapper wrapper, SessionImpl session,
            URL name, int flags, AdaptorTool tool) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, flags);
        entry = createNSEntryAdaptor(session, name, flags, true, tool); 
    }
    
    public AdaptorTool getAdaptorTool() {
        return entry.tool;
    }
    
    protected LocalNSEntryAdaptor createNSEntryAdaptor(SessionImpl session,
            URL name, int flags, boolean isDir, AdaptorTool factory)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        return new LocalNSEntryAdaptor(null, session, name, flags, isDir,
                factory);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        LocalNSDirectoryAdaptor clone = (LocalNSDirectoryAdaptor) super.clone();
        clone.entry = (LocalNSEntryAdaptor) entry.clone();
        clone.entry.setWrapper(clone.wrapper);
        return clone;
    }

    @Override
    public void setWrapper(NSDirectoryWrapper wrapper) {
        super.setWrapper(wrapper);
        entry.setWrapper(wrapper);
    }

    @Override
    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        super.close(timeoutInSeconds);
        entry.close(timeoutInSeconds);
    }
    
    @Override
    public List<URL> listCurrentDir(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            IncorrectURLException {

        String[] entries = entry.file.list();
        
        if (entries == null) {
            throw new IncorrectStateException("Not a directory: " + entry.file);
        }
        
        return convertToRelativeURLs(entries);
    }

    public void changeDir(URL dir) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {

        entry.tool.checkURL(dir);
        
        File newCwd = entry.resolve(dir.getPath());
        
        if (!newCwd.exists()) {
            throw new DoesNotExistException(dir.toString());
        }
        if (!newCwd.isDirectory()) {
            throw new DoesNotExistException(dir.toString()
                    + " is not a directory");
        }
        
        URL normalized = resolveToDir(dir.normalize());
        setEntryURL(normalized);
        entry.init(newCwd, normalized);
    }

    public void copy(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        performOperation(OPERATION.COPY, source, target, flags);
    }
    
    public void move(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        performOperation(OPERATION.MOVE, source, target, flags);
    }
    
    void performOperation(OPERATION operation, URL source, URL target,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {

        URL resolvedSource = resolveToDir(source);
        URL resolvedTarget = resolveToDir(target);

        LocalNSEntryAdaptor sourceEntry = createNSEntryAdaptor(sessionImpl,
                resolvedSource, Flags.NONE.getValue(), isDir(source),
                entry.tool);

        entry.tool.checkURL(resolvedTarget);
        File targetFile = sourceEntry.resolve(resolvedTarget.getPath());
        NSEntryData targetData = new NSEntryData(resolvedTarget, targetFile);
        
        switch(operation) {
        case COPY:
            sourceEntry.nonResolvingCopy(targetData, flags);
            break;
        case MOVE:
            sourceEntry.nonResolvingMove(targetData, flags);
            break;
        }
        
        sourceEntry.close(0);
    }
    
    public void copy(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        performOperation(OPERATION.COPY, source, target, flags);
    }
    
    public void move(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        performOperation(OPERATION.MOVE, source, target, flags);
    }

    void performOperation(OPERATION operation, String source, URL target,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {

        entry.tool.checkURL(target);

        List<URL> sources = expandWildCards(source);
        target = resolveToDir(target);
        File targetFile = entry.tool.createFile(target.getPath());

        if (sources.size() > 1) {
            // target must exist and be a directory
            if (!targetFile.isDirectory()) {
                throw new BadParameterException("Source expands to more "
                        + "than one file and target is not a directory");
            }
        } else if (sources.isEmpty()) {
            throw new DoesNotExistException("Source does not exist: " + source);
        }

        for (URL sourceUrl : sources) {
            URL resolvedUrl = resolveToDir(sourceUrl);
            LocalNSEntryAdaptor sourceEntry = createNSEntryAdaptor(sessionImpl,
                    resolvedUrl, Flags.NONE.getValue(), isDir(sourceUrl),
                    entry.tool);

            NSEntryData targetData = new NSEntryData(target, targetFile);
            
            switch(operation) {
            case COPY:
                sourceEntry.nonResolvingCopy(targetData, flags);
                break;
            case MOVE:
                sourceEntry.nonResolvingMove(targetData, flags);
                break;
            }
            
            sourceEntry.close(0);
        }
    }
    
    public boolean exists(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {

        entry.tool.checkURL(name);
        
        URL resolved = resolveToDir(name);
        File resolvedFile = entry.tool.createFile(resolved.getPath());
        
        return resolvedFile.exists();
    }

    public URL getEntry(int num) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {

        if (num < 0) {
            throw new DoesNotExistException("Invalid index: " + num);
        }
        
        String[] list = entry.file.list();

        if (list == null) {
            throw new IncorrectStateException("Not a directory: " + entry.file);
        }
        if (num >= list.length) {
            throw new DoesNotExistException("Invalid index: " + num);
        }
        
        try {
            return convertToRelativeURL(list[num]);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Could not create URL for entry: " 
                    + list[num], e);
        }
    }

    public int getNumEntries() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {

        String[] list = entry.file.list();

        if (list == null) {
            throw new IncorrectStateException("Not a directory: " + entry.file);
        }
        
        return list.length;
    }

    public boolean isDir(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        File f = createExistingRelativeEntryFile(name);
        return f.isDirectory();
    }

    public boolean isEntry(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        File f = createExistingRelativeEntryFile(name);
        return f.isFile();
    }

    private File createExistingRelativeEntryFile(URL name)
            throws IncorrectStateException, IncorrectURLException,
            NotImplementedException, NoSuccessException, BadParameterException,
            DoesNotExistException {

        entry.tool.checkURL(name);

        URL resolved = resolveToDir(name);

        File f = entry.tool.createFile(resolved.getPath());

        if (!f.exists()) {
            throw new DoesNotExistException(name.toString());
        }

        return f;
    }
    
    public boolean isLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Links are not supported");
    }

    public void link(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Links are not supported");
    }

    public void link(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Links are not supported");
    }

    public void permissionsAllow(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        throw new NotImplementedException("Permissions are not supported");
    }

    public void permissionsAllow(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        throw new NotImplementedException("Permissions are not supported");
    }

    public void permissionsDeny(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException, IncorrectStateException {
        throw new NotImplementedException("Permissions are not supported");
    }

    public void permissionsDeny(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException, IncorrectURLException, IncorrectStateException {
        throw new NotImplementedException("Permissions are not supported");
    }

    public URL readLink(URL name) throws NotImplementedException,
            IncorrectURLException, DoesNotExistException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Links are not supported");
    }

    public void remove(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {

        URL resolved = resolveToDir(target);

        LocalNSEntryAdaptor targetEntry = null;
        try {
            targetEntry = createNSEntryAdaptor(sessionImpl, resolved,
                    Flags.NONE.getValue(), isDir(target), entry.tool);
        } catch (AlreadyExistsException e) {
            // cannot happen because the 'Create' flag is not set
            throw new NoSuccessException("Internal error", e);
        }

        targetEntry.remove(flags);
        targetEntry.close(0);
    }

    public void remove(String target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
 
        List<URL> targets = expandWildCards(target);

        if (targets.size() < 1) {
            throw new DoesNotExistException(target);
        }

        for (URL u : targets) {
            LocalNSEntryAdaptor targetEntry = null;
            URL resolved = resolveToDir(u);
            try {
                targetEntry = createNSEntryAdaptor(sessionImpl, resolved,
                        Flags.NONE.getValue(), isDir(u), entry.tool);
            } catch (AlreadyExistsException e) {
                // cannot happen because 'Create' flag is not set
                throw new NoSuccessException("Internal error", e);
            }

            targetEntry.remove(flags);
        }
    }

    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        entry.copy(target, flags);
    }

    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.isDir();
    }

    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.isEntry();
    }

    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.isLink();
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        entry.link(target, flags);
    }

    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        entry.move(target, flags);
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsAllow(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsDeny(id, permissions, flags);
    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return entry.readLink();
    }

    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        entry.remove(flags);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getGroup();
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getOwner();
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return entry.permissionsCheck(id, permissions);
    }

}
