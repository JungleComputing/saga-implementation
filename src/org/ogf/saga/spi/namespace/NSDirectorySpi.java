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
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.impl.session.Session;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public abstract class NSDirectorySpi extends NSEntrySpi implements
        NSDirectorySpiInterface {
      
    protected static Logger logger = Logger.getLogger(NSDirectorySpi.class);
    
    protected NSDirectorySpi(NSDirectoryWrapper wrapper, Session session, URL name, int flags)
            throws NotImplemented, IncorrectURL, BadParameter, DoesNotExist,
            PermissionDenied, AuthorizationFailed, AuthenticationFailed,
            Timeout, NoSuccess, AlreadyExists {
        super(wrapper, session, name, flags);
    }

    public Task changeDir(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
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
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "copy", new Class[] { URL.class, URL.class, Integer.TYPE },
                source, target, flags);
    }
    
    public Task copy(TaskMode mode, String source, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "copy", new Class[] { String.class, URL.class, Integer.TYPE },
                source, target, flags);
    }

    public Task<Boolean> exists(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session,
                mode, "exists", new Class[] { URL.class }, name);
    }

    public Task<List<URL>> find(TaskMode mode, String pattern, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<URL>>(wrapper, session,
                mode, "find", new Class[] { String.class, Integer.TYPE },
                pattern, flags);
    }

    public Task<URL> getEntry(TaskMode mode, int entryNo) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(wrapper, session, mode,
                "getEntry", new Class[] { Integer.TYPE }, entryNo);
    }

    public Task<Integer> getNumEntries(TaskMode mode) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Integer>(wrapper, session,
                mode, "getNumEntries", new Class[] {});
    }

    public Task<Boolean> isDir(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session,
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
    
    // list method without the special handling of a single directory.
    protected List<URL> internalList(String pattern) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess, IncorrectURL {
        
        List<URL> resultList = new ArrayList<URL>();

        PatternConverter f = new PatternConverter(pattern);
        if (f.hasWildcard()) {
            List<URL> list = list("", Flags.NONE.getValue());
            for (URL u : list) {
                String path = u.getPath();
                if (f.matches(path)) {
                    resultList.add(u);
                }
            }
        } else {
            URL url = new URL(pattern);
            if (exists(url)) {
                resultList.add(url);
            }
        }
        return resultList;
    }


    protected List<URL> expandWildCards(String path) throws NotImplemented, NoSuccess,
            BadParameter, IncorrectURL, DoesNotExist, PermissionDenied,
            AuthorizationFailed, AuthenticationFailed, Timeout,
            IncorrectState {

        if (path.startsWith("/")) {
            throw new BadParameter("a wildcard path must be relative");
        }
        
        // Take the first part of the pattern, up until the first '/'.       
        int slashIndex = path.indexOf('/');
        String pattern;
        if (slashIndex < 0) {
            pattern = path;
        } else {
            pattern = path.substring(0, slashIndex);
        }
        
        // List the contents of this directory that matches the pattern.
        List<URL> list = internalList(pattern);
        if (slashIndex < 0) {
            // Done.
            return list;
        }
        
        List<URL> retval = new ArrayList<URL>();
        String subPath = path.substring(slashIndex+1);
        
        // If subPath is now an empty string, the original path ended with a '/',
        // so we now filter out the non-directories.
        if (subPath.equals("")) {
            for (URL u : list) {
                try {
                    if (isDir(u)) {
                        retval.add(u);
                    }
                } catch(Throwable e) {
                    logger.debug("isDir() gave an exception!", e);
                }
            }
            return retval;
        }
        
        // The pattern continues, so check sub-directories.
        for (URL url : list) {
            try {
                String urlPath = url.getPath();
                if (isDir(url)) {
                    logger.debug("" + urlPath + " is directory");
                    NSDirectorySpi dir = (NSDirectorySpi) clone();
                    dir.changeDir(url);
                    List<URL> subdirList = dir.expandWildCards(subPath);
                    dir.close(0);
                    for (URL u : subdirList) {
                        u.setPath(url.getPath() + "/" + u.getPath());
                        retval.add(u);
                    }
                } else {
                    logger.debug("" + urlPath + " is not a directory");
                }
            } catch (Throwable e) {
                logger.debug("got an exception!", e);
            }
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("Wildcard " + path + " gave: ");
            for (URL u : retval) {
                logger.debug("    " + u);
            }
        }
        return retval;
    }
    
    public List<URL> find(String pattern, int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess {
        checkClosed();
        int allowedFlags = Flags.DEREFERENCE.or(Flags.RECURSIVE);
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameter("Flags not allowed for find method: "
                    + flags);
        }
//      first add all files in the current directory that match the pattern

        List<URL> resultList;

        try {
            resultList = internalList(pattern);
        } catch(IncorrectURL e) {
            throw new BadParameter("Incorrect pattern", e);
        }

//      then list all files in the current directory if Recursive is set
        if (Flags.RECURSIVE.isSet(flags)) {
            List<URL> list;
            try {
                list = list(".", Flags.NONE.getValue());
            } catch(IncorrectURL e) {
                throw new SagaError("Internal error", e);
            }
            for (URL u : list) {
                try {
                    // and if it is a directory then find the pattern
                    if (isDir(u)) {
                        NSDirectorySpi dir = (NSDirectorySpi) clone();
                        dir.changeDir(u);
                        List<URL> l = dir.find(pattern, flags);
                        dir.close(0);
                        for (URL u1 : l) {
                            u1.setPath(u.getPath() + "/" + u1.getPath());
                            resultList.add(u1);
                        }
                    }
                } catch (Throwable e) {
                    logger.debug("Got exception!", e);
                }
            }

        }
        return resultList;
    }


    public Task<Boolean> isEntry(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session,
                mode, "isEntry", new Class[] { URL.class }, name);
    }

    public Task<Boolean> isLink(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<Boolean>(wrapper, session,
                mode, "isLink", new Class[] { URL.class }, name);
    }

    public Task link(TaskMode mode, URL source, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "link", new Class[] { URL.class, URL.class, Integer.TYPE },
                source, target, flags);
    }
    
    public Task link(TaskMode mode, String source, URL target, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "link", new Class[] { String.class, URL.class, Integer.TYPE },
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

        List<URL> list = listCurrentDir(flags);

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
    
    public abstract List<URL> listCurrentDir(int flags) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, Timeout, NoSuccess;

    public Task<List<URL>> list(TaskMode mode, String pattern, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<List<URL>>(wrapper, session,
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
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "makeDir", new Class[] { URL.class, Integer.TYPE }, name, flags);
    }

    public Task move(TaskMode mode, URL src, URL dest, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "move", new Class[] { URL.class, URL.class, Integer.TYPE },
                src, dest, flags);
    }
    
    public Task move(TaskMode mode, String src, URL dest, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "move", new Class[] { String.class, URL.class, Integer.TYPE },
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
                wrapper, session, mode, "open", new Class[] { URL.class,
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
                wrapper, session, mode, "openDir", new Class[] { URL.class,
                        Integer.TYPE }, name, flags);
    }

    public Task permissionsAllow(TaskMode mode, URL name, String id,
            int permissions, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsAllow", new Class[] { URL.class, String.class,
                        Integer.TYPE, Integer.TYPE }, name, id, permissions,
                flags);
    }
    
    public Task permissionsAllow(TaskMode mode, String name, String id,
            int permissions, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsAllow", new Class[] { String.class, String.class,
                        Integer.TYPE, Integer.TYPE }, name, id, permissions,
                flags);
    }

    public Task permissionsDeny(TaskMode mode, URL name, String id,
            int permissions, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsDeny", new Class[] { URL.class, String.class,
                        Integer.TYPE, Integer.TYPE }, name, id, permissions,
                flags);
    }
    
    public Task permissionsDeny(TaskMode mode, String name, String id,
            int permissions, int flags) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "permissionsDeny", new Class[] { String.class, String.class,
                        Integer.TYPE, Integer.TYPE }, name, id, permissions,
                flags);
    }   

    public Task<URL> readLink(TaskMode mode, URL name) throws NotImplemented {
        return new org.ogf.saga.impl.task.Task<URL>(wrapper, session, mode,
                "readlink", new Class[] { URL.class }, name);
    }

    public Task remove(TaskMode mode, URL name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "remove", new Class[] { URL.class, Integer.TYPE }, name, flags);
    }
    
    public Task remove(TaskMode mode, String name, int flags)
            throws NotImplemented {
        return new org.ogf.saga.impl.task.Task(wrapper, session, mode,
                "remove", new Class[] { String.class, Integer.TYPE }, name, flags);
    }
}
