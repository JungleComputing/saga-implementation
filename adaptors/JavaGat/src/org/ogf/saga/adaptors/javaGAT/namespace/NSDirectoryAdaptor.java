package org.ogf.saga.adaptors.javaGAT.namespace;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.io.File;
import org.gridlab.gat.io.FileInterface;
import org.ogf.saga.adaptors.javaGAT.util.Initialize;
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
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSDirectoryWrapper;
import org.ogf.saga.spi.namespace.NSDirectoryAdaptorBase;
import org.ogf.saga.spi.namespace.NSDirectorySPI;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class NSDirectoryAdaptor extends NSDirectoryAdaptorBase implements
        NSDirectorySPI {

    static Logger logger = LoggerFactory.getLogger(NSDirectoryAdaptor.class);

    static {
        Initialize.initialize();
    }

    protected NSEntryAdaptor entry;

    protected NSDirectoryAdaptor(SessionImpl sessionImpl, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        this(null, sessionImpl, name, flags);
    }

    public NSDirectoryAdaptor(NSDirectoryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, sessionImpl, name, flags);
        entry = new NSEntryAdaptor(wrapper, sessionImpl, name, flags, true);
    }

    public Object clone() throws CloneNotSupportedException {
        NSDirectoryAdaptor clone = (NSDirectoryAdaptor) super.clone();
        clone.entry = (NSEntryAdaptor) entry.clone();
        clone.entry.setWrapper(clone.wrapper);
        return clone;
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        entry.close(timeoutInSeconds);
        super.close(timeoutInSeconds);
    }

    public void changeDir(URL dir) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "changeDir(): directory already closed", wrapper);
        }
        File toDir;
        FileInterface toDirFileInterface;
        try {
            toDir = GAT.createFile(entry.gatContext, NSEntryAdaptor
                    .cvtToGatURI(dir));
            toDirFileInterface = toDir.getFileInterface();
        } catch (GATObjectCreationException e) {
            throw new NoSuccessException(e, wrapper);
        } catch (URISyntaxException e) {
            throw new BadParameterException(e, wrapper);
        }
        try {
            if (!toDirFileInterface.exists()) {
                throw new DoesNotExistException("Directory does not exist: "
                        + dir.toString(), wrapper);
            }
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        try {
            if (!toDirFileInterface.isDirectory()) {
                throw new DoesNotExistException(dir.toString()
                        + " is not a directory", wrapper);
            }
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        entry.fileImpl = toDir;
        entry.file = toDirFileInterface;
        nameUrl = dir.normalize();
    }

    public void copy(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        checkNotClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.RECURSIVE
                .or(Flags.OVERWRITE));
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for copy method: " + flags, wrapper);
        }

        URL source1 = resolveToDir(source);
        target = resolveToDir(target);

        NSEntryAdaptor sourceEntry = new NSEntryAdaptor(null, sessionImpl,
                source1, Flags.NONE.getValue(), isDir(source));

        // Don't resolve target with respect to source!!!
        sourceEntry.nonResolvingCopy(target, flags);
        sourceEntry.close(0.0F);
    }

    public void copy(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        checkNotClosed();
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.RECURSIVE
                .or(Flags.OVERWRITE));
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for copy method: " + flags, wrapper);
        }
        List<URL> sources = expandWildCards(source);
        target = resolveToDir(target);
        if (sources.size() > 1) {
            // target must exist and be a directory!
            try {
                File targetFile = GAT.createFile(entry.gatContext,
                        NSEntryAdaptor.cvtToGatURI(target));
                if (!targetFile.isDirectory()) {
                    throw new BadParameterException(
                            "source expands to more than one file and "
                                    + "target is not a directory", wrapper);
                }
            } catch (GATObjectCreationException e) {
                throw new NoSuccessException(e, wrapper);
            } catch (URISyntaxException e) {
                throw new BadParameterException(e, wrapper);
            }
        } else if (sources.size() < 1) {
            throw new DoesNotExistException("source " + source
                    + " does not exist", wrapper);
        }

        for (URL s : sources) {
            URL s1 = resolveToDir(s);
            NSEntryAdaptor sourceEntry = new NSEntryAdaptor(null, sessionImpl,
                    s1, Flags.NONE.getValue(), isDir(s));
            // Don't resolve target with respect to source!!!
            sourceEntry.nonResolvingCopy(target, flags);
            sourceEntry.close(0.0F);
        }
    }

    public boolean exists(URL name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        checkNotClosed();

        name = resolveToDir(name);

        File existsFile;
        try {
            existsFile = GAT.createFile(entry.gatContext, NSEntryAdaptor
                    .cvtToGatURI(name));
        } catch (GATObjectCreationException e) {
            throw new NoSuccessException(e, wrapper);
        } catch (URISyntaxException e) {
            throw new BadParameterException(e, wrapper);
        }
        try {
            return existsFile.getFileInterface().exists();
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
    }

    public URL getEntry(int entryNo) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "getEntry(): directory already closed", wrapper);
        }
        File[] resultFiles;
        try {
            resultFiles = (org.gridlab.gat.io.File[]) entry.file.listFiles();
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        if (entryNo >= resultFiles.length) {
            throw new DoesNotExistException("Invalid index: " + entryNo, wrapper);
        }
        try {
            return URLFactory.createURL(resultFiles[entryNo].toGATURI().toString());
        } catch (BadParameterException e) {
            throw new NoSuccessException(e, wrapper);
        }
    }

    public int getNumEntries() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "getNumEntries(): directory already closed", wrapper);
        }
        File[] resultFiles;
        try {
            resultFiles = (org.gridlab.gat.io.File[]) entry.file.listFiles();
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        return resultFiles.length;
    }

    public boolean isDir(URL name) throws NotImplementedException,
            DoesNotExistException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "isDir(): directory already closed", wrapper);
        }
        if (!name.getPath().startsWith("/")) {
            name = resolveToDir(name);
        }
        File isDirFile;
        try {
            logger.debug("name = " + name);
            isDirFile = GAT.createFile(entry.gatContext, NSEntryAdaptor
                    .cvtToGatURI(name));
        } catch (GATObjectCreationException e) {
            throw new NoSuccessException(e, wrapper);
        } catch (URISyntaxException e) {
            throw new BadParameterException(e, wrapper);
        }
        try {
            if (!isDirFile.getFileInterface().exists()) {
                throw new DoesNotExistException("Does not exist: "
                        + name.toString(), wrapper);
            }
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        try {
            return isDirFile.getFileInterface().isDirectory();
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
    }

    public boolean isEntry(URL name) throws NotImplementedException,
            DoesNotExistException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "isEntry(): directory already closed", wrapper);
        }
        name = resolveToDir(name);
        File isDirFile;
        try {
            isDirFile = GAT.createFile(entry.gatContext, NSEntryAdaptor
                    .cvtToGatURI(name));
        } catch (GATObjectCreationException e) {
            throw new NoSuccessException(e, wrapper);
        } catch (URISyntaxException e) {
            throw new BadParameterException(e, wrapper);
        }
        try {
            if (!entry.file.exists()) {
                throw new DoesNotExistException("Does not exist: "
                        + name.toString(), wrapper);
            }
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        try {
            return isDirFile.getFileInterface().isFile();
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
    }

    public boolean isLink(URL name) throws NotImplementedException,
            DoesNotExistException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("isLink", wrapper);
    }

    public void link(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("link", wrapper);
    }

    public void link(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("link", wrapper);
    }

    public List<URL> listCurrentDir(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {

        return listDir();

    }

    private List<URL> listDir() throws NoSuccessException,
            NotImplementedException, BadParameterException {

        File[] resultFiles;
        try {
            resultFiles = (org.gridlab.gat.io.File[]) entry.file.listFiles();
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        List<URL> resultList = new ArrayList<URL>();
        if (resultFiles != null) {
            for (File resultFile : resultFiles) {
                try {
                    resultList.add(URLFactory.createURL(resultFile.getFileInterface()
                            .getName()));
                } catch (GATInvocationException e) {
                    throw new NoSuccessException(e, wrapper);
                }
            }
        }
        return resultList;
    }

    public void makeDir(URL target, int flags) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "makeDir(): directory already closed", wrapper);
        }
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.EXCL);
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for makeDir method: " + flags, wrapper);
        }
        target = resolveToDir(target);
        NSDirectoryAdaptor dir = new NSDirectoryAdaptor(null, sessionImpl, target,
                Flags.CREATE.or(flags));
        dir.close(0);
    }

    public void move(URL source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "move(): directory already closed", wrapper);
        }
        int allowedFlags = Flags.RECURSIVE.or(Flags.DEREFERENCE
                .or(Flags.OVERWRITE));
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for move method: " + flags, wrapper);
        }

        target = resolveToDir(target);
        URL source1 = resolveToDir(source);

        NSEntryAdaptor sourceEntry = new NSEntryAdaptor(null, sessionImpl,
                    source1, Flags.NONE.getValue(), isDir(source));
        // Don't resolve target with respect to source!!!
        sourceEntry.nonResolvingMove(target, flags);
        sourceEntry.close(0.0F);
    }

    public void move(String source, URL target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "move(): directory already closed", wrapper);
        }
        int allowedFlags = Flags.RECURSIVE.or(Flags.DEREFERENCE
                .or(Flags.OVERWRITE));
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for move method: " + flags, wrapper);
        }

        List<URL> sources = expandWildCards(source);
        target = resolveToDir(target);
        if (sources.size() > 1) {
            // target must exist and be a directory!
            try {
                File targetFile = GAT.createFile(entry.gatContext,
                        NSEntryAdaptor.cvtToGatURI(target));
                try {
                    if (!targetFile.getFileInterface().isDirectory()) {
                        throw new BadParameterException(
                                "source expands to more than one file and "
                                        + "target is not a directory", wrapper);
                    }
                } catch (GATInvocationException e) {
                    throw new NoSuccessException(e, wrapper);
                }
            } catch (GATObjectCreationException e) {
                throw new NoSuccessException(e, wrapper);
            } catch (URISyntaxException e) {
                throw new BadParameterException(e, wrapper);
            }
        } else if (sources.size() < 1) {
            throw new DoesNotExistException("source " + source
                    + " does not exist", wrapper);
        }

        for (URL s : sources) {
            URL s1 = resolveToDir(s);
            NSEntryAdaptor sourceEntry = new NSEntryAdaptor(null, sessionImpl,
                    s1, Flags.NONE.getValue(), isDir(s));
            // Don't resolve target with respect to source!!!
            sourceEntry.nonResolvingMove(target, flags);
            sourceEntry.close(0.0F);
        }
    }

    public void permissionsAllow(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsAllow", wrapper);
    }

    public void permissionsAllow(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsAllow", wrapper);
    }

    public void permissionsDeny(URL target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("permissionsDeny", wrapper);
    }

    public void permissionsDeny(String target, String id, int permissions,
            int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("permissionsDeny", wrapper);
    }

    public URL readLink(URL name) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("readLink", wrapper);
    }

    public void remove(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectURLException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "remove(): directory already closed", wrapper);
        }
        int allowedFlags = Flags.RECURSIVE.or(Flags.DEREFERENCE);
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for remove method: " + flags, wrapper);
        }

        URL target1 = resolveToDir(target);

        NSEntryAdaptor targetEntry = null;
        try {
            targetEntry = new NSEntryAdaptor(null, sessionImpl,
                    target1, Flags.NONE.getValue(), isDir(target));
        } catch (AlreadyExistsException e) {
            // cannot happen because create flag is not allowed for this method
            throw new NoSuccessException("Should not happen!: " + e, wrapper);
        }

        targetEntry.remove(flags);
        targetEntry.close(0);

    }

    public void remove(String target, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectURLException, BadParameterException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already closed!");
            }
            throw new IncorrectStateException(
                    "remove(): directory already closed", wrapper);
        }
        int allowedFlags = Flags.RECURSIVE.or(Flags.DEREFERENCE);
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for remove method: " + flags, wrapper);
        }

        List<URL> targets = expandWildCards(target);

        if (targets.size() < 1) {
            throw new DoesNotExistException("remove target " + target
                    + " does not exist", wrapper);
        }

        for (URL s : targets) {
            NSEntryAdaptor targetEntry = null;
            URL s1 = resolveToDir(s);
            try {
                targetEntry = new NSEntryAdaptor(null, sessionImpl,
                        s1, Flags.NONE.getValue(), isDir(s));
            } catch (AlreadyExistsException e) {
                // cannot happen because create flag is not allowed for this
                // method
                throw new NoSuccessException("Should not happen!: " + e, wrapper);
            }

            targetEntry.remove(flags);
            targetEntry.close(0);
        }
    }

    public void copy(URL target, int flags) throws IncorrectStateException,
            NoSuccessException, BadParameterException, AlreadyExistsException,
            IncorrectURLException, NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, DoesNotExistException {
        entry.copy(target, flags);
    }

    public void move(URL target, int flags)
            throws IncorrectStateException, NoSuccessException,
            BadParameterException, AlreadyExistsException,
            NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, IncorrectURLException, DoesNotExistException {
        entry.move(target, flags);
    }

    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isDir();
    }

    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.isEntry();
    }

    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("isLink", wrapper);
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        entry.link(target, flags);
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsAllow(id, permissions, flags);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        entry.permissionsDeny(id, permissions, flags);
    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        return entry.readLink();
    }

    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        entry.remove(flags);
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getGroup();
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return entry.getOwner();
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        return entry.permissionsCheck(id, permissions);
    }
}
