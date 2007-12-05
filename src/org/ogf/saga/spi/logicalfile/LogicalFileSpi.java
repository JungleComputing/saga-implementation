package org.ogf.saga.spi.logicalfile;

import java.util.List;

import org.ogf.saga.URL;
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
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.logicalfile.LogicalFileWrapper;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.spi.namespace.NSEntrySpi;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class LogicalFileSpi extends NSEntrySpi implements
        LogicalFileSpiInterface {
    
    protected LogicalFileAttributes attributes;
    protected int logicalFileFlags;
    
    private static int checkFlags(int flags) throws BadParameter {
        int allowed = Flags.ALLNAMESPACEFLAGS.getValue() | Flags.ALLLOGICALFILEFLAGS.getValue();
        if ((flags | allowed) != allowed) {
            throw new BadParameter("Illegal flags for logical file: " + flags);
        }
        return flags;
    }

    public LogicalFileSpi(LogicalFileWrapper wrapper, Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, BadParameter, DoesNotExist,
            PermissionDenied, AuthorizationFailed, AuthenticationFailed,
            Timeout, NoSuccess, AlreadyExists {
        super(wrapper, session, name, checkFlags(flags) & Flags.ALLNAMESPACEFLAGS.getValue());
        logicalFileFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
        attributes = new LogicalFileAttributes(wrapper, session, true);
    }

    public void addAttribute(String name, AttributeType type, boolean vector,
             boolean readOnly, boolean notImplemented, boolean removeable) {
        attributes.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }

    public String[] findAttributes(String... patterns) throws NotImplemented,
            BadParameter, AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            Timeout, NoSuccess {
        return attributes.findAttributes(patterns);
    }

    public String getAttribute(String key) throws NotImplemented, AuthenticationFailed,
    AuthorizationFailed, PermissionDenied, IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return attributes.getAttribute(key);
    }

    public String[] getVectorAttribute(String key) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState,
            DoesNotExist, Timeout, NoSuccess {
        return attributes.getVectorAttribute(key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return attributes.isReadOnlyAttribute(key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return attributes.isRemovableAttribute(key);
    }

    public boolean isVectorAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return attributes.isVectorAttribute(key);
    }

    public boolean isWritableAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        return attributes.isWritableAttribute(key);
    }

    public String[] listAttributes() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return attributes.listAttributes();
    }

    public void removeAttribute(String key) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, DoesNotExist, Timeout, NoSuccess {
        attributes.removeAttribute(key);
    }

    public void setAttribute(String key, String value) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        attributes.setAttribute(key, value);
    }

    public void setVectorAttribute(String key, String[] values) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, BadParameter, DoesNotExist, Timeout, NoSuccess {
        attributes.setVectorAttribute(key, values);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns) throws NotImplemented {
        return attributes.findAttributes(mode, patterns);
    }

    public Task<String> getAttribute(TaskMode mode, String key) throws NotImplemented {
        return attributes.getAttribute(mode, key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key) throws NotImplemented {
        return attributes.getVectorAttribute(mode, key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key) throws NotImplemented {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key) throws NotImplemented {
        return attributes.isRemovableAttribute(mode, key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key) throws NotImplemented {
        return attributes.isVectorAttribute(mode, key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key) throws NotImplemented {
        return attributes.isWritableAttribute(mode, key);
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplemented {
        return attributes.listAttributes(mode);
    }

    public Task removeAttribute(TaskMode mode, String key) throws NotImplemented {
        return attributes.removeAttribute(mode, key);
    }

    public Task setAttribute(TaskMode mode, String key, String value) throws NotImplemented {
        return attributes.setAttribute(mode, key, value);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values) throws NotImplemented {
        return attributes.setVectorAttribute(mode, key, values);
    }

    public Task addLocation(TaskMode mode, URL name) throws NotImplemented {  
        return new org.ogf.saga.impl.task.Task(
                wrapper, session, mode, "addLocation",
                new Class[] { URL.class },
                name);
    }

    public Task<List<URL>> listLocations(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<URL>>(
                wrapper, session, mode, "listLocations",
                new Class[] { });
    }

    public Task removeLocation(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(
                wrapper, session, mode, "removeLocation",
                new Class[] { URL.class },
                name);
    }

    public Task replicate(TaskMode mode, URL name, int flags) throws NotImplemented { 
        return new org.ogf.saga.impl.task.Task(
            wrapper, session, mode, "replicate",
            new Class[] { URL.class, Integer.TYPE },
            name, flags);
    }

    public Task updateLocation(TaskMode mode, URL nameOld, URL nameNew) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(
                wrapper, session, mode, "updateLocation",
                new Class[] { URL.class, URL.class },
                nameOld, nameNew);
    }
}
