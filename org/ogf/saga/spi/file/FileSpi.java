package org.ogf.saga.spi.file;

import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.file.IOVec;
import org.ogf.saga.file.SeekMode;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;
import org.ogf.saga.spi.namespace.NSEntrySpi;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class FileSpi extends NSEntrySpi implements FileInterface {

    protected int fileFlags;

    public FileSpi(Session session, URL name, int flags) throws NotImplemented,
            IncorrectURL, BadParameter, DoesNotExist, PermissionDenied,
            AuthorizationFailed, AuthenticationFailed, Timeout, NoSuccess,
            AlreadyExists {
        super(session, name, flags & Flags.ALLNAMESPACEFLAGS.getValue());
        fileFlags = flags & ~Flags.ALLNAMESPACEFLAGS.getValue();
        if ((fileFlags | Flags.ALLFILEFLAGS.getValue())
                != Flags.ALLFILEFLAGS.getValue()) {
            throw new BadParameter("Illegal flags for File constructor: " + flags);
        }
    }

    public Task<Long> getSize(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Long>(this, session, mode,
                "getSize", new Class[] {});
    }

    public Task<Integer> read(TaskMode mode, int len, Buffer buffer)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "read", new Class[] { Integer.TYPE, Buffer.class }, len, buffer);
    }

    public Task<Integer> write(TaskMode mode, int arg1, Buffer arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "write", new Class[] { Integer.TYPE, Buffer.class }, arg1, arg2);
    }

    public Task<Long> seek(TaskMode mode, long arg1, SeekMode arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Long>(this, session, mode,
                "seek", new Class[] { Long.TYPE, SeekMode.class }, arg1, arg2);
    }

    public Task<List<String>> modesE(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<String>>(this, session,
                mode, "modesE", new Class[] {});
    }

    public Task<Integer> sizeE(TaskMode mode, String arg1, String arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "sizeE", new Class[] { String.class, String.class }, arg1, arg2);
    }

    public Task<Integer> readE(TaskMode mode, String arg1, String arg2,
            Buffer arg3) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "readE",
                new Class[] { String.class, String.class, Buffer.class }, arg1,
                arg2, arg3);
    }

    public Task<Integer> writeE(TaskMode mode, String arg1, String arg2,
            Buffer arg3) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "writeE", new Class[] { String.class, String.class,
                        Buffer.class }, arg1, arg2, arg3);
    }

    public Task<Integer> sizeP(TaskMode mode, String arg1)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "sizeP", new Class[] { String.class }, arg1);
    }

    public Task<Integer> readP(TaskMode mode, String arg1, Buffer arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "readP", new Class[] { String.class, Buffer.class }, arg1, arg2);
    }

    public Task<Integer> writeP(TaskMode mode, String arg1, Buffer arg2)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session, mode,
                "writeP", new Class[] { String.class, Buffer.class }, arg1,
                arg2);
    }

    public Task readV(TaskMode mode, IOVec[] arg1) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode, "readV",
                new Class[] { IOVec[].class }, (Object) arg1);
    }

    public Task writeV(TaskMode mode, IOVec[] arg1) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode, "writeV",
                new Class[] { IOVec[].class }, (Object) arg1);
    }
}
