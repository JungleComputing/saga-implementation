package org.ogf.saga.adaptors.fuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ogf.saga.adaptors.fuse.properties.FusePropertyParser;
import org.ogf.saga.adaptors.fuse.util.RunProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuseAdaptorConfig {

    private Logger logger = LoggerFactory.getLogger(FuseAdaptorConfig.class);
    
    static final String PREFIX = "saga.adaptor.fuse.";
    static final String DELEGATE_SCHEME = PREFIX + "delegate.scheme"; 
    static final String MOUNT_DIR = PREFIX + "mount.dir";
    static final String FILESYSTEMS = PREFIX + "fs";
    static final String FS_SCHEMES = "schemes";
    static final String FS_CONTEXTS = "contexts";
    static final String FS_MOUNT_CMD = "mount.command";
    static final String FS_MOUNT_INPUT = "mount.input";
    static final String FS_MOUNT_POINT = "mount.point";
    static final String FS_UMOUNT_CMD = "umount.command";
    
    static final String DEFAULT_DELEGATE_SCHEME = "local";
    static final String DEFAULT_MOUNT_DIR = FusePropertyParser.VAR_JAVA_TMPDIR;
    
    // separator between schemes and context types
    private static final String LIST_SEPARATOR = ",";
    
    private String delegateScheme;

    private final Map<String, List<FsInfo>> fsMap;
    private final Set<String> acceptedContextTypes;
    
    public FuseAdaptorConfig(Properties p) {
        logger.debug("FUSE adaptor configuration:");
        
        delegateScheme = p.getProperty(DELEGATE_SCHEME, DEFAULT_DELEGATE_SCHEME);
        logger.debug("- delegate scheme: " + delegateScheme);
        
        String mountDir = p.getProperty(MOUNT_DIR, DEFAULT_MOUNT_DIR);
        logger.debug("- mount dir syntax: " + mountDir);
    
        String filesystems = p.getProperty(FILESYSTEMS);
        
        fsMap = new HashMap<String, List<FsInfo>>();
        acceptedContextTypes = new HashSet<String>();
        
        if (filesystems == null || filesystems.trim().isEmpty()) {
            logger.warn("No FUSE filesystems specified");
        } else {
            for (String fs: filesystems.split(",")) {
                String mountCmd = p.getProperty(fsProperty(fs, FS_MOUNT_CMD));
                String umountCmd = p.getProperty(fsProperty(fs, FS_UMOUNT_CMD));

				if (checkCommand(fs, mountCmd, "mount command")
						&& checkCommand(fs, umountCmd, "umount command")) {
                	
	                String schemeList = p.getProperty(fsProperty(fs, FS_SCHEMES));
	                String[] schemes = splitList(schemeList);
	                if (logger.isDebugEnabled()) {
	                    logger.debug("- " + fs + " schemes: "
	                            + Arrays.toString(schemes));
	                }
	                
	                String contextList = p.getProperty(fsProperty(fs, FS_CONTEXTS));
	                String[] contexts = splitList(contextList);
	                if (logger.isDebugEnabled()) {
	                    logger.debug("- " + fs + " contexts: "
	                            + Arrays.toString(contexts));
	                }
	                for (String type: contexts) {
	                	acceptedContextTypes.add(type);
	                }
	                
	                String mountPoint = p.getProperty(fsProperty(fs, FS_MOUNT_POINT));
	                String mountInput = p.getProperty(fsProperty(fs, FS_MOUNT_INPUT));

	                logger.debug("- {} mount point: {}", fs, mountPoint);
	                logger.debug("- {} mount command: {}", fs, mountCmd);
	                logger.debug("- {} mount input: {}", fs, mountInput);
	                logger.debug("- {} umount command: {}", fs, umountCmd);

					FsInfo info = new FsInfo(fs, contexts, mountDir, 
                			mountPoint, mountCmd, mountInput, umountCmd);
                
	                for (String scheme: schemes) {
	                    List<FsInfo> list = fsMap.get(scheme);
	                    
	                    if (list == null) {
	                        list = new LinkedList<FsInfo>();
	                        fsMap.put(scheme, list);
	                    }
	                    
	                    list.add(info);
	                }
                }
            }
        }
        
//        if (logger.isDebugEnabled()) {
//            logger.debug("FUSE adaptor accepts these schemes:");
//            for (String scheme: getAllAcceptedSchemes()) {
//                List<FsInfo> l = getFilesystems(scheme);
//                logger.debug("- '" + scheme + "' for " + l);
//            }
//        }
    }
    
    static String fsProperty(String fs, String prop) {
        return FILESYSTEMS + "." + fs + "." + prop;
    }
    
    private static String[] splitList(String list) {
        if (list == null) {
            return new String[0];
        } else {
            return list.split(LIST_SEPARATOR);
        }
    }
    
    private boolean checkCommand(String fs, String cmd, String description) {
    	if (cmd == null || cmd.trim().isEmpty()) {
    		logger.debug("- " + fs + ": IGNORED, missing " + description);
    		return false;
    	}
    	
    	int firstSpace = cmd.indexOf(' ');
    	String cleanCmd = firstSpace < 0 ? cmd : cmd.substring(0, firstSpace);

    	RunProcess proc = new RunProcess(cleanCmd);
    	proc.run();
    	int exitStatus = proc.getExitStatus(); 
    	
    	if (exitStatus >= 0) {
    		// the command can be executed
    		return true;
    	} else {
    		logger.debug("- " + fs + ": IGNORED, cannot execute " + description
    				+ ": " + cleanCmd);
    		return false;
    	}
    }
    
    public String getDelegateScheme() {
        return delegateScheme;
    }

    public boolean isAcceptedScheme(String scheme) {
        return fsMap.containsKey(scheme);
    }
    
    public boolean isAcceptedContextType(String type) {
    	return acceptedContextTypes.contains(type);
    }

    public List<String> getAllAcceptedSchemes() {
        ArrayList<String> l = new ArrayList<String>(fsMap.keySet());
        Collections.sort(l);
        return l;
    }

    public List<String> getAllAcceptedContextTypes() {
    	ArrayList<String> result = new ArrayList<String>(acceptedContextTypes);
    	Collections.sort(result);
    	return result;
    }
    
    public List<FsInfo> getFilesystems(String scheme) {
        return fsMap.get(scheme);
    }
    
}
