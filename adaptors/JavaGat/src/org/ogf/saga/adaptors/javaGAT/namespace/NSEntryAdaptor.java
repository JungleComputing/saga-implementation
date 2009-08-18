package org.ogf.saga.adaptors.javaGAT.namespace;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
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
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.attributes.AttributesImpl;
import org.ogf.saga.impl.session.SessionImpl;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.spi.namespace.NSEntryAdaptorBase;
import org.ogf.saga.spi.namespace.NSEntrySPI;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class NSEntryAdaptor extends NSEntryAdaptorBase implements NSEntrySPI {

    private static Logger logger = LoggerFactory
            .getLogger(NSEntryAdaptor.class);

    static {
        Initialize.initialize();
    }

    protected File fileImpl;
    protected FileInterface file;
    protected GATContext gatContext;
    protected boolean isDirectory;
    protected URI gatURI;

    AttributesImpl attributesImpl = new AttributesImpl();

    protected NSEntryAdaptor(SessionImpl sessionImpl, URL name, int flags)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        this(null, sessionImpl, name, flags, false);
    }

    public NSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        this(wrapper, sessionImpl, name, flags, false);
    }

    NSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl sessionImpl, URL name,
            int flags, boolean isDir) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, sessionImpl, name, flags);

        org.ogf.saga.adaptors.javaGAT.session.Session gatSession;

        synchronized (sessionImpl) {
            gatSession = (org.ogf.saga.adaptors.javaGAT.session.Session) sessionImpl
                    .getAdaptorSession("JavaGAT");
            if (gatSession == null) {
                gatSession = new org.ogf.saga.adaptors.javaGAT.session.Session();
                sessionImpl.putAdaptorSession("JavaGAT", gatSession);
            }
        }

        gatContext = gatSession.getGATContext();
        try {
            gatURI = cvtToGatURI(nameUrl);
        } catch (URISyntaxException e1) {
            throw new IncorrectURLException(e1);
        }

        try {
            fileImpl = GAT.createFile(gatContext, gatURI);
            file = fileImpl.getFileInterface();
        } catch (GATObjectCreationException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("GAT.createFile failed");
            }
            throw new NoSuccessException(e);
        }
        
        boolean exists = false;
        try {
            exists = file.exists();
        } catch(Throwable e) {
            // ignore ...
        }
        
        if (!Flags.CREATE.isSet(flags) && ! exists) {
            throw new DoesNotExistException(name.toString()
                    + " does not exist");
        }

        if (Flags.CREATE.isSet(flags)
                && Flags.EXCL.isSet(flags) && exists) {
            throw new AlreadyExistsException(name.toString()
                    + " already exists");
        }

        try {
            isDirectory = exists && file.isDirectory();
        } catch (Throwable e) {
            // TODO: isDirectory fails, is a problem for GAT http adaptor.
            // For now, ignore the exception and assume that it is not a
            // directory.
            // throw new NoSuccess(e);
            logger.debug("isDirectory() gave exception! Assume non-directory",
                    e);
            isDirectory = false;
        }
        if (isDirectory && !isDir) {
            throw new BadParameterException(name.toString()
                    + " indicates a directory");
        }
        if (isDir && exists && ! isDirectory) {
            throw new BadParameterException(name.toString()
                    + " does not indicate a directory");
        }
    
        // Check existence of parent dir, and create it if required.
        if (Flags.CREATE.isSet(flags)) {
            File parentFile;
            try {
                parentFile = file.getParentFile();
            } catch (GATInvocationException e) {
                throw new NoSuccessException(e);
            }
            boolean parentExists = parentFile == null || parentFile.exists();
            if (Flags.CREATEPARENTS.isSet(flags) && ! parentExists) {
                parentExists = parentFile.mkdirs();
            }

            if (! parentExists) {
                throw new DoesNotExistException("Parent file does not exist: "
                        + name.toString());
            }
        }
        // below not specified...
        try {
            if (Flags.CREATE.isSet(flags) && ! exists) {
                if (!isDir) {
                    if (!file.createNewFile()) {
                        throw new NoSuccessException(
                                "Failed to create new file: " + name.toString());
                    }
                } else {
                    if (!file.mkdir()) {
                        throw new NoSuccessException(
                                "Failed to create directory: "
                                        + name.toString());
                    }
                }
            }
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        NSEntryAdaptor clone = (NSEntryAdaptor) super.clone();
        try {
            clone.gatURI = new URI(gatURI.toString());
        } catch (URISyntaxException e) {
            throw new SagaRuntimeException("Should not happen", e);
        }
        // Should we also clone fileImpl? I don't think it is needed.
        return clone;
    }

    public static URI cvtToGatURI(URL url) throws URISyntaxException {

        URI uri;
        String scheme = url.getScheme();
        String userInfo = url.getUserInfo();
        String host = url.getHost();
        int port = url.getPort();
        String path = url.getPath();
        String query = url.getQuery();
        String fragment = url.getFragment();

        StringBuffer u = new StringBuffer();
        if (scheme != null) {
            u.append(scheme);
            u.append(":");
        }
        if (host != null) {
            u.append("//");
            if (userInfo != null) {
                u.append(userInfo);
                u.append("@");
            }
            u.append(host);
            if (port >= 0) {
                u.append(":");
                u.append(port);
            }
        }

        if (scheme != null) {
            // This is the work-around to obtain uri's that
            // JavaGAT understands.
            if (host != null) {
                u.append("/");
            } else {
                u.append("///");
            }
        }
        u.append(path);
        if (query != null) {
            u.append("?");
            u.append(query);
        }
        if (fragment != null) {
            u.append("#");
            u.append(fragment);
        }

        uri = new URI(u.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("URL " + url + " converted to " + uri);
        }
        return uri;
    }

    public static URL cvtToSagaURL(URI uri) throws NotImplementedException,
            BadParameterException, NoSuccessException {
        return URLFactory.createURL(uri.toString()).normalize();
    }

    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        closed = true;
    }

    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException, DoesNotExistException {
        checkNotClosed();
        checkCopyFlags(flags);
        nonResolvingCopy(resolve(target), flags);
    }
    
    private File copyOrMoveChecks(URL target, int flags, String method) 
            throws IncorrectStateException, NoSuccessException,
            BadParameterException, AlreadyExistsException,
            IncorrectURLException, NotImplementedException,
            DoesNotExistException {
        if (closed) {
            if (logger.isDebugEnabled()) {
                logger.debug("Entry already closed!");
            }
            throw new IncorrectStateException("NSEntry already closed", wrapper);
        }
        int allowedFlags = Flags.CREATEPARENTS.or(Flags.RECURSIVE
                .or(Flags.OVERWRITE));
        // allowed flags: RECURSIVE, CREATE_PARENTS, OVERWRITE
        // otherwise: BadParameter();
        if ((allowedFlags | flags) != allowedFlags) {
            if (logger.isDebugEnabled()) {
                logger.debug("Wrong flags used!");
            }
            throw new BadParameterException(
                    "Flags not allowed for NSEntry " + method + ": " + flags, wrapper);
        }
        File targetFile = null;
        try {
            targetFile = GAT.createFile(gatContext, cvtToGatURI(target));

            if (logger.isDebugEnabled()) {
                logger.debug("targetFile: " + targetFile.toGATURI().toString());
            }
        } catch (GATObjectCreationException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Target file could not be created!");
            }
            throw new NoSuccessException(e, wrapper);
        } catch (URISyntaxException e) {
            throw new BadParameterException(e, wrapper);
        }

        // a 'BadParameter' exception is thrown if the source is a directory
        // and the 'Recursive' flag is not set

        if (isDirectory && !Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException(
                    "Source is a directory and recursive flag not set", wrapper);
        }

        // a 'BadParameter' exception is thrown if the source is not a directory
        // and the 'Recursive' flag is set

        if (!isDirectory && Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException(
                    "Source is not a directory and recursive flag is set",
                    wrapper);
        }

        if (isDirectory && Flags.RECURSIVE.isSet(flags) && targetFile.isFile()) {
            throw new AlreadyExistsException("cannot overwrite non-directory"
                    + targetFile.toGATURI().toString() + " with directory "
                    + fileImpl.toGATURI().toString(), wrapper);
        }
        // test whether target is in existing part of the name space
        File targetParentFile;
        File targetChildFile;
        try {
            if (targetFile.isDirectory() && file.isFile()) {
                targetParentFile = targetFile;
                try {
                    targetChildFile = GAT.createFile(gatContext, cvtToGatURI(target)
                            .toString()
                            + "/" + file.getName());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Creating target "
                                + targetChildFile.getName());
                    }
                } catch (GATObjectCreationException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("targetChildFile not created!");
                    }
                    throw new NoSuccessException(e, wrapper);
                }
            } else {
                targetParentFile = (org.gridlab.gat.io.File) targetFile
                        .getParentFile();
                if (targetParentFile == null) {
                    try {
                        targetParentFile = GAT.createFile(gatContext, ".");
                    } catch (GATObjectCreationException e) {
                        throw new NoSuccessException(e, wrapper);
                    }
                }
                targetChildFile = targetFile;
            }
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }  catch (URISyntaxException e) {
            throw new BadParameterException(e, wrapper);
        }
        if (!Flags.OVERWRITE.isSet(flags) && targetChildFile.exists()) {
            throw new AlreadyExistsException("Target already exists: "
                    + targetChildFile.toGATURI().toString(), wrapper);
        }
        if (Flags.CREATEPARENTS.isSet(flags)) {
            if (!targetParentFile.exists()) {
                if (!targetParentFile.mkdirs()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("targetParentFile mkdirs failed!");
                    }
                    throw new NoSuccessException(
                            "Failed to make non-existing directories", wrapper);
                }
            }
        } else {
            if (!targetParentFile.exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("targetParentFile does not exist!");
                }
                throw new DoesNotExistException(
                        "Target parent file does not exist", wrapper);
            }
        }
        return targetChildFile;
    }

    // Copy, without resolving target.
    // It has already been resolved, either with respect to this NSEntry or
    // with respect to the NSDirectory object that invoked this method.
    protected void nonResolvingCopy(URL target, int flags)
            throws IncorrectStateException, NoSuccessException,
            BadParameterException, AlreadyExistsException,
            IncorrectURLException, NotImplementedException,
            DoesNotExistException {
        File targetChildFile = copyOrMoveChecks(target, flags, "copy");

        // if the target already exists, it will be overwritten if the
        // 'Overwrite' flag is set, otherwise it is an 'AlreadyExists'
        // exception
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("copy: " + fileImpl.toGATURI().toString()
                        + " --> " + targetChildFile.toGATURI().toString());
            }
            file.copy(targetChildFile.toGATURI());
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
    }

    public URL getCWD() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("NSEntry already closed", wrapper);
        }
        String path = nameUrl.getPath();
        if (!isDirectory) {
            int i = path.lastIndexOf('/');
            if (i != -1) {
                path = path.substring(0, i);
            }
        }
        URL newURL = null;
        try {
            newURL = URLFactory.createURL(nameUrl.toString());
            newURL.setPath(path);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e, wrapper);
        }
        return newURL;
    }

    public URL getName() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("NSEntry already closed", wrapper);
        }
        String path = nameUrl.getPath();
        String[] s = path.split("/");

        try {
            return URLFactory.createURL(s[s.length - 1]);
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e, wrapper);
        }
    }

    public URL getURL() throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("NSEntry already closed", wrapper);
        }
        try {
            return URLFactory.createURL(nameUrl.normalize().toString());
        } catch (BadParameterException e) {
            throw new NoSuccessException("Unexpected error", e, wrapper);
        }
    }

    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("NSEntry already closed", wrapper);
        }
        return isDirectory;
    }

    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("NSEntry already closed", wrapper);
        }
        return !isDirectory;
    }

    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("isLink", wrapper);
    }

    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        throw new NotImplementedException("link", wrapper);
    }

    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException, DoesNotExistException {
        checkNotClosed();
        checkCopyFlags(flags);
        nonResolvingMove(resolve(target), flags);
    }

    protected void nonResolvingMove(URL target, int flags)
            throws IncorrectStateException, NoSuccessException,
            BadParameterException, AlreadyExistsException,
            NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            TimeoutException, IncorrectURLException, DoesNotExistException {
        File targetChildFile = copyOrMoveChecks(target, flags, "move");

        // if the target already exists, it will be overwritten if the
        // 'Overwrite' flag is set, otherwise it is an 'AlreadyExists'
        // exception
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("copy: " + fileImpl.toGATURI().toString()
                        + " --> " + targetChildFile.toGATURI().toString());
            }
            file.move(targetChildFile.toGATURI());
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
    }

    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("permissionsAllow", wrapper);
    }

    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("permissionsDeny", wrapper);
    }

    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("readLink", wrapper);
    }

    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (closed) {
            throw new IncorrectStateException("NSEntry already closed", wrapper);
        }
        int allowedFlags = Flags.DEREFERENCE.or(Flags.RECURSIVE);
        if ((allowedFlags | flags) != allowedFlags) {
            throw new BadParameterException(
                    "Flags not allowed for NSEntry remove: " + flags, wrapper);
        }
        if (isDirectory && !Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException(
                    "Target is directory and recursive flag not set", wrapper);
        }

        if (!isDirectory && Flags.RECURSIVE.isSet(flags)) {
            throw new BadParameterException(
                    "Target is not directory and recursive flag is set",
                    wrapper);
        }
        
        try {
            if (isDirectory && Flags.RECURSIVE.isSet(flags)) {
                file.recursivelyDeleteDirectory();
            }
            else if (!file.delete()) {
                throw new NoSuccessException("Remove operation failed!",
                        wrapper);
            }
        } catch (GATInvocationException e) {
            throw new NoSuccessException(e, wrapper);
        }
        close(0.0F);
    }

    public void permissionsAllow(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        try {
            permissionsAllow(id, permissions, Flags.NONE.getValue());
        } catch (IncorrectStateException e) {
            // This method cannot throw this, because it implements the method
            // as
            // specified in org.ogf.saga.permissions.Permissions.
            throw new NoSuccessException("Incorrect state", e, wrapper);
        }
    }

    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("permissionsCheck", wrapper);
    }

    public void permissionsDeny(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        try {
            permissionsDeny(id, permissions, Flags.NONE.getValue());
        } catch (IncorrectStateException e) {
            // This method cannot throw this, because it implements the method
            // as
            // specified in org.ogf.saga.permissions.Permissions.
            throw new NoSuccessException("Incorrect state", e, wrapper);
        }
    }

    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getGroup", wrapper);
    }

    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("getOwner", wrapper);
    }
}
