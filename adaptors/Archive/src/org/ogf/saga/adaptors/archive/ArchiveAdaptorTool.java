package org.ogf.saga.adaptors.archive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.URLChecker;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schlichtherle.io.ArchiveBusyWarningException;
import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.DefaultArchiveDetector;

public class ArchiveAdaptorTool implements AdaptorTool {

    private static ArchiveAdaptorTool uniqueInstance = null;
    private static Logger logger = LoggerFactory.getLogger(ArchiveAdaptorTool.class); 
    private static final DefaultArchiveDetector ARCHIVE_DETECTOR = ArchiveDetector.ALL;
        
    public static String[] getSupportedSchemes() {
        return new String[] { "archive", "file", ""};
    }
    
    protected ArchiveAdaptorTool() {
    }
    
    public static synchronized ArchiveAdaptorTool getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new ArchiveAdaptorTool();
            logger.info("Recognized suffixes: "
                    + ARCHIVE_DETECTOR.getSuffixes());
        }
        return uniqueInstance;
    }
        
    public void checkURL(URL u) throws IncorrectURLException {
        URLChecker.check(u);
    }

    public File createFile(String pathname) {
        logger.debug("Creating archive file: {}", pathname);
        return new de.schlichtherle.io.File(pathname, ARCHIVE_DETECTOR);
    }

    public File createFile(File parent, String child) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating archive file: {}/{}", 
                    parent.getAbsolutePath(), child);
        }
        return new de.schlichtherle.io.File(parent, child, ARCHIVE_DETECTOR);
    }

    public File createFile(String parent, String child) {
        logger.debug("Creating archive file: {}/{}", parent, child); 
        return new de.schlichtherle.io.File(parent, child, ARCHIVE_DETECTOR);
    }
    
    public InputStream createFileInputStream(String pathname)
            throws FileNotFoundException {
        File f = createFile(pathname);
        return new de.schlichtherle.io.FileInputStream(f);
    }

    public OutputStream createFileOutputStream(String pathname)
            throws FileNotFoundException {
        File f = createFile(pathname);
        return new de.schlichtherle.io.FileOutputStream(f);
    }
    
    public void copyRecursively(File sourceDir, File targetDir)
            throws NoSuccessException {

        logger.debug("Recursive archive copy from '{}' to '{}'",
                sourceDir, targetDir);

        de.schlichtherle.io.File archive = (de.schlichtherle.io.File) sourceDir;

        if (!archive.copyAllTo(targetDir)) {
            throw new NoSuccessException(
                    "Error during recursive archive copy from '" + sourceDir
                            + "' to '" + targetDir + "'");
        }
    }

    public void copyBytes(java.io.File from, java.io.File to)
            throws NoSuccessException {
        logger.debug("Archive copy from '{}' to '{}'", from, to);

        de.schlichtherle.io.File archive = (de.schlichtherle.io.File)from;
        
        if (!archive.copyTo(to)) {
            throw new NoSuccessException("Error during archive copy from '"
                    + from + "' to '" + to + "'");
        }
    }
    
    public void close(File file) throws NoSuccessException {
        if (file == null) {
            return;
        }
        
        if (file instanceof de.schlichtherle.io.File) {
            de.schlichtherle.io.File archive = (de.schlichtherle.io.File)file;
                
            if (!archive.isArchive()) {
                // file may be an entry within an enclosing archive
                archive = archive.getEnclArchive();
                if (archive == null) {
                    // file was not located in any archive -> do nothing
                    return;
                }
            }
    
            // update the archive (i.e. sync changes to disk)
            try {
                logger.debug("Unmounting archive '{}'", archive);
                de.schlichtherle.io.File.umount(archive, true);
            } catch (ArchiveBusyWarningException ignored) {
                // Truezip forcibly closed some streams, which seems normal 
                logger.debug("Warning while closing " + file.getAbsolutePath(), 
                        ignored);
            } catch (ArchiveException e) {
                throw new NoSuccessException("Error while updating archive '"
                        + file + "'", e);
            }
        }
    }
    
    public void close(InputStream in) throws IOException {
        if (in != null) {
            try {
                in.close();
            } catch (ArchiveBusyWarningException ignored) {
                // Truezip forcibly closed some streams, which seems normal 
                logger.debug("Warning while closing streams", ignored);
            }
        }
    }
    
    public void close(OutputStream out) throws IOException {
        if (out != null) {
            try {
                out.close();
            } catch (ArchiveBusyWarningException ignored) {
                // Truezip forcibly closed some streams, which seems normal 
                logger.debug("Warning while closing streams", ignored);
            }
        }
    }

    public AdaptorTool clone() {
        return this;
    }

}
