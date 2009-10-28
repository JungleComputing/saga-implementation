package org.ogf.saga.adaptors.archive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.ogf.saga.adaptors.local.AdaptorTool;
import org.ogf.saga.adaptors.local.URLChecker;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.DefaultArchiveDetector;

public class ArchiveAdaptorTool implements AdaptorTool {

    private static ArchiveAdaptorTool uniqueInstance = null;
    private static Logger logger = LoggerFactory.getLogger(ArchiveAdaptorTool.class); 
    private static final DefaultArchiveDetector ARCHIVE_DETECTOR = ArchiveDetector.ALL;
    
    private URLChecker urlChecker;
    
    protected ArchiveAdaptorTool() {
        Collection<String> archiveSchemes = new ArrayList<String>(3);
        archiveSchemes.add("file");
        archiveSchemes.add("archive");
        archiveSchemes.add("any");
        
        urlChecker = new URLChecker(archiveSchemes, "archive");
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
        urlChecker.check(u);
    }

    public File createFile(String pathname) {
        return new de.schlichtherle.io.File(pathname, ARCHIVE_DETECTOR);
    }

    public File createFile(File parent, String child) {
        return new de.schlichtherle.io.File(parent, child, ARCHIVE_DETECTOR);
    }

    public File createFile(String parent, String child) {
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
        
        close(targetDir);
    }

    public void copyBytes(java.io.File from, java.io.File to)
            throws NoSuccessException {
        logger.debug("Archive copy from '{}' to '{}'", from, to);

        de.schlichtherle.io.File archive = (de.schlichtherle.io.File)from;
        
        if (!archive.copyTo(to)) {
            throw new NoSuccessException("Error during archive copy from '"
                    + from + "' to '" + to + "'");
        }
        
        close(to);
    }
    
    public void close(File file) throws NoSuccessException {
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
            logger.debug("Updating archive '{}'", archive);
            de.schlichtherle.io.File.update(archive, true);
        } catch (ArchiveException e) {
            throw new NoSuccessException("Error while updating archive '"
                    + file + "'", e);
        }
    }
    
    public AdaptorTool clone() {
        return this;
    }

}
