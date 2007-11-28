package org.ogf.saga.spi.logicalfile;

import java.util.ArrayList;
import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.attributes.Attributes;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.logicalfile.LogicalDirectory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.spi.namespace.NSDirectorySpi;

public abstract class LogicalDirectorySpi extends NSDirectorySpi implements
        LogicalDirectorySpiInterface {
    
    private LogicalFileAttributes attributes;  
    private int logicalFileFlags;
    
    private static int checkFlags(int flags) throws BadParameter {
        int allowed = Flags.ALLNAMESPACEFLAGS.getValue()
                | Flags.ALLLOGICALFILEFLAGS.getValue();
        if ((flags | allowed) != allowed) {
            throw new BadParameter("Illegal flags for logical directory: " + flags);
        }
        return flags;
    }

    public LogicalDirectorySpi(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, BadParameter, DoesNotExist,
            PermissionDenied, AuthorizationFailed, AuthenticationFailed,
            Timeout, NoSuccess, AlreadyExists {
        super(session, name, checkFlags(flags) & Flags.ALLNAMESPACEFLAGS.getValue());
        attributes = new LogicalFileAttributes(session, true);
        logicalFileFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
    }

    public List<URL> find(String namePattern, String[] attrPattern, int flags)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess {

        checkClosed();
        
        if (! Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDenied("find() on logicalDirectory not opened for reading");
        }
        
        // First, find the list of URLs that match the name pattern.
        List<URL> candidates = find(namePattern, flags);
        
        ArrayList<URL> newCandidates = new ArrayList<URL>();
              
        // Now match the attributes. A candidate must match all attribute patterns.
        for (URL url : candidates) {
            try {
                boolean matches = true;
                Attributes a;
                if (isFile(url)) {
                    a = openLogicalFile(url, Flags.NONE.getValue());
                } else {
                    a = openLogicalDir(url, Flags.NONE.getValue());
                }
                for (String s : attrPattern) {
                    if (a.findAttributes(s).length == 0) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    newCandidates.add(url);
                }
            } catch (Throwable e) {
                // ignore ???
            }

        }
        return newCandidates;
    }

    public Task<List<URL>> find(TaskMode mode, String namePattern,
            String[] attrPattern, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<URL>>(
                this, session, mode, "find",
                new Class[] {String.class, String[].class, Integer.TYPE },
                namePattern, attrPattern, flags);
    }

    public LogicalDirectory openLogicalDir(URL name,
            int flags) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        checkClosed();
        
        if (Flags.CREATE.isSet(flags) && ! Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDenied("openLogicalDir with CREATE flag "
                    + "on logicalDirectory not opened for writing");
        }
        name = resolve(name);
        return LogicalFileFactory.createLogicalDirectory(session, name, flags);
    }

    public Task<LogicalDirectory> openLogicalDir(
            TaskMode mode, URL name, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<LogicalDirectory>(
                this, session, mode, "openLogicalDir",
                new Class[] {URL.class, Integer.TYPE },
                name, flags);
    }

    public LogicalFile openLogicalFile(URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {

        checkClosed();
        
        if (Flags.CREATE.isSet(flags) && ! Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDenied("openLogicalFile with CREATE flag on "
                    + "logicalDirectory not opened for writing");
        }
        
        name = resolve(name);
        return LogicalFileFactory.createLogicalFile(session, name, flags);
    }

    public Task<LogicalFile> openLogicalFile(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<LogicalFile>(
                this, session, mode, "openLogicalFile",
                new Class[] {URL.class, Integer.TYPE },
                name, flags);
    }

    public boolean isFile(URL name) throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, DoesNotExist {
        return isEntry(name);
    }

    public Task<Boolean> isFile(TaskMode mode, URL name) throws NotImplemented {
        return isEntry(mode, name);
    }

    public String[] findAttributes(String... patterns) throws NotImplemented,
            BadParameter, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, Timeout, NoSuccess {
        return attributes.findAttributes(patterns);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns)
            throws NotImplemented {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getAttribute(key);
    }

    public Task<String> getAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getVectorAttribute(key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isReadOnlyAttribute(key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isRemovableAttribute(key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isVectorAttribute(key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.isWritableAttribute(key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return attributes.listAttributes();
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplemented {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        attributes.removeAttribute(key);
    }

    public Task removeAttribute(TaskMode mode, String key)
            throws NotImplemented {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        attributes.setAttribute(key, value);
    }

    public Task setAttribute(TaskMode mode, String key, String value)
            throws NotImplemented {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, IncorrectState, BadParameter, DoesNotExist,
            Timeout, NoSuccess {
        attributes.setVectorAttribute(key, values);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values)
            throws NotImplemented {
        return attributes.setVectorAttribute(mode, key, values);
    }
}
