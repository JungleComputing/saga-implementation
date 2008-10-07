package org.ogf.saga.spi.logicalfile;

import java.util.ArrayList;
import java.util.List;

import org.ogf.saga.attributes.Attributes;
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
import org.ogf.saga.logicalfile.LogicalDirectory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.proxies.logicalfile.LogicalDirectoryWrapper;
import org.ogf.saga.spi.namespace.NSDirectoryAdaptorBase;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public abstract class LogicalDirectoryAdaptorBase extends
        NSDirectoryAdaptorBase implements LogicalDirectorySPI {

    private LogicalDirectoryAttributes attributes;
    private int logicalFileFlags;
    protected LogicalDirectoryWrapper wrapper;

    private static int checkFlags(int flags) throws BadParameterException {
        int allowed = Flags.ALLNAMESPACEFLAGS.getValue()
                | Flags.ALLLOGICALFILEFLAGS.getValue();
        if ((flags | allowed) != allowed) {
            throw new BadParameterException(
                    "Illegal flags for logical directory: " + flags);
        }
        return flags;
    }

    public LogicalDirectoryAdaptorBase(LogicalDirectoryWrapper wrapper,
            Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, checkFlags(flags)
                & Flags.ALLNAMESPACEFLAGS.getValue());
        this.wrapper = wrapper;
        attributes = new LogicalDirectoryAttributes(wrapper, session, true);
        logicalFileFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
    }

    public Object clone() throws CloneNotSupportedException {
        LogicalDirectoryAdaptorBase clone = (LogicalDirectoryAdaptorBase) super
                .clone();
        clone.attributes = (LogicalDirectoryAttributes) attributes.clone();
        return clone;
    }

    public List<URL> find(String namePattern, String[] attrPattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {

        checkNotClosed();

        if (!Flags.READ.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "find() on logicalDirectory not opened for reading");
        }

        // First, find the list of URLs that match the name pattern.
        List<URL> candidates = find(namePattern, flags);

        ArrayList<URL> newCandidates = new ArrayList<URL>();

        // Now match the attributes. A candidate must match all attribute
        // patterns.
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

    public Task<LogicalDirectory, List<URL>> find(TaskMode mode,
            String namePattern, String[] attrPattern, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<LogicalDirectory, List<URL>>(
                wrapper, session, mode, "find", new Class[] { String.class,
                        String[].class, Integer.TYPE }, namePattern,
                attrPattern, flags);
    }

    public LogicalDirectory openLogicalDir(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        checkNotClosed();

        if (Flags.CREATE.isSet(flags) && !Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "openLogicalDir with CREATE flag "
                            + "on logicalDirectory not opened for writing");
        }
        name = resolve(name);
        return LogicalFileFactory.createLogicalDirectory(session, name, flags);
    }

    public Task<LogicalDirectory, LogicalDirectory> openLogicalDir(
            TaskMode mode, URL name, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<LogicalDirectory, LogicalDirectory>(
                wrapper, session, mode, "openLogicalDir", new Class[] {
                        URL.class, Integer.TYPE }, name, flags);
    }

    public LogicalFile openLogicalFile(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {

        checkNotClosed();

        if (Flags.CREATE.isSet(flags) && !Flags.WRITE.isSet(logicalFileFlags)) {
            throw new PermissionDeniedException(
                    "openLogicalFile with CREATE flag on "
                            + "logicalDirectory not opened for writing");
        }

        name = resolve(name);
        return LogicalFileFactory.createLogicalFile(session, name, flags);
    }

    public Task<LogicalDirectory, LogicalFile> openLogicalFile(TaskMode mode,
            URL name, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<LogicalDirectory, LogicalFile>(
                wrapper, session, mode, "openLogicalFile", new Class[] {
                        URL.class, Integer.TYPE }, name, flags);
    }

    public boolean isFile(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, DoesNotExistException {
        return isEntry(name);
    }

    public Task<NSDirectory, Boolean> isFile(TaskMode mode, URL name)
            throws NotImplementedException {
        return isEntry(mode, name);
    }

    public String[] findAttributes(String... patterns)
            throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public Task<LogicalDirectory, String[]> findAttributes(TaskMode mode,
            String... patterns) throws NotImplementedException {
        return attributes.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public Task<LogicalDirectory, String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return attributes.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public Task<LogicalDirectory, String[]> getVectorAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }
    
    public Task<LogicalDirectory, Boolean> isReadOnlyAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.isReadOnlyAttribute(mode, key);
    }
    
    public boolean existsAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return attributes.existsAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> existsAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.existsAttribute(mode, key);
    }

    public boolean isRemovableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> isRemovableAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> isVectorAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public Task<LogicalDirectory, Boolean> isWritableAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public Task<LogicalDirectory, String[]> listAttributes(TaskMode mode)
            throws NotImplementedException {
        return attributes.listAttributes(mode);
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        attributes.removeAttribute(key);
    }

    public Task<LogicalDirectory, Void> removeAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return attributes.removeAttribute(mode, key);
    }

    public void setAttribute(String key, String value)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public Task<LogicalDirectory, Void> setAttribute(TaskMode mode, String key,
            String value) throws NotImplementedException {
        return attributes.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public Task<LogicalDirectory, Void> setVectorAttribute(TaskMode mode,
            String key, String[] values) throws NotImplementedException {
        return attributes.setVectorAttribute(mode, key, values);
    }
}
