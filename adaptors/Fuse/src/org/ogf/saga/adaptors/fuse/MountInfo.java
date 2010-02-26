package org.ogf.saga.adaptors.fuse;

import java.io.File;

public class MountInfo {

	final String mountCommand;
	final String mountInput;
    final String umountCommand;
    final File mountPoint;
    
	MountInfo(String mountCommand, String mountInput, String umountCommand,
			File mountPoint) {
        this.mountCommand = mountCommand;
        this.mountInput = mountInput;
        this.umountCommand = umountCommand;
        this.mountPoint = mountPoint;
    }
    
}
