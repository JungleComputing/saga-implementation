package org.ogf.saga.spi.namespace;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.attributes.PatternConverter;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class NSDirectorySpi extends NSEntrySpi implements
        NSDirectorySpiInterface {
      
    protected static Logger logger = Logger.getLogger(NSDirectorySpi.class);
    
    protected NSDirectorySpi(Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, BadParameter, DoesNotExist,
            PermissionDenied, AuthorizationFailed, AuthenticationFailed,
            Timeout, NoSuccess, AlreadyExists {
        super(session, name, flags);
    }

    public Task changeDir(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "changeDir", new Class[] { URL.class }, name);
    }

    protected void checkDirCopyFlags(int flags) throws IncorrectState, BadParameter {
        checkClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.RECURSIVE
                .or(Flags.OVERWRITE));
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameter("Flags not allowed for copy method: "
                    + flags);
        }
    }

    public Task copy(TaskMode mode, URL source, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "copy", new Class[] { URL.class, URL.class, Integer.TYPE },
                source, target, flags);
    }

    public Task<Boolean> exists(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "exists", new Class[] { URL.class }, name);
    }

    public Task<List<URL>> find(TaskMode mode, String pattern, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<URL>>(this, session,
                mode, "find", new Class[] { String.class, Integer.TYPE },
                pattern, flags);
    }

    public Task<URL> getEntry(TaskMode mode, int entryNo) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(this, session, mode,
                "getEntry", new Class[] { Integer.TYPE }, entryNo);
    }

    public Task<Integer> getNumEntries(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(this, session,
                mode, "getNumEntries", new Class[] {});
    }

    public Task<Boolean> isDir(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "isDir", new Class[] { URL.class }, name);
    }

    protected URL resolveToDir(URL url)
        throws NotImplemented, NoSuccess, BadParameter {
        if (url.isAbsolute()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Absolute URI " + url + " not resolved");
            }
            return url;
        }
        String path = url.getPath();
        if (path.startsWith("/")) {
            // Relative URL, absolute path. Resolve.
            URL u = nameUrl.resolve(url);

            if (logger.isDebugEnabled()) {
                logger.debug("Relative URI with abs path " + url
                        + " resolved to " + u);
            }
            return u;
        }

        URL u = new URL(nameUrl.toString());
        path = u.getPath();
        if ("".equals(path)) {
            path = ".";
        }
        u.setPath(path + "/DUMMY");
        u = u.resolve(url);

        if (logger.isDebugEnabled()) {
            logger.debug("Relative URI " + url + " resolved to " + u);
        }

        return u;
    }

    public Task<Boolean> isEntry(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "isEntry", new Class[] { URL.class }, name);
    }

    public Task<Boolean> isLink(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(this, session,
                mode, "isLink", new Class[] { URL.class }, name);
    }

    public Task link(TaskMode mode, URL source, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "link", new Class[] { URL.class, URL.class, Integer.TYPE },
                source, target, flags);
    }

    public List<URL> list(String pattern, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectState("list(): directory already closed");
        }
        int allowedFlags = Flags.DEREFERENCE.getValue();
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameter("Flags not allowed for list method: "
                    + flags);
        }

        if ("".equals(pattern)) {
            pattern = ".";
        }

        List<URL> list = listDir(flags);

        if (".".equals(pattern)) {
            return list;
        }

        List<URL> resultList = new ArrayList<URL>();
        PatternConverter pc = new PatternConverter(pattern);
        for (URL url : list) {
            String path = url.getPath();
            if (pc.matches(path)) {
                resultList.add(url);
            }
        }
        
        try {

        if (resultList.size() == 1 && isDir(resultList.get(0))) {
            // Pattern indicates a single directory. In this case, list the
            // contents of the directory (like "ls").
            NSDirectory dir = NSFactory.createNSDirectory(session,
                    nameUrl.resolve(resultList.get(0)),
                    Flags.NONE.getValue());
            return dir.list(".", flags);
        }
        } catch(DoesNotExist e) {
            throw new SagaError("Should not happen", e);
        } catch(AlreadyExists e) {
            throw new SagaError("Should not happen", e);
        }

        return resultList;
    }

    protected abstract List<URL> listDir(int flags) throws NoSuccess, NotImplemented,
              BadParameter;

    public Task<List<URL>> list(TaskMode mode, String pattern, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<URL>>(this, session,
                mode, "list", new Class[] { String.class, Integer.TYPE },
                pattern, flags);
    }

    public void makeDir(URL target, int flags) throws NotImplemented,
            IncorrectURL, AuthenticationFailed, AuthorizationFailed,
            PermissionDenied, BadParameter, IncorrectState, AlreadyExists,
            DoesNotExist, Timeout, NoSuccess {
        checkClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.EXCL);
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameter("Flags not allowed for makeDir method: "
                    + flags);
        }
        target = resolveToDir(target);
        NSFactory.createNSDirectory(session, target,
                flags | Flags.CREATE.getValue());
    }

    public Task makeDir(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "makeDir", new Class[] { URL.class, Integer.TYPE }, name, flags);
    }

    public Task move(TaskMode mode, URL src, URL dest, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "move", new Class[] { URL.class, URL.class, Integer.TYPE },
                src, dest, flags);
    }

    public NSEntry open(URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        checkClosed();
        name = resolveToDir(name);
        return NSFactory.createNSEntry(session, name, flags);
    }

    public Task<NSEntry> open(TaskMode mode, URL name,
            int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<NSEntry>(
                this, session, mode, "open", new Class[] { URL.class,
                        Integer.TYPE }, name, flags);
    }

    public org.ogf.saga.namespace.NSDirectory openDir(URL name, int flags)
            throws NotImplemented, IncorrectURL, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, BadParameter,
            IncorrectState, AlreadyExists, DoesNotExist, Timeout, NoSuccess {
        checkClosed();
        name = resolveToDir(name);
        return NSFactory.createNSDirectory(session, name, flags);
    }

    public Task<org.ogf.saga.namespace.NSDirectory> openDir(TaskMode mode,
            URL name, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<org.ogf.saga.namespace.NSDirectory>(
                this, session, mode, "openDir", new Class[] { URL.class,
                        Integer.TYPE }, name, flags);
    }

    public Task permissionsAllow(TaskMode mode, URL name, String id,
            int permissions, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsAllow", new Class[] { URL.class, String.class,
                        Integer.TYPE, Integer.TYPE }, name, id, permissions,
                flags);
    }

    public Task permissionsDeny(TaskMode mode, URL name, String id,
            int permissions, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "permissionsDeny", new Class[] { URL.class, String.class,
                        Integer.TYPE, Integer.TYPE }, name, id, permissions,
                flags);
    }

    public Task<URL> readLink(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(this, session, mode,
                "readlink", new Class[] { URL.class }, name);
    }

    public Task remove(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(this, session, mode,
                "remove", new Class[] { URL.class, Integer.TYPE }, name, flags);
    }
}
