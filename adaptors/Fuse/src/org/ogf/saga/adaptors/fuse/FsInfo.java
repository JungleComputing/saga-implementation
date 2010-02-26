package org.ogf.saga.adaptors.fuse;

import org.ogf.saga.context.Context;
import org.ogf.saga.error.SagaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FsInfo {

    private Logger logger = LoggerFactory.getLogger(FsInfo.class);
    
    private final String name;
    private final String[] contexts;
    private final String mountDir;
    private final String mountPoint;
    private final String mountCommand;
    private final String mountInput;
    private final String umountCommand;
    
    FsInfo(String name, String[] contexts, String mountDir, 
            String mountPoint, String mountCommand, String mountInput,
            String umountCommand) {
        this.name = name;
        this.contexts = contexts;
        this.mountDir = mountDir;
        this.mountPoint = mountPoint;
        this.mountCommand = mountCommand;
        this.mountInput = mountInput;
        this.umountCommand = umountCommand;
    }
    
    String getName() {
        return name;
    }

    String getMountDir() {
        return mountDir;
    }

    String getMountPoint() {
        return mountPoint;
    }

    String getMountCommand() {
        return mountCommand;
    }

    String getMountInput() {
        return mountInput;
    }

    String getUmountCommand() {
        return umountCommand;
    }

    boolean acceptContext(Context c) {
        if (c == null) {
            return true;
        }
        
        String type = null;
        try {
            type = c.getAttribute(Context.TYPE);
        } catch (SagaException e) {
            logger.debug("Cannot retrieve context type");
            return false;
        }

        if (type == null) {
            return false;
        }
        
        for (int i = 0; i < contexts.length; i++) {
            if (type.equals(contexts[i])) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return name;
    }

}
