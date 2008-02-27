package org.ogf.saga.spi.logicalfile;

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
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.logicalfile.LogicalFileWrapper;
import org.ogf.saga.spi.namespace.NSEntrySpi;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class LogicalFileSpi extends NSEntrySpi implements
        LogicalFileSpiInterface {
    
    protected LogicalFileAttributes attributes;
    protected int logicalFileFlags;
    
    private static int checkFlags(int flags) throws BadParameterException {
        int allowed = Flags.ALLNAMESPACEFLAGS.getValue() | Flags.ALLLOGICALFILEFLAGS.getValue();
        if ((flags | allowed) != allowed) {
            throw new BadParameterException("Illegal flags for logical file: " + flags);
        }
        return flags;
    }

    public LogicalFileSpi(LogicalFileWrapper wrapper, Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException, BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, session, name, checkFlags(flags) & Flags.ALLNAMESPACEFLAGS.getValue());
        logicalFileFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
        attributes = new LogicalFileAttributes(wrapper, session, true);
    }
    
    public Object clone() throws CloneNotSupportedException {
        LogicalFileSpi clone = (LogicalFileSpi) super.clone();
        clone.attributes = (LogicalFileAttributes) attributes.clone();        
        return clone;
    }

    public void addAttribute(String name, AttributeType type, boolean vector,
             boolean readOnly, boolean notImplemented, boolean removeable) {
        attributes.addAttribute(name, type, vector, readOnly, notImplemented, removeable);
    }

    public String[] findAttributes(String... patterns) throws NotImplementedException,
            BadParameterException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return attributes.findAttributes(patterns);
    }

    public String getAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
    AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getAttribute(key);
    }

    public String[] getVectorAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.getVectorAttribute(key);
    }

    public boolean isReadOnlyAttribute(String key) throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isReadOnlyAttribute(key);
    }

    public boolean isRemovableAttribute(String key) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isRemovableAttribute(key);
    }

    public boolean isVectorAttribute(String key) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isVectorAttribute(key);
    }

    public boolean isWritableAttribute(String key) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException, NoSuccessException {
        return attributes.isWritableAttribute(key);
    }

    public String[] listAttributes() throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return attributes.listAttributes();
    }

    public void removeAttribute(String key) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.removeAttribute(key);
    }

    public void setAttribute(String key, String value) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setAttribute(key, value);
    }

    public void setVectorAttribute(String key, String[] values) throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException, IncorrectStateException, BadParameterException, DoesNotExistException, TimeoutException, NoSuccessException {
        attributes.setVectorAttribute(key, values);
    }

    public Task<String[]> findAttributes(TaskMode mode, String... patterns) throws NotImplementedException {
        return attributes.findAttributes(mode, patterns);
    }

    public Task<String> getAttribute(TaskMode mode, String key) throws NotImplementedException {
        return attributes.getAttribute(mode, key);
    }

    public Task<String[]> getVectorAttribute(TaskMode mode, String key) throws NotImplementedException {
        return attributes.getVectorAttribute(mode, key);
    }

    public Task<Boolean> isReadOnlyAttribute(TaskMode mode, String key) throws NotImplementedException {
        return attributes.isReadOnlyAttribute(mode, key);
    }

    public Task<Boolean> isRemovableAttribute(TaskMode mode, String key) throws NotImplementedException {
        return attributes.isRemovableAttribute(mode, key);
    }

    public Task<Boolean> isVectorAttribute(TaskMode mode, String key) throws NotImplementedException {
        return attributes.isVectorAttribute(mode, key);
    }

    public Task<Boolean> isWritableAttribute(TaskMode mode, String key) throws NotImplementedException {
        return attributes.isWritableAttribute(mode, key);
    }

    public Task<String[]> listAttributes(TaskMode mode) throws NotImplementedException {
        return attributes.listAttributes(mode);
    }

    public Task removeAttribute(TaskMode mode, String key) throws NotImplementedException {
        return attributes.removeAttribute(mode, key);
    }

    public Task setAttribute(TaskMode mode, String key, String value) throws NotImplementedException {
        return attributes.setAttribute(mode, key, value);
    }

    public Task setVectorAttribute(TaskMode mode, String key, String[] values) throws NotImplementedException {
        return attributes.setVectorAttribute(mode, key, values);
    }

    public Task addLocation(TaskMode mode, URL name) throws NotImplementedException {  
        return new org.ogf.saga.impl.task.Task(
                wrapper, session, mode, "addLocation",
                new Class[] { URL.class },
                name);
    }

    public Task<List<URL>> listLocations(TaskMode mode) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task<List<URL>>(
                wrapper, session, mode, "listLocations",
                new Class[] { });
    }

    public Task removeLocation(TaskMode mode, URL name) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(
                wrapper, session, mode, "removeLocation",
                new Class[] { URL.class },
                name);
    }

    public Task replicate(TaskMode mode, URL name, int flags) throws NotImplementedException { 
        return new org.ogf.saga.impl.task.Task(
            wrapper, session, mode, "replicate",
            new Class[] { URL.class, Integer.TYPE },
            name, flags);
    }

    public Task updateLocation(TaskMode mode, URL nameOld, URL nameNew) throws NotImplementedException {
        return new org.ogf.saga.impl.task.Task(
                wrapper, session, mode, "updateLocation",
                new Class[] { URL.class, URL.class },
                nameOld, nameNew);
    }
}
