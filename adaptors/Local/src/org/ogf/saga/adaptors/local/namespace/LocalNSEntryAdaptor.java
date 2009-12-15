package org.ogf.saga.adaptors.local.namespace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.LocalAdaptorTool;
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
import org.ogf.saga.proxies.namespace.NSEntryWrapper;
import org.ogf.saga.spi.namespace.NSEntryAdaptorBase;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalNSEntryAdaptor extends NSEntryAdaptorBase {

    private static final Logger logger = LoggerFactory
            .getLogger(LocalNSEntryAdaptor.class);

    protected File file;
    protected AdaptorTool tool;
    protected boolean isDirectory;

    public LocalNSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        this(wrapper, sessionImpl, name, flags, false, LocalAdaptorTool
                .getInstance());
    }

    public LocalNSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags, boolean isDir, AdaptorTool tool)
            throws NotImplementedException, IncorrectURLException,
            BadParameterException, DoesNotExistException,
            PermissionDeniedException, AuthorizationFailedException,
            AuthenticationFailedException, TimeoutException,
            NoSuccessException, AlreadyExistsException {
        super(wrapper, sessionImpl, name, flags);

        URL nameUrl = getEntryURL();
        tool.checkURL(nameUrl);

        this.tool = tool;
        file = tool.createFile(nameUrl.getPath());

        boolean exists = file.exists();

        if (!Flags.CREATE.isSet(flags) && !exists) {
            throw new DoesNotExistException(name.toString() + " does not exist");
        }
        if (Flags.CREATE.isSet(flags) && Flags.EXCL.isSet(flags) && exists) {
            throw new AlreadyExistsException(name.toString()
                    + " already exists");
        }

        isDirectory = exists && file.isDirectory();

        if (isDirectory && !isDir) {
            throw new BadParameterException(name.toString()
                    + " indicates a directory");
        }
        if (isDir && exists && !isDirectory) {
            throw new BadParameterException(name.toString()
                    + " does not indicate a directory");
        }

        // create parent dir(s) if required
        if (Flags.CREATE.isSet(flags)) {
            File parentFile = file.getParentFile();

            boolean parentExists = parentFile == null || parentFile.exists();
            if (Flags.CREATEPARENTS.isSet(flags) && !parentExists) {
                parentExists = parentFile.mkdirs();
            }

            if (!parentExists) {
                throw new DoesNotExistException("Parent file does not exist: "
                        + name.toString());
            }
        }

        // create file or directory if required
        if (Flags.CREATE.isSet(flags) && !exists) {
            if (!isDir) {
                try {
                    if (!file.createNewFile()) {
                        throw new NoSuccessException(
                                "Failed to create new file: " + name.toString());
                    }
                } catch (IOException e) {
                    throw new NoSuccessException("Failed to create new file: "
                            + name.toString(), e);
                }
            } else {
                if (!file.mkdir()) {
                    throw new NoSuccessException("Failed to create directory: "
                            + name.toString());
                }
            }
        }
    }

    void init(File file, URL nameUrl) {
        this.file = file;
        setEntryURL(nameUrl);
    }
    
    public long getLength() {
        return file.length();
    }
    
    public RandomAccessFile createRandomAccessFile(String mode) 
    throws FileNotFoundException 
    {
        return new RandomAccessFile(file, mode);
    }
    
    @Override
    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException {
        super.close(timeoutInSeconds);
        tool.close(file);
    }

    @Override
    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        tool.checkURL(target);

        File targetFile = resolve(target.getPath());
        
        try {
            NSEntryData targetData = new NSEntryData(target, targetFile);
            nonResolvingCopy(targetData, flags);
        } finally {
            tool.close(targetFile);
        }
    }

    protected void nonResolvingCopy(NSEntryData target, int flags)
            throws DoesNotExistException, AlreadyExistsException,
            NoSuccessException, NotImplementedException,
            IncorrectStateException {
        
        target = copyOrMoveChecks(target, flags);

        if (isDirectory && Flags.RECURSIVE.isSet(flags)) {
            // copy recursively to the given target directory
            if (!target.getFile().exists()) {
                if (!target.getFile().mkdir()) {
                    throw new NoSuccessException("Cannot create directory: "
                            + target);
                }
            }
            tool.copyRecursively(file, target.getFile());
        } else {
            tool.copyBytes(file, target.getFile());
        }
    }

    @Override
    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        tool.checkURL(target);

        File targetFile = resolve(target.getPath());
        NSEntryData targetData = new NSEntryData(target, targetFile);

        nonResolvingMove(targetData, flags);
    }

    protected void nonResolvingMove(NSEntryData target, int flags)
            throws DoesNotExistException, AlreadyExistsException,
            NoSuccessException {

        target = copyOrMoveChecks(target, flags);
        
        logger.debug("Renaming '{}' to '{}'", file, target.getFile());

        if (!file.renameTo(target.getFile())) {
            throw new NoSuccessException("Could not move '" + getEntryURL()
                    + "' to '" + target.getURL() + "'");
        }
    }

    private NSEntryData copyOrMoveChecks(NSEntryData target, int flags)
            throws DoesNotExistException, NoSuccessException,
            AlreadyExistsException {

        if (!target.getFile().exists()) {
            createParentDirs(target, flags);
        } else {
            if (target.getFile().isDirectory()) {
                // move this entry into the target directory
                logger.debug("Copy this file into target directory {}", target
                        .getFile().getAbsolutePath());
                File targetFile = tool.createFile(target.getFile(), file
                        .getName());
                try {
                    URL targetUrl = (URL) target.getURL().clone();
                    URI fileURI = targetFile.toURI();
                    targetUrl.setPath(fileURI.getPath());
                    target = new NSEntryData(targetUrl, targetFile);
                } catch (Exception e) {
                    throw new NoSuccessException(
                            "Cannot create URL for target: " + target.getURL(),
                            e);
                }
            } else if (isDirectory) {
                // cannot move this directory over the existing target file
                throw new AlreadyExistsException("Target is an existing file");
            }

            if (target.getFile().exists() && !Flags.OVERWRITE.isSet(flags)) {
                // don't overwrite an existing file without the 'Overwrite' flag
                throw new AlreadyExistsException("Target already exists: "
                        + target.getURL());
            }
        }

        return target;
    }

    /**
     * Create parent directories of the given target file, if required
     */
    protected void createParentDirs(NSEntryData target, int flags)
            throws DoesNotExistException {
        File parentFile = target.getFile().getParentFile();

        if (parentFile != null && !parentFile.exists()) {
            // no parent directories; we have to create them
            if (Flags.CREATEPARENTS.isSet(flags)) {
                logger.debug("Creating parent directories of {}", parentFile);

                if (!parentFile.mkdirs()) {
                    throw new DoesNotExistException(
                            "Cannot create parent directories of target: "
                                    + target.getURL());
                }
            } else {
                throw new DoesNotExistException("Target does not exist: "
                        + target.getURL());
            }
        }
    }

    /**
     * Resolves the given path against the current working directory of this
     * NSEntry
     * 
     * @return the resolved File object
     */
    protected File resolve(String path) {
        File f = tool.createFile(path);

        if (f.isAbsolute()) {
            return f;
        } else if (isDirectory) {
            return tool.createFile(file, path);
        } else {
            String cwd = file.getParent();
            return tool.createFile(cwd, path);
        }
    }

    @Override
    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return isDirectory;
    }

    @Override
    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        return !isDirectory;
    }

    @Override
    public boolean isLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("Links are not supported");
    }

    @Override
    public void link(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException, TimeoutException,
            NoSuccessException, IncorrectURLException {
        throw new NotImplementedException("Links are not supported");
    }

    @Override
    public void permissionsAllow(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            IncorrectStateException, BadParameterException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("Permissions are not supported");
    }

    @Override
    public void permissionsDeny(String id, int permissions, int flags)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, IncorrectStateException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException {
        throw new NotImplementedException("Permissions are not supported");
    }

    @Override
    public URL readLink() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        throw new NotImplementedException("Links are not supported");
    }

    @Override
    public void remove(int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException {

        if (isDirectory && Flags.RECURSIVE.isSet(flags)) {
            removeRecursively(file);
        } else {
            logger.debug("Removing '{}'", file);
            
            if (!file.delete()) {
                throw new NoSuccessException("Remove failed");
            }
        }

        logger.debug("Closing '{}'", file);
        
        close(0.0F);
    }

    private boolean removeRecursively(File dir) {
        String[] entries = dir.list();

        for (int i = 0; i < entries.length; i++) {
            File entry = tool.createFile(dir, entries[i]);

            if (entry.isDirectory()) {
                if (!removeRecursively(entry)) {
                    logger.debug("Could not remove dir {}", entry);
                    return false;
                }
            } else {
                logger.debug("Removing '{}'", entry);
                
                if (!entry.delete()) {
                    logger.debug("Could not remove file {}", entry);
                    return false;
                }
            }
        }

        logger.debug("Removing '{}'", dir);
        return dir.delete();
    }

    @Override
    public String getGroup() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Permissions are not supported");
    }

    @Override
    public String getOwner() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Permissions are not supported");
    }

    @Override
    public boolean permissionsCheck(String id, int permissions)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException {
        throw new NotImplementedException("Permissions are not supported");
    }

}
