package org.ogf.saga.adaptors.local.namespace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;

import org.ogf.saga.adaptors.local.LocalAdaptor;
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

public class NSEntryAdaptor extends NSEntryAdaptorBase {

    private static final Logger logger = LoggerFactory
            .getLogger(NSEntryAdaptor.class);

    protected File file;
    protected boolean isDirectory;

    public NSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        this(wrapper, sessionImpl, name, flags, false);
    }

    NSEntryAdaptor(NSEntryWrapper wrapper, SessionImpl sessionImpl,
            URL name, int flags, boolean isDir) throws NotImplementedException,
            IncorrectURLException, BadParameterException,
            DoesNotExistException, PermissionDeniedException,
            AuthorizationFailedException, AuthenticationFailedException,
            TimeoutException, NoSuccessException, AlreadyExistsException {
        super(wrapper, sessionImpl, name, flags);
        
        URL nameUrl = getEntryURL();

        LocalAdaptor.checkURL(nameUrl);

        file = new File(nameUrl.getPath());

        logger.debug("Using local file: {}", file);
        
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
        setNameURL(nameUrl);
    }
    
    @Override
    public void copy(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        LocalAdaptor.checkURL(target);
        checkNotClosed();
        checkCopyFlags(flags);
        checkDirectoryFlags("Target", flags, isDirectory);
        
        File targetFile = resolve(target.getPath());
        
        nonResolvingCopy(target, targetFile, flags);
    }
    
    protected void nonResolvingCopy(URL target, File targetFile, int flags)
            throws DoesNotExistException, AlreadyExistsException,
            NoSuccessException, NotImplementedException,
            IncorrectStateException {
        if (!targetFile.exists()) {
            createParentDirs(target, targetFile, flags);
        } else {
            if (targetFile.isDirectory()) {
                // copy this file into the target directory
                targetFile = new File(targetFile, file.getName());
                try {
                    target = (URL)target.clone();
                    URI fileURI = targetFile.toURI();
                    target.setPath(fileURI.getPath());
                } catch (Exception e) {
                    throw new NoSuccessException(
                            "Cannot create URL for target", e);
                }
            } else if (isDirectory) {
                // cannot copy this directory over the existing target file
                throw new AlreadyExistsException("Target is an existing file");
            }
        
            if (targetFile.exists() && !Flags.OVERWRITE.isSet(flags)) { 
                // don't overwrite an existing file without the 'Overwrite' flag 
                throw new AlreadyExistsException("Target already exists: "
                    + target);
            }
        }
        
        if (isDirectory && Flags.RECURSIVE.isSet(flags)) {
            // copy recursively to the given target directory
            if (!targetFile.exists()) {
                if (!targetFile.mkdir()) {
                    throw new NoSuccessException("Cannot create directory: "
                            + targetFile);
                }
            }
            copyRecursively(file, targetFile);
        } else {
            copyBytes(file, targetFile);
        }
    }
    
    /**
     * Copies all entries in sourceDir to targetDir. Directories are copied
     * recursively. Precondition: the targetDir exists.
     * 
     * @param sourceDir
     *            the source directory to copy recursively
     * @param targetDir
     *            the target directory, which must not exist yet
     * @throws NoSuccessException
     */
    private void copyRecursively(File sourceDir, File targetDir)
            throws NoSuccessException {

        String[] sources = sourceDir.list();

        for (int i = 0; i < sources.length; i++) {
            File source = new File(sourceDir, sources[i]);
            File target = new File(targetDir, sources[i]);

            if (source.isFile()) {
                copyBytes(source, target);
            } else {
                if (!target.mkdir()) {
                    throw new NoSuccessException("Cannot create directory: "
                            + target);
                }
                copyRecursively(source, target);
            }
        }
    }
            
    private void copyBytes(File from, File to)
            throws NoSuccessException {

        logger.debug("Copying '{}' to '{}'", from, to);

        try {
            FileChannel fromChannel = null;
            FileChannel toChannel = null;
            try {
                fromChannel = new FileInputStream(from).getChannel();
                toChannel = new FileOutputStream(to).getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);
            } finally {
                if (fromChannel != null)
                    fromChannel.close();
                if (toChannel != null)
                    toChannel.close();
            }
        } catch (IOException e) {
            throw new NoSuccessException("Error during copy from '" + from + "' to '" + to + "'");
        }
    }

    @Override
    public void move(URL target, int flags) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, AlreadyExistsException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectURLException {
        LocalAdaptor.checkURL(target);
        checkNotClosed();
        checkCopyFlags(flags);
        checkDirectoryFlags("Target", flags, isDirectory);
        
        File targetFile = resolve(target.getPath());
        
        nonResolvingMove(target, targetFile, flags);
    }
    
    protected void nonResolvingMove(URL target, File targetFile, int flags)
            throws DoesNotExistException, AlreadyExistsException,
            NoSuccessException {
        if (!targetFile.exists()) {
            createParentDirs(target, targetFile, flags);
        } else if (targetFile.isDirectory()) {
            // target is an existing directory; we should copy into it
            targetFile = new File(targetFile, file.getName());
        } else {
            // target is an existing file; overwriting it requires the 
            // 'Overwrite' flag
            if (!Flags.OVERWRITE.isSet(flags)) {
                throw new AlreadyExistsException("Target already exists: " 
                        + target);
            }
            // explicitly remove the target first
            logger.debug("Removing target: {}", targetFile);
            if (!targetFile.delete()) {
                throw new NoSuccessException("Could not remove target: "
                        + targetFile);
            }
        }
        
        logger.debug("Renaming '{}' to '{}'", file, targetFile);
        
        if (!file.renameTo(targetFile)) {
            throw new NoSuccessException("Could not move '" + getEntryURL()
                    + "' to '" + target + "'");
        }
    }

    /**
     * Create parent directories of the given target file, if required
     */
    protected void createParentDirs(URL target, File targetFile, int flags)
            throws DoesNotExistException {
        File parentFile = targetFile.getParentFile();

        if (parentFile != null && !parentFile.exists()) {
            // no parent directories; we have to create them
            if (Flags.CREATEPARENTS.isSet(flags)) {
                logger.debug("Creating parent directories of {}", parentFile);

                if (!parentFile.mkdirs()) {
                    throw new DoesNotExistException(
                            "Cannot create parent directories of target: "
                                    + target);
                }
            } else {
                throw new DoesNotExistException("Target does not exist: "
                        + target);
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
        File f = new File(path);
        
        if (f.isAbsolute()) {
            return f;
        } else if (isDirectory) {
            return new File(file, path);
        } else {
            String cwd = file.getParent();
            return new File(cwd, path);            
        }
    }
    
    @Override
    public boolean isDir() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        checkNotClosed();
        return isDirectory;
    }

    @Override
    public boolean isEntry() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        checkNotClosed();
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
        checkNotClosed();
        checkRemoveFlags(flags);
        checkDirectoryFlags("NSEntry", flags, isDirectory);
                
        if (isDirectory && Flags.RECURSIVE.isSet(flags)) {
            removeRecursively(file);
        } else {
            logger.debug("Removing '{}'", file);;
            if (!file.delete()) {
                throw new NoSuccessException("Remove failed");
            }
        }

        logger.debug("Closing '{}'", file);;
        close(0.0F);
    }
    
    private static boolean removeRecursively(File dir) {
        String[] entries = dir.list();
        
        for (int i = 0; i < entries.length; i++) {
            File entry = new File(entries[i]);
            
            if (entry.isDirectory()) {
                if (!removeRecursively(entry)) {
                    return false;
                }
            }
        }
    
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

    public long getSize() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException {
        checkNotClosed();
        
        return file.length();
    }
    
}
