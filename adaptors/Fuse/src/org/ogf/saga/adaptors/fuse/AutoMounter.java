package org.ogf.saga.adaptors.fuse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    static Logger logger = LoggerFactory.getLogger(AutoMounter.class);

    protected AutoMounter() {
        Properties sagaProp = SagaProperties.getDefaultProperties();
        config = new FuseAdaptorConfig(sagaProp);
        mounted = new HashMap<String, MountInfo>();
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
    
    public boolean isAcceptedContextType(String type) {
    	if (type == null) {
    		return false;
    	} else {
    		return config.isAcceptedContextType(type);
    	}
    }
    
    public List<String> getAllAcceptedContextTypes() {
    	return config.getAllAcceptedContextTypes();
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
        
        // try a mount for each accepted context, including none 
        for (Context c: contextList) {
            if (fs.acceptContext(c)) {
                if (logger.isDebugEnabled()) {
					logger.debug("Trying to mount " + u + " with "
							+ fs.getName() + " and context " + getType(c));
                }
                try {
                    MountInfo info = parseWithContext(fs, u, c);

                    if (!mounted.containsKey(info.mountCommand)) {
                    	createMountPoint(info.mountPoint);
                    	
                    	// mount the remote filesystem
                        try {
							RunSagaCommand.execute(info.mountCommand,
									info.mountInput);
                        	mounted.put(info.mountCommand, info);
                        } catch (SagaException e) {
                        	deleteDirectory(info.mountPoint);
                        	throw e;
                        }
                    } else {
                        // use existing mounted filesystem
                        logger.debug("Using existing filesystem mounted at: {}", 
                        		info.mountPoint); 
                    }
                    
                    return info.mountCommand;
                } catch (SagaException e) {
                    logger.debug("Failed to mount " + u 
                            + " with context " + getType(c), e);
                    errors.add(e);
                }
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

    
//            String mountpoint = null;
//            try {
//                mountpoint = fs.parseMountpoint(fs, u, s);
//            } catch (PropertyParseException e) {
//                String msg = fs + ": cannot parse mountpoint";
//                logger.debug(msg, e);
//                NoSuccessException x = new NoSuccessException(msg, e); 
//                errors.add(x);
//                continue;
//            }
//            
//            File mountDir = new File(mountpoint);
//                
//            if (!mounted.containsKey(mountDir)) {
//                // new remote volume, mount it
//                logger.info(fs + " mounts " + u + " at " + mountDir);
//                Context[] contexts = s.listContexts();
//                try {
//                    MountData data = fs.mount(u, mountDir, contexts);
//                    mounted.put(mountDir, data);
//                } catch (SagaException e) {
//                    logger.debug(fs + ": mount failed", e);
//                    errors.add(e);
//                    continue;
//                }
//            }
//    
//            String remotePath = "/";
//            try {
//                String urlPath = u.getPath();
//                if (!urlPath.isEmpty()) {
//                    // use canonical path to remove all redundant /..
//                    remotePath = new File(urlPath).getCanonicalPath();
//                }
//            } catch (IOException e) {
//                logger.error("Cannot retrieve canonical path of "
//                        + remotePath);
//            }
//                localPath = new File(mountDir, remotePath).getPath();
//                break;
//            }
//        } else {
//            // local url
//            localPath = u.getPath();
//        }
//
//        // use the internal scheme in the delegate URL to avoid an endless loop
//        // in adaptor loading.
//        URL localUrl = URLFactory.createURL(config.getDelegateScheme() 
//                + "://localhost" + localPath);
//
//        return localUrl;
//    }

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

    /**
     * Translates a local URL to a possible remote counterpart. If the entry is
     * located in a mountpoint directory, the URL is translated to a remote
     * URL. Otherwise, the scheme of the local URL is set to 'file' and 
     * then returned.
     * 
     * @param remoteBase
     *            the remote base URL to resolve rhs against
     * @param rhs
     *            a local URL that possibly refers to a remote but locally
     *            mounted entry
     * 
     * @return an absolute URL that refers to an entry in a remote filesystem
     * 
     * @throws IncorrectURLException
     * @throws BadParameterException
     * @throws NotImplementedException
     * @throws NoSuccessException
     */
//    public synchronized URL resolveRemoteURL(String mountId, URL remoteBase, 
//            URL rhs) throws NoSuccessException, BadParameterException {
//
//        File rhsPath = new File(rhs.getPath());
//        URL volumeUrl = AutoMounter.getInstance().getVolume(rhsPath);
//
//        if (volumeUrl == null) {
//            // rhs points to a local file outside a mounted directory
//
//            if (Constants.INTERNAL_SCHEME.equals(rhs.getScheme())) {
//                // change scheme of delegate URL to generic 'file' scheme
//                URL result = URLFactory.createURL(rhs.getString());
//                result.setScheme(Constants.FILE_SCHEME);
//                return result;
//            } else {
//                return rhs;
//            }
//        }
//
//        File mountedDir = AutoMounter.getInstance().getMountPoint(volumeUrl);
//        if (mountedDir == null) {
//            throw new NoSuccessException("Unexpected error: mounted dir of "
//                    + "volume " + volumeUrl + " is unknown");
//        }
//
//        String mountedDirStr = mountedDir.getPath();
//        String rhsPathStr = rhsPath.getPath();
//
//        if (rhsPathStr.length() < mountedDirStr.length()) {
//            throw new NoSuccessException("Unexpected error: rhs path '"
//                    + rhsPathStr + "' < mounted dir '" + mountedDirStr + "'");
//        }
//
//        String remotePath = rhsPathStr.substring(mountedDirStr.length());
//
//        URL remoteUrl = volumeUrl;
//        remoteUrl.setPath(remotePath);
//        AutoMounter.getInstance().removeDefaultPort(remoteUrl);
//
//        logger.debug("resolved '" + rhs + "' to remote url '" + remoteUrl + "'");
//
//        return remoteUrl;
//    }
    
//    public synchronized URL OLDmount(URL u, Session s)
//            throws PermissionDeniedException, TimeoutException,
//            NoSuccessException, BadParameterException {
//        String scheme = u.getScheme();
//        List<Filesystem> filesystems = config.getFilesystems(scheme);
//        
//        String localPath = null;
//
//        if (!filesystems.isEmpty()) {
//            // remote url
//            List<SagaException> errors = new LinkedList<SagaException>();
//            
//            for (Filesystem fs: filesystems) {
//                String mountpoint = null;
//                try {
//                    mountpoint = parseMountpoint(fs, u, s);
//                } catch (PropertyParseException e) {
//                    String msg = fs + ": cannot parse mountpoint";
//                    logger.debug(msg, e);
//                    NoSuccessException x = new NoSuccessException(msg, e); 
//                    errors.add(x);
//                    continue;
//                }
//                
//                File mountDir = new File(mountpoint);
//                    
//                if (!mounted.containsKey(mountDir)) {
//                    // new remote volume, mount it
//                    logger.info(fs + " mounts " + u + " at " + mountDir);
//                    Context[] contexts = s.listContexts();
//                    try {
//                        MountData data = fs.mount(u, mountDir, contexts);
//                        mounted.put(mountDir, data);
//                    } catch (SagaException e) {
//                        logger.debug(fs + ": mount failed", e);
//                        errors.add(e);
//                        continue;
//                    }
//                }
//        
//                String remotePath = "/";
//                try {
//                    String urlPath = u.getPath();
//                    if (!urlPath.isEmpty()) {
//                        // use canonical path to remove all redundant /..
//                        remotePath = new File(urlPath).getCanonicalPath();
//                    }
//                } catch (IOException e) {
//                    logger.error("Cannot retrieve canonical path of "
//                            + remotePath);
//                }
//                localPath = new File(mountDir, remotePath).getPath();
//                break;
//            }
//        } else {
//            // local url
//            localPath = u.getPath();
//        }
//
//        // use the internal scheme in the delegate URL to avoid an endless loop
//        // in adaptor loading.
//        URL localUrl = URLFactory.createURL(config.getDelegateScheme() 
//                + "://localhost" + localPath);
//
//        return localUrl;
//    }

//    public File getMountpoint(URL url, Session s) throws NoSuccessException {
//        try {
//            String mountpoint = parseMountpoint(fs, u, s);
//            return new File(mountpoint);
//        } catch (PropertyParseException e) {
//            String msg = fs + ": cannot parse mountpoint";
//            logger.debug(msg, e);
//            NoSuccessException x = new NoSuccessException(msg, e); 
//            errors.add(x);
//            continue;
//        }
//        
//    }
//    
    
//    public void umount(URL url, Context c) throws NoSuccessException,
//            PermissionDeniedException, TimeoutException {
//
//        String cmd = parseCommandWithContext(umountCommand, url, c);
//        execute(cmd);
//    }

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
