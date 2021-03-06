package org.ogf.saga.spi.namespace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.attributes.PatternConverter;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public abstract class NSDirectoryAdaptorBase extends NSEntryAdaptorBase
        implements NSDirectorySPI {

    private class DirIterator implements Iterator<URL> {

        int index = 0;
        URL[] list;
 
        public DirIterator() throws NotImplementedException,
                AuthenticationFailedException, AuthorizationFailedException,
                PermissionDeniedException, IncorrectStateException,
                TimeoutException, NoSuccessException {
            try {
                list = listCurrentDir(0).toArray(new URL[0]);
            } catch (BadParameterException e) {
                // Should not happen
            } catch (IncorrectURLException e) {
                // Should not happen
            }
        }

        public boolean hasNext() {
            return index < list.length;
        }

        public URL next() {
            if (index >= list.length) {
                throw new NoSuchElementException("No more elements");
            }
            return list[index++];
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }

    private static final Logger logger = LoggerFactory
            .getLogger(NSDirectoryAdaptorBase.class);

    protected NSDirectoryWrapper nsDirectoryWrapper;

    protected NSDirectoryAdaptorBase(NSDirectoryWrapper wrapper,
            SessionImpl sessionImpl, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, sessionImpl, name, flags);
        this.nsDirectoryWrapper = wrapper;
    }

    public void setWrapper(NSDirectoryWrapper wrapper) {
        this.nsDirectoryWrapper = wrapper;
        super.setWrapper(wrapper);
    }

    public Iterator<URL> iterator() {
        try {
            return new DirIterator();
        } catch (SagaException e) {
            throw new RuntimeException("Got SAGA exception", e);
        }
    }

    public Task<NSDirectory, Void> changeDir(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "changeDir", new Class[] { URL.class }, name);
    }
  
    public Task<NSDirectory, Void> copy(TaskMode mode, URL source, URL target,
            int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "copy", new Class[] { URL.class, URL.class,
                        Integer.TYPE }, source, target, flags);
    }

    public Task<NSDirectory, Void> copy(TaskMode mode, String source,
            URL target, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "copy", new Class[] { String.class,
                        URL.class, Integer.TYPE }, source, target, flags);
    }

    public Task<NSDirectory, Boolean> exists(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Boolean>(
                nsDirectoryWrapper, sessionImpl, mode, "exists",
                new Class[] { URL.class }, name);
    }

    public Task<NSDirectory, List<URL>> find(TaskMode mode, String pattern,
            int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, List<URL>>(
                nsDirectoryWrapper, sessionImpl, mode, "find", new Class[] { String.class,
                        Integer.TYPE }, pattern, flags);
    }

    public Task<NSDirectory, URL> getEntry(TaskMode mode, int entryNo)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, URL>(nsDirectoryWrapper,
                sessionImpl, mode, "getEntry", new Class[] { Integer.TYPE },
                entryNo);
    }

    public Task<NSDirectory, Integer> getNumEntries(TaskMode mode)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Integer>(
                nsDirectoryWrapper, sessionImpl, mode, "getNumEntries", new Class[] {});
    }

    public Task<NSDirectory, Boolean> isDir(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Boolean>(
                nsDirectoryWrapper, sessionImpl, mode, "isDir", new Class[] { URL.class },
                name);
    }

    protected URL resolveToDir(URL url) throws NotImplementedException,
            NoSuccessException, BadParameterException {
        if (url.isAbsolute()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Absolute URI " + url + " not resolved");
            }
            return url;
        }
        String path = url.getPath();
        URL myURL = getEntryURL();
        if (path.startsWith("/")) {
            // Relative URL, absolute path. Resolve.
            URL u = myURL.resolve(url);

            if (logger.isDebugEnabled()) {
                logger.debug("Relative URI with abs path " + url
                        + " resolved to " + u);
            }
            return u;
        }

        URL u = URLFactory.createURL(MY_FACTORY, myURL.toString());
        path = u.getPath();
        
        // If there is no path, and the URL has a host part, the path
        // should start with a '/'.
        if (path.equals("")) {
            if (myURL.getHost() == null) {
                path = ".";
            }
        }
        u.setPath(path + "/DUMMY");
        u = u.resolve(url);

        if (logger.isDebugEnabled()) {
            logger.debug("Relative URI " + url + " resolved to " + u);
        }

        return u;
    }

    // list method without the special handling of a single directory.
    protected List<URL> internalList(String pattern)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, IncorrectURLException {

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
            URL url = URLFactory.createURL(MY_FACTORY, pattern);
            if (exists(url)) {
                resultList.add(url);
            }
        }
        return resultList;
    }

    protected List<URL> expandWildCards(String path)
            throws NotImplementedException, NoSuccessException,
            BadParameterException, IncorrectURLException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, IncorrectStateException {

        if (path.startsWith("/")) {
            throw new BadParameterException("a wildcard path must be relative");
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
        String subPath = path.substring(slashIndex + 1);

        // If subPath is now an empty string, the original path ended with a
        // '/',
        // so we now filter out the non-directories.
        if (subPath.equals("")) {
            for (URL u : list) {
                try {
                    if (isDir(u)) {
                        retval.add(u);
                    }
                } catch (Throwable e) {
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
                    NSDirectoryAdaptorBase dir = (NSDirectoryAdaptorBase) clone();
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

    public List<URL> find(String pattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {

        // first add all files in the current directory that match the pattern
        List<URL> resultList;

        try {
            resultList = internalList(pattern);
        } catch (IncorrectURLException e) {
            throw new BadParameterException("Incorrect pattern", e);
        }

        // then list all files in the current directory if Recursive is set
        if (Flags.RECURSIVE.isSet(flags)) {
            List<URL> list;
            try {
                list = list(".", Flags.NONE.getValue());
            } catch (IncorrectURLException e) {
                throw new SagaRuntimeException("Internal error", e);
            }
            for (URL u : list) {
                try {
                    // and if it is a directory then find the pattern
                    if (isDir(u)) {
                        NSDirectoryAdaptorBase dir = (NSDirectoryAdaptorBase) clone();
                        dir.setWrapper(null);
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

    public Task<NSDirectory, Boolean> isEntry(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Boolean>(
                nsDirectoryWrapper, sessionImpl, mode, "isEntry",
                new Class[] { URL.class }, name);
    }

    public Task<NSDirectory, Boolean> isLink(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Boolean>(
                nsDirectoryWrapper, sessionImpl, mode, "isLink",
                new Class[] { URL.class }, name);
    }

    public Task<NSDirectory, Void> link(TaskMode mode, URL source, URL target,
            int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "link", new Class[] { URL.class, URL.class,
                        Integer.TYPE }, source, target, flags);
    }

    public Task<NSDirectory, Void> link(TaskMode mode, String source,
            URL target, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "link", new Class[] { String.class,
                        URL.class, Integer.TYPE }, source, target, flags);
    }

    public List<URL> list(String pattern, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        
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
                NSDirectory dir = NSFactory.createNSDirectory(MY_FACTORY, sessionImpl,
                        resolveToDir(resultList.get(0)), Flags.NONE
                                .getValue());
                return dir.list(".", flags);
            }
        } catch (DoesNotExistException e) {
            throw new SagaRuntimeException("Should not happen", e);
        } catch (AlreadyExistsException e) {
            throw new SagaRuntimeException("Should not happen", e);
        }

        return resultList;
    }

    public abstract List<URL> listCurrentDir(int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, IncorrectURLException;

    public Task<NSDirectory, List<URL>> list(TaskMode mode, String pattern,
            int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, List<URL>>(
                nsDirectoryWrapper, sessionImpl, mode, "list", new Class[] { String.class,
                        Integer.TYPE }, pattern, flags);
    }

    protected List<URL> convertToRelativeURLs(String[] entries)
            throws BadParameterException, NoSuccessException,
            NotImplementedException {
        List<URL> resultList = new ArrayList<URL>();
     
        if (entries != null) {
            for (String entry : entries) {
                URL u = convertToRelativeURL(entry);
                resultList.add(u);
            }
        }
        return resultList;
    }
    
    protected URL convertToRelativeURL(String entry) throws NoSuccessException,
            NotImplementedException, BadParameterException {
        // Watch out for special characters. Therefore, create the
        // URL in two steps: First create an empty one, and then
        // set the path.
        // Also, this does not work if the first section of the path
        // contains a ':' (Bug in java.net.URI?). So more trickery ...
        URL url = URLFactory.createURL(MY_FACTORY, "");

        if (entry.contains(":")) {
            url.setPath("./" + entry);
        } else {
            url.setPath(entry);
        }

        return url;
    }
    
    public void makeDir(URL target, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        target = resolveToDir(target);
        NSDirectory dir = NSFactory.createNSDirectory(MY_FACTORY, sessionImpl, target, flags
                | Flags.CREATE.getValue());
        dir.close();
    }

    public Task<NSDirectory, Void> makeDir(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "makeDir", new Class[] { URL.class,
                        Integer.TYPE }, name, flags);
    }

    public Task<NSDirectory, Void> move(TaskMode mode, URL src, URL dest,
            int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "move", new Class[] { URL.class, URL.class,
                        Integer.TYPE }, src, dest, flags);
    }

    public Task<NSDirectory, Void> move(TaskMode mode, String src, URL dest,
            int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "move", new Class[] { String.class,
                        URL.class, Integer.TYPE }, src, dest, flags);
    }

    public NSEntry open(URL name, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        name = resolveToDir(name);
        return NSFactory.createNSEntry(MY_FACTORY, sessionImpl, name, flags);
    }

    public Task<NSDirectory, NSEntry> open(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, NSEntry>(
                nsDirectoryWrapper, sessionImpl, mode, "open", new Class[] { URL.class,
                        Integer.TYPE }, name, flags);
    }

    public org.ogf.saga.namespace.NSDirectory openDir(URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        name = resolveToDir(name);
        return NSFactory.createNSDirectory(MY_FACTORY, sessionImpl, name, flags);
    }

    public Task<NSDirectory, org.ogf.saga.namespace.NSDirectory> openDir(
            TaskMode mode, URL name, int flags) throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, NSDirectory>(
                nsDirectoryWrapper, sessionImpl, mode, "openDir", new Class[] { URL.class,
                        Integer.TYPE }, name, flags);
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, URL name,
            String id, int permissions, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "permissionsAllow", new Class[] { URL.class,
                        String.class, Integer.TYPE, Integer.TYPE }, name, id,
                permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsAllow(TaskMode mode, String name,
            String id, int permissions, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "permissionsAllow",
                new Class[] { String.class, String.class, Integer.TYPE,
                        Integer.TYPE }, name, id, permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, URL name,
            String id, int permissions, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "permissionsDeny", new Class[] { URL.class,
                        String.class, Integer.TYPE, Integer.TYPE }, name, id,
                permissions, flags);
    }

    public Task<NSDirectory, Void> permissionsDeny(TaskMode mode, String name,
            String id, int permissions, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "permissionsDeny",
                new Class[] { String.class, String.class, Integer.TYPE,
                        Integer.TYPE }, name, id, permissions, flags);
    }

    public Task<NSDirectory, URL> readLink(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, URL>(nsDirectoryWrapper,
                sessionImpl, mode, "readlink", new Class[] { URL.class }, name);
    }

    public Task<NSDirectory, Void> remove(TaskMode mode, URL name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "remove", new Class[] { URL.class,
                        Integer.TYPE }, name, flags);
    }

    public Task<NSDirectory, Void> remove(TaskMode mode, String name, int flags)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Void>(nsDirectoryWrapper,
                sessionImpl, mode, "remove", new Class[] { String.class,
                        Integer.TYPE }, name, flags);
    }
    
    public Task<NSDirectory, Long> getMTime(TaskMode mode, URL name)
            throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskImpl<NSDirectory, Long>(nsDirectoryWrapper,
                sessionImpl, mode, "getMTime", new Class[] { URL.class }, name);
    }
}
