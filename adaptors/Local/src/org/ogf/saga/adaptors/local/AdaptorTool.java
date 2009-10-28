package org.ogf.saga.adaptors.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.url.URL;

public interface AdaptorTool {

    public void checkURL(URL u) throws IncorrectURLException;
    
    public File createFile(String pathname);
    
    public File createFile(File parent, String child);
    
    public File createFile(String parent, String child);
    
    public InputStream createFileInputStream(String pathname)
            throws FileNotFoundException;

    public OutputStream createFileOutputStream(String pathname)
            throws FileNotFoundException;

    /**
     * Copies all entries in sourceDir to targetDir. Directories are copied
     * recursively. Precondition: the targetDir exists.
     * 
     * @param sourceDir
     *            the source directory to copy recursively
     * @param targetDir
     *            the target directory, which must already exist
     * @throws NoSuccessException
     */
    public void copyRecursively(File sourceDir, File targetDir)
            throws NoSuccessException;
    
    public void copyBytes(File from, File to) throws NoSuccessException;
    
    public void close(File file) throws NoSuccessException;
    
}
