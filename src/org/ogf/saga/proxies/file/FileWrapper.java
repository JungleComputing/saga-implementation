package org.ogf.saga.proxies.file;

import java.io.IOException;
import java.util.List;

import org.ogf.saga.ObjectType;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.IncorrectState;
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

class FileWrapper extends NSEntryWrapper implements File {
    
    private FileSpiInterface proxy;
    
    FileWrapper(Session session, FileSpiInterface proxy) {
        super(session, proxy);
        this.proxy = proxy;
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
        FileWrapper f = (FileWrapper) super.clone();
        f.proxy = (FileSpiInterface) proxy.clone();
        return f;
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
