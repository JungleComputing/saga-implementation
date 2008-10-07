package org.ogf.saga.proxies.logicalfile;

import java.util.List;

import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.logicalfile.LogicalFileSPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public final class LogicalFileWrapper extends NSEntryWrapper implements
        LogicalFile {

    private LogicalFileSPI proxy;

    LogicalFileWrapper(Session session, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        super(session);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (LogicalFileSPI) SAGAEngine.createAdaptorProxy(
                    LogicalFileSPI.class, new Class[] {
                            LogicalFileWrapper.class,
                            org.ogf.saga.impl.session.Session.class, URL.class,
                            Integer.TYPE }, parameters);
            super.setProxy(proxy);
        } catch (org.ogf.saga.error.SagaException e) {
            if (e instanceof NotImplementedException) {
                throw (NotImplementedException) e;
            }
            if (e instanceof IncorrectURLException) {
                throw (IncorrectURLException) e;
            }
            if (e instanceof AuthenticationFailedException) {
                throw (AuthenticationFailedException) e;
            }
            if (e instanceof AuthorizationFailedException) {
                throw (AuthorizationFailedException) e;
            }
            if (e instanceof PermissionDeniedException) {
                throw (PermissionDeniedException) e;
            }
            if (e instanceof BadParameterException) {
                throw (BadParameterException) e;
            }
            if (e instanceof AlreadyExistsException) {
                throw (AlreadyExistsException) e;
            }
            if (e instanceof DoesNotExistException) {
                throw (DoesNotExistException) e;
            }
            if (e instanceof TimeoutException) {
                throw (TimeoutException) e;
            }
            if (e instanceof NoSuccessException) {
                throw (NoSuccessException) e;
            }
            throw new NoSuccessException("Constructor failed", e);
        }
    }

    public Task<LogicalFile, Void> addLocation(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.addLocation(mode, name);
    }

    public void addLocation(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        proxy.addLocation(name);
    }

    public Object clone() throws CloneNotSupportedException {
        LogicalFileWrapper clone = (LogicalFileWrapper) super.clone();
        clone.proxy = (LogicalFileSPI) SAGAEngine.createAdaptorCopy(
                LogicalFileSPI.class, proxy, clone);
        clone.setProxy(clone.proxy);
        return clone;
    }

    public String[] findAttributes(String... patterns)
            throws NotImplementedException, BadParameterException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.findAttributes(patterns);
    }

    public Task<LogicalFile, String[]> findAttributes(TaskMode mode,
            String... patterns) throws NotImplementedException {
        return proxy.findAttributes(mode, patterns);
    }

    public String getAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.getAttribute(key);
    }

    public Task<LogicalFile, String> getAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.getAttribute(mode, key);
    }

    public String[] getVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return proxy.getVectorAttribute(key);
    }

    public Task<LogicalFile, String[]> getVectorAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.getVectorAttribute(mode, key);
    }

    public boolean isReadOnlyAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isReadOnlyAttribute(key);
    }

    public Task<LogicalFile, Boolean> isReadOnlyAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isReadOnlyAttribute(mode, key);
    }


    public boolean existsAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, NoSuccessException {
        return proxy.existsAttribute(key);
    }

    public Task<LogicalFile, Boolean> existsAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.existsAttribute(mode, key);
    }
    
    public boolean isRemovableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isRemovableAttribute(key);
    }

    public Task<LogicalFile, Boolean> isRemovableAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isRemovableAttribute(mode, key);
    }

    public boolean isVectorAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isVectorAttribute(key);
    }

    public Task<LogicalFile, Boolean> isVectorAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isVectorAttribute(mode, key);
    }

    public boolean isWritableAttribute(String key)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        return proxy.isWritableAttribute(key);
    }

    public Task<LogicalFile, Boolean> isWritableAttribute(TaskMode mode,
            String key) throws NotImplementedException {
        return proxy.isWritableAttribute(mode, key);
    }

    public String[] listAttributes() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return proxy.listAttributes();
    }

    public Task<LogicalFile, String[]> listAttributes(TaskMode mode)
            throws NotImplementedException {
        return proxy.listAttributes(mode);
    }

    public List<URL> listLocations() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return proxy.listLocations();
    }

    public Task<LogicalFile, List<URL>> listLocations(TaskMode mode)
            throws NotImplementedException {
        return proxy.listLocations(mode);
    }

    public void removeAttribute(String key) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        proxy.removeAttribute(key);
    }

    public Task<LogicalFile, Void> removeAttribute(TaskMode mode, String key)
            throws NotImplementedException {
        return proxy.removeAttribute(mode, key);
    }

    public Task<LogicalFile, Void> removeLocation(TaskMode mode, URL name)
            throws NotImplementedException {
        return proxy.removeLocation(mode, name);
    }

    public void removeLocation(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.removeLocation(name);
    }

    public Task<LogicalFile, Void> replicate(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return proxy.replicate(mode, name, flags);
    }

    public Task<LogicalFile, Void> replicate(TaskMode mode, URL name)
            throws NotImplementedException {
        return replicate(mode, name, Flags.NONE.getValue());
    }

    public void replicate(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        proxy.replicate(name, flags);
    }

    public void replicate(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        replicate(name, Flags.NONE.getValue());
    }

    public void setAttribute(String key, String value)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.setAttribute(key, value);
    }

    public Task<LogicalFile, Void> setAttribute(TaskMode mode, String key,
            String value) throws NotImplementedException {
        return proxy.setAttribute(mode, key, value);
    }

    public void setVectorAttribute(String key, String[] values)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.setVectorAttribute(key, values);
    }

    public Task<LogicalFile, Void> setVectorAttribute(TaskMode mode,
            String key, String[] values) throws NotImplementedException {
        return proxy.setVectorAttribute(mode, key, values);
    }

    public Task<LogicalFile, Void> updateLocation(TaskMode mode, URL nameOld,
            URL nameNew) throws NotImplementedException {
        return proxy.updateLocation(mode, nameOld, nameNew);
    }

    public void updateLocation(URL nameOld, URL nameNew)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        proxy.updateLocation(nameOld, nameNew);
    }
}
