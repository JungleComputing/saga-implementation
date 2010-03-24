package org.ogf.saga.adaptors.fuse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ogf.saga.adaptors.fuse.properties.FusePropertyParser;
import org.ogf.saga.adaptors.fuse.properties.PropertyParseException;
import org.ogf.saga.adaptors.fuse.util.RunSagaCommand;
import org.ogf.saga.adaptors.fuse.util.URLUtil;
import org.ogf.saga.bootstrap.SagaProperties;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.session.Session;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class AutoMounter {

    public static final String FILE_SCHEME = "file";
    public static final String ANY_SCHEME = "any";
    public static final String LOCAL_HOST = "localhost";

    private static AutoMounter uniqueInstance = null;

    private final FuseAdaptorConfig config;
    private final Map<String, MountInfo> mounted;
    private final Set<File> activeMountPoints;

    static Logger logger = LoggerFactory.getLogger(AutoMounter.class);

    protected AutoMounter() {
        Properties sagaProp = SagaProperties.getDefaultProperties();
        config = new FuseAdaptorConfig(sagaProp);
        mounted = new HashMap<String, MountInfo>();
        activeMountPoints = new HashSet<File>();
        Runtime.getRuntime().addShutdownHook(new Unmounter());
    }

    public static synchronized AutoMounter getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new AutoMounter();
        }
        return uniqueInstance;
    }

    public void validateUrl(URL url) throws IncorrectURLException {
        if (URLUtil.isLocal(url)) {
            // local files are OK
            return;
        }
        
        // Otherwise, we accept all configured schemes
        String scheme = url.getScheme();
        if (!config.isAcceptedScheme(scheme)) {
			throw new IncorrectURLException("Unknown scheme: '" + scheme
					+ "', the FUSE adaptor only accepts "
					+ config.getAllAcceptedSchemes());
        }
    }
    
    public boolean isAbsorbedContextType(String type) {
    	if (type == null) {
    		return false;
    	} else {
    		return config.isAbsorbedContextType(type);
    	}
    }
    
    public List<String> getAllAbsorbedContextTypes() {
    	return config.getAllAbsorbedContextTypes();
    }

    public synchronized String mount(URL u, Session s)
            throws NoSuccessException, TimeoutException,
            PermissionDeniedException, IncorrectURLException {

        if (URLUtil.isLocal(u)) {
            // local URL; no need to mount anything
            logger.debug("Skipping mount of local URL: {}", u);
            return null;
        }

        // remote URL; try a mount with all filesystems that support the URL scheme
        String scheme = u.getScheme();
        List<FsInfo> filesystems = config.getFilesystems(scheme);

        if (filesystems == null || filesystems.isEmpty()) {
            logger.debug("Unknown scheme '{}', cannot mount URL: {}", scheme, u);
            List<String> accepted = config.getAllAcceptedSchemes();
            throw new IncorrectURLException("Unknown scheme: '" + scheme + 
                    "', the FUSE adaptor only accepts " + accepted); 
        }
        
        List<SagaException> errors = new LinkedList<SagaException>();
        
        for (FsInfo fs: filesystems) {
            String mountCmd = mount(u, s, fs, errors);
            
            if (mountCmd != null) {
                // successful mount, or reusing existing mount point
                return mountCmd;
            }
        }
        
        // by now the mount failed 
        
        if (errors.isEmpty()) {
            // no exceptions?
            throw new NoSuccessException("No filesystem could mount: " + u);
        } else {
            throwNestedException(errors);
        }

        // never reached
        return null;
    }
    
    private void throwNestedException(List<SagaException> errors) 
            throws NoSuccessException, TimeoutException,
            PermissionDeniedException, IncorrectURLException {
        Collections.sort(errors);
        SagaException top = errors.remove(0);
       
        if (top instanceof NoSuccessException) {
            throw nest((NoSuccessException)top, errors);
        } else if (top instanceof TimeoutException) {
            throw nest((TimeoutException)top, errors);
        } else if (top instanceof PermissionDeniedException) {
            throw nest((PermissionDeniedException)top, errors);
        } else if (top instanceof IncorrectURLException) {
            throw nest((IncorrectURLException)top, errors);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Converting " + top.getClass()
                        + " to NoSuccessException");
            }
            NoSuccessException e = new NoSuccessException(top.getMessage(), 
                    top.getCause());
            throw nest(e, errors);
        }
    }
    

    private static <T extends SagaException> T nest(T e, List<SagaException> l) {
        for (SagaException nested: l) {
            e.addNestedException(nested);
        }
        return e;
    }
    
    private String mount(URL u, Session s, FsInfo fs, 
            List<SagaException> errors) { 
        // create a list of all contexts, plus <null> to try a mount without
        // any context after all contexts have been tried
        List<Context> contextList = new ArrayList<Context>();
        Collections.addAll(contextList, s.listContexts());
        contextList.add(null);
        
        // create the mount command for each context; if the mount command
        // maps to an existing one we can return its mount point immediately
        List<MountInfo> mountInfos = new ArrayList<MountInfo>(contextList.size());
        for (Context c: contextList) {
            if (fs.acceptContext(c)) {
                try {
                    MountInfo info = parseWithContext(fs, u, c);
                    if (mounted.containsKey(info.mountCommand)) {
                        // remote file system is already mounted
                        return info.mountCommand;
                    } else {
                        mountInfos.add(info);
                    }
                } catch (SagaException e) {
                    logger.debug("Cannot mount " + u + " with context " 
                            + getType(c), e);
                    errors.add(e);
                }
            }
        }
        
        // no existing mount point found; try all parsed mount commands 
        for (MountInfo info: mountInfos) {
            logger.debug("Trying to mount {} with {}", u, fs.getName());
            try {
                createMountPoint(info.mountPoint);
                    	
                RunSagaCommand.execute(info.mountCommand, info.mountInput);

                mounted.put(info.mountCommand, info);
                activeMountPoints.add(info.mountPoint);
                    
                return info.mountCommand;
            } catch (SagaException e) {
                // mount went wrong; delete the created mount point
                // unless it's already used by another mount
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to mount " + u + " with " 
                            + fs.getName(), e);
                }
                if (!activeMountPoints.contains(info.mountPoint)) {
                    deleteDirectory(info.mountPoint);
                }
                errors.add(e);
            }
        }
        
        // mount failed
        return null;
    }

	private void createMountPoint(File dir) throws NoSuccessException {
		if (!dir.exists()) {
			// create the mount point
			logger.debug("Creating mount point: " + dir);
			if (!dir.mkdirs()) {
				throw new NoSuccessException("Failed to create mount point: "
						+ dir);
			}
		} else if (dir.isDirectory()) {
			// mount point is an existing directory; reuse it
			logger.debug("Reusing existing mount point: {}", dir);
		} else {
			// mount point is an existing file
			throw new NoSuccessException("Mount point is an existing file: "
					+ dir);
		}
	}
        
    private String getType(Context c) {
        if (c == null) {
            return "<none>";
        }
        try {
            return c.getAttribute(Context.TYPE);
        } catch (SagaException e) {
            logger.debug("Failed to get type of context");
            return "<unknown>";
        }
    }

    private MountInfo parseWithContext(FsInfo fs, URL url, Context c) 
    		throws NoSuccessException, PermissionDeniedException,
            TimeoutException {

        String parsedMountDir = parse(fs.getMountDir(), fs, url, c, null);
        String parsedMountPoint = parse(fs.getMountPoint(), fs, url, c, null);

        File mountFile = new File(parsedMountDir, parsedMountPoint);
        String mountPath = mountFile.getAbsolutePath();

        String mountCmd = parse(fs.getMountCommand(), fs, url, c, mountPath);
        String mountInput = parse(fs.getMountInput(), fs, url, c, mountPath);
        String umountCmd = parse(fs.getUmountCommand(), fs, url, c, mountPath);
                
        return new MountInfo(mountCmd, mountInput, umountCmd, mountFile);
    }

    private String parse(String s, FsInfo fs, URL url, Context c, 
            String parsedMountPoint) throws NoSuccessException {
        try {
            FusePropertyParser p = new FusePropertyParser(s, fs.getName(),
                    parsedMountPoint, c);
            return p.parse(url);
        } catch (PropertyParseException e) {
            throw new NoSuccessException("Cannot parse: " + s, e);
        }
    }

    public synchronized URL resolveLocalURL(String mountId, URL url)
            throws NoSuccessException, BadParameterException {
        
        String localPath = null;
        
        if (mountId == null) {
            // URL is local and has not been mounted
            localPath = url.getPath(); 
        } else {
            MountInfo info = mounted.get(mountId);
            if (info == null) {
                throw new NoSuccessException("No mountpoint for: " + mountId);
            }
        
            String pathInVolume = "/";
            String urlPath = url.getPath();

            if (!urlPath.isEmpty()) {
                try {
                    // use canonical path to remove all redundant /..
                    pathInVolume = new File(urlPath).getCanonicalPath();
                } catch (IOException e) {
                    throw new NoSuccessException("Cannot retrieve canonical path of " 
                        + urlPath);
                }
            }
            localPath = new File(info.mountPoint, pathInVolume).getPath();
        }

        // use the internal scheme in the delegate URL to avoid an endless loop
        // in adaptor loading.
        URL localUrl = URLFactory.createURL(config.getDelegateScheme() 
                + "://" + LOCAL_HOST + localPath);

        return localUrl;   
    }
    
    /**
     * Returns the local counterpart of the given URL. If the given URL is
     * already a local one, the same URL is returned. If the given URL is a
     * remote one, the remote volume is mounted and a URL to the locally mounted
     * entry is returned. If the given URL is relative, it is resolved against
     * the base directory.
     * 
     * @param base
     *            the base url to resolve rhs against
     * @param rhs
     *            the possibly remote URL used as a right-hard side parameter.
     * 
     * @return an absolute URL to a local file.
     * 
     * @throws IncorrectURLException
     * @throws BadParameterException
     * @throws NotImplementedException
     * @throws NoSuccessException
     * @throws TimeoutException
     * @throws AlreadyExistsException
     * @throws PermissionDeniedException
     */
    public URL resolveLocalURL(URL base, URL rhs, String mountId, Session s)
            throws IncorrectURLException, BadParameterException,
            NoSuccessException, PermissionDeniedException, TimeoutException {
        
        validateUrl(rhs);

        logger.debug("rhs.isRelative=" + URLUtil.isRelative(rhs)); 
        logger.debug("base.isLocal=" + URLUtil.isLocal(base)); 
        
        if (URLUtil.isRelative(rhs) && !URLUtil.isLocal(base)) {
            // resolve url relative to our remote base
            File basePath = new File(URLUtil.getPathSafe(base));
            File rhsPath = new File(URLUtil.getPathSafe(rhs));
            File pathInVolume = null;

            logger.debug("basePath=" + basePath + ", rhsPath=" + rhsPath);
            
            if (rhsPath.isAbsolute()) {
                // absolute path in remote volume, which completely replaces
                // the base path
                logger.debug("pathInVolume is absolute");
                pathInVolume = rhsPath;
            } else {
                // relative path in remote volume: add it to the url path
                if (basePath.isAbsolute()) {
                    logger.debug("basePath is absolute");
                    pathInVolume = basePath;
                } else {
                    logger.debug("basePath is relative");
                    pathInVolume = new File("/", basePath.getPath());
                }
                pathInVolume = new File(pathInVolume, rhsPath.getPath());
            }

            logger.debug("pathInVolume=" + pathInVolume);
            String normPath = URLUtil.normalizePath(pathInVolume.getPath());
            logger.debug("normPath=" + normPath);

            MountInfo info = mounted.get(mountId);
            if (info == null) {
                throw new NoSuccessException("No mountpoint for: " + mountId);
            }
            
            File localPath = new File(info.mountPoint, normPath);
            logger.debug("localPath=" + localPath);

            URL result = URLFactory.createURL(config.getDelegateScheme() + "://" 
                    + LOCAL_HOST);
            result.setPath(localPath.getPath());
            
            return result;
        } else if (URLUtil.isLocal(rhs)) {
            // rhs is a local url, do not change it
            return rhs;
        } else {
            // mount remote volume and return the local mount point
            String rhsMountId = mount(rhs, s); 
            return resolveLocalURL(rhsMountId, rhs);
        }
    }

    public synchronized void unmount(String mountId) {
        boolean unmounted = false;
        
        MountInfo info = mounted.get(mountId);
        
        try {
            if (info == null) {
                logger.debug("Unknown mount ID: " + mountId);
                return;
            }

            logger.info("Unmounting {}", info.mountPoint);
            RunSagaCommand.execute(info.umountCommand);
            
            unmounted = true;
        } catch (Throwable e) {
            logger.error("Failed to unmount: " + info.mountPoint, e);
        }

        if (unmounted) {
            deleteDirectory(info.mountPoint);
            mounted.remove(mountId);
            activeMountPoints.remove(info.mountPoint);
        }
    }

	private void deleteDirectory(File dir) {
		try {
		    logger.info("Deleting " + dir);
		    if (!dir.delete()) {
		        logger.error("Failed to delete: " + dir);
		    }
		} catch (Throwable e) {
		    logger.error("Failed to delete: " + dir, e);
		}
	}
    
    public synchronized void unmountAll() {
        // prevent busy mount points: garbage collect open files
        System.gc();

        // use a copy of the keys to prevent ConcurrentModificationExceptions
        List<String> allMountIds = new LinkedList<String>(mounted.keySet());
        for (String mountId: allMountIds) {
            unmount(mountId);
        }

        mounted.clear();
    }

    // INNER CLASSES

    private class Unmounter extends Thread {

        @Override
        public void run() {
            unmountAll();
        }
    }
       
}
