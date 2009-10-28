package org.ogf.saga.adaptors.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;

import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalAdaptorTool implements AdaptorTool {

    private static LocalAdaptorTool uniqueInstance = null;
    private static Logger logger = LoggerFactory.getLogger(LocalAdaptorTool.class); 
    
    private URLChecker urlChecker;    
    
    protected LocalAdaptorTool() {
        Collection<String> localSchemes = new ArrayList<String>(3);
        localSchemes.add("file");
        localSchemes.add("local");
        localSchemes.add("any");
        
        urlChecker = new URLChecker(localSchemes, "local");
    }
    
    public static synchronized LocalAdaptorTool getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new LocalAdaptorTool();
        }
        return uniqueInstance;
    }
     
    public void checkURL(URL u) throws IncorrectURLException {
        urlChecker.check(u);
    }
    
    public File createFile(String pathname) {
        return new File(pathname);
    }

    public File createFile(File parent, String child) {
        return new File(parent, child);
    }

    public File createFile(String parent, String child) {
        return new File(parent, child);
    }

    public InputStream createFileInputStream(String pathname)
            throws FileNotFoundException {
        return new FileInputStream(pathname);
    }

    public OutputStream createFileOutputStream(String pathname)
            throws FileNotFoundException {
        return new FileOutputStream(pathname);
    }
    
    public void copyRecursively(File sourceDir, File targetDir)
            throws NoSuccessException {

        logger.debug("Recursive local copy from '{}' to '{}'",
                sourceDir, targetDir);
        
        String[] sources = sourceDir.list();

        for (int i = 0; i < sources.length; i++) {
            File source = createFile(sourceDir, sources[i]);
            File target = createFile(targetDir, sources[i]);

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
            
    public void copyBytes(File from, File to) throws NoSuccessException {
        logger.debug("Local copy from '{}' to '{}'", from, to);
        
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
            throw new NoSuccessException("Error during local copy from '" + from
                    + "' to '" + to + "'");
        }
    }
    
    public void close(File file) {
        // do nothing
    }

    public AdaptorTool clone() {
        return this;
    }
    
}
