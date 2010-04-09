package org.ogf.saga.adaptors.local;

import java.io.Closeable;
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
        logger.debug("Creating local file: {}", pathname);
        return new File(pathname);
    }

    public File createFile(File parent, String child) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating local file: {}/{}", 
                    parent.getAbsolutePath(), child);
        }
        return new File(parent, child);
    }

    public File createFile(String parent, String child) {
        logger.debug("Creating local file: {}/{}", parent, child); 
        return new File(parent, child);
    }

    public FileInputStream createFileInputStream(String pathname)
            throws FileNotFoundException {
        return new FileInputStream(pathname);
    }

    public FileOutputStream createFileOutputStream(String pathname)
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
        
        FileInputStream fin = null;
        FileOutputStream fout = null;
        FileChannel fromChannel = null;
        FileChannel toChannel = null;

        try {
            fin = new FileInputStream(from);
            fout = new FileOutputStream(to);
            
            fromChannel = fin.getChannel();
            toChannel = fout.getChannel();
            boolean useNioCopy = true;
            
            // NIO copy does not always work, so first try to transfer 1 byte
            try {
                fromChannel.transferTo(0, 1, toChannel);
            } catch (IOException e) {
                useNioCopy = false;
            }
            
            if (useNioCopy) {
                if (from.length() > 1) {
                    // transfer the remaining bytes
                    logger.debug("Using NIO file copy");
                    fromChannel.transferTo(1, fromChannel.size(), toChannel);
                }
            } else {
                logger.debug("Using normal file copy");
                byte[] buf = new byte[1024 * 32];
                int readBytes = 0;
                while ((readBytes = fin.read(buf)) != -1) {
                    fout.write(buf, 0, readBytes);
                }
            }
        } catch (IOException e) {
            throw new NoSuccessException("Error during local copy from '" + from
                    + "' to '" + to + "'", e);
        } finally {
            forceClose(fromChannel);
            forceClose(toChannel);
            forceClose(fin);
            forceClose(fout);
        }
    }
    
    private void forceClose(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            logger.debug("Error during close", e); 
        }
    }
    
    public void close(File file) {
        // do nothing
    }
    
    public void close(InputStream in) throws IOException {
        in.close();
    }

    public void close(OutputStream out) throws IOException {
        out.close();
    }

    public AdaptorTool clone() {
        return this;
    }

}
