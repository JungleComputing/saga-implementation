package org.ogf.saga.proxies.file;

import java.io.IOException;
import java.util.List;

import org.ogf.saga.ObjectType;
import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.engine.SAGAEngine;
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
import org.ogf.saga.file.File;
import org.ogf.saga.file.IOVec;
import org.ogf.saga.file.SeekMode;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.file.FileSpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public class FileWrapper extends NSEntryWrapper implements File {
    
    private FileSpiInterface proxy;
    
    FileWrapper(Session session, URL name, int flags) 
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        super(session);
        Object[] parameters = { this, session, name, flags };
        try {
            proxy = (FileSpiInterface) SAGAEngine.createAdaptorProxy(
                    FileSpiInterface.class,
                    new Class[] { FileWrapper.class,
                        org.ogf.saga.impl.session.Session.class, URL.class,
                        Integer.TYPE },
                        parameters);
            super.setProxy(proxy);
        } catch(org.ogf.saga.error.Exception e) {
            if (e instanceof NotImplemented) {
                throw (NotImplemented) e;
            }
            if (e instanceof IncorrectURL) {
                throw (IncorrectURL) e;
            }
            if (e instanceof AuthenticationFailed) {
                throw (AuthenticationFailed) e;
            }
            if (e instanceof AuthorizationFailed) {
                throw (AuthorizationFailed) e;
            }
            if (e instanceof PermissionDenied) {
                throw (PermissionDenied) e;
            }
            if (e instanceof BadParameter) {
                throw (BadParameter) e;
            }
            if (e instanceof  AlreadyExists) {
                throw (AlreadyExists) e;
            }
            if (e instanceof DoesNotExist) {
                throw (DoesNotExist) e;
            }
            if (e instanceof Timeout) {
                throw (Timeout) e;
            }
            if (e instanceof NoSuccess) {
                throw (NoSuccess) e;
            }
            throw new NoSuccess("Constructor failed", e);
        }
    }
    
    public long getSize()
            throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
                    PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return proxy.getSize();
    }
    
    public Task<Long> getSize(TaskMode mode)
            throws NotImplemented {
        return proxy.getSize(mode);
    }
       
    public Object clone() throws CloneNotSupportedException {
        FileWrapper clone = (FileWrapper) super.clone();
        clone.proxy = (FileSpiInterface) SAGAEngine.createAdaptorCopy(
                FileSpiInterface.class, proxy);
        clone.setProxy(proxy);
        return clone;
    }

    public ObjectType getType() {
        return ObjectType.FILE;
    }

    public List<String> modesE() throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess {
        return proxy.modesE();
    }

    public Task<List<String>> modesE(TaskMode mode) throws NotImplemented {
        return proxy.modesE(mode);
    }

    public int read(Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return read(buffer.getSize(), buffer);
    }

    public int read(int len, Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.read(len, buffer);
    }

    public Task<Integer> read(TaskMode mode, Buffer buffer) throws NotImplemented {
        return read(mode, -1, buffer);
    }

    public Task<Integer> read(TaskMode mode, int len, Buffer buffer) throws NotImplemented {
        return proxy.read(mode, len, buffer);
    }

    public int readE(String emode, String spec, Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.readE(emode, spec, buffer);
    }

    public Task<Integer> readE(TaskMode mode, String emode, String spec, Buffer buffer) throws NotImplemented {
        return proxy.readE(mode, emode, spec, buffer);
    }

    public int readP(String pattern, Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.readP(pattern, buffer);
    }

    public Task<Integer> readP(TaskMode mode, String pattern, Buffer buffer) throws NotImplemented {
        return proxy.readP(mode, pattern, buffer);
    }

    public void readV(IOVec[] iovecs) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        proxy.readV(iovecs);
    }

    public Task readV(TaskMode mode, IOVec[] iovecs) throws NotImplemented {
        return proxy.readV(mode, iovecs);
    }

    public long seek(long offset, SeekMode whence) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.seek(offset, whence);
    }

    public Task<Long> seek(TaskMode mode, long offset, SeekMode whence) throws NotImplemented {
        return proxy.seek(mode, offset, whence);
    }

    public int sizeE(String emode, String spec) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, IncorrectState, PermissionDenied, BadParameter, Timeout, NoSuccess {
        return proxy.sizeE(emode, spec);
    }

    public Task<Integer> sizeE(TaskMode mode, String emode, String spec) throws NotImplemented {
        return proxy.sizeE(mode, emode, spec);
    }

    public int sizeP(String pattern) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, IncorrectState, PermissionDenied, BadParameter, Timeout, NoSuccess {
        return proxy.sizeP(pattern);
    }

    public Task<Integer> sizeP(TaskMode mode, String pattern) throws NotImplemented {
        return proxy.sizeP(mode, pattern);
    }

    public int write(Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return write(buffer.getSize(), buffer);
    }

    public int write(int len, Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.write(len, buffer);
    }

    public Task<Integer> write(TaskMode mode, Buffer buffer) throws NotImplemented {
        return write(mode,-1, buffer);
    }

    public Task<Integer> write(TaskMode mode, int len, Buffer buffer) throws NotImplemented {
        return proxy.write(mode, len, buffer);
    }

    public int writeE(String emode, String spec, Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.writeE(emode, spec, buffer);
    }

    public Task<Integer> writeE(TaskMode mode, String emode, String spec, Buffer buffer) throws NotImplemented {
        return proxy.writeE(mode, emode, spec, buffer);
    }

    public int writeP(String pattern, Buffer buffer) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        return proxy.writeP(pattern, buffer);
    }

    public Task<Integer> writeP(TaskMode mode, String pattern, Buffer buffer) throws NotImplemented {
        return proxy.writeP(mode, pattern, buffer);
    }

    public void writeV(IOVec[] iovecs) throws NotImplemented, AuthenticationFailed, AuthorizationFailed, PermissionDenied, BadParameter, IncorrectState, Timeout, NoSuccess, IOException {
        proxy.writeV(iovecs);
    }

    public Task writeV(TaskMode mode, IOVec[] iovecs) throws NotImplemented {
        return proxy.writeV(mode, iovecs);
    }
}
