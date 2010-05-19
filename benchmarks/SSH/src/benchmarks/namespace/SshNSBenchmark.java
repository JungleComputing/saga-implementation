package benchmarks.namespace;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;
import benchmarks.Connecter;
import benchmarks.HostKeyVerifier;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SFTPException;
import com.trilead.ssh2.SFTPv3Client;
import com.trilead.ssh2.SFTPv3DirectoryEntry;
import com.trilead.ssh2.SFTPv3FileAttributes;
import com.trilead.ssh2.SFTPv3FileHandle;
import com.trilead.ssh2.sftp.ErrorCodes;

public class SshNSBenchmark implements Benchmark {
    
    private static Logger logger = LoggerFactory.getLogger(SshNSBenchmark.class);

    static final int STDOUT = 0, STDERR = 1, EXIT_VALUE = 2;
    
    private static byte[] copyBuf = new byte[32768];
    
    private final URI uri;
    private final Connection connection;
    private final String path;
    
    
    public SshNSBenchmark(String baseDirUrl) throws URISyntaxException {
        uri = new URI(baseDirUrl);
        path = uri.getPath();

        HostKeyVerifier verifier = new HostKeyVerifier(false, true, true);
        
        String host = uri.getHost();
        
        if (host == null) {
            throw new Error("Host not specified");
        }
        
        int port = uri.getPort();
        if (port == -1) {
            port = 22;      // default ssh port.
        }
        
        try {
            connection = Connecter.getConnection(host, port, verifier, true);
        } catch (Exception e) {
            throw new Error("Unable to connect!", e);
        }
    }

    public void close() {
        if (connection != null) {
            connection.close();
        }
    }

    public void run() {
        
        SFTPv3Client sftp = null;
        
        try {
            sftp = new SFTPv3Client(connection);
            
            // sanity check: is the base directory a directory and empty? If not, bail out
            SFTPv3FileAttributes attrs;
            try {
                attrs = sftp.stat(path);
            } catch (SFTPException x) {
                if (x.getServerErrorCode() == ErrorCodes.SSH_FX_NO_SUCH_FILE) {
                    throw new Error(path + " does not exist");
                }
                throw new Error(x);
            }
            if (! attrs.isDirectory()) {
                throw new Error(path + " is not a directory");
            }
         
            Vector<SFTPv3DirectoryEntry> result = listDir(sftp, path);
 
            if (! result.isEmpty()) {
                throw new Error("Directory " + path + " is not empty");
            }
            
            // create DIR_COUNT directories ('/dir000' to '/dirXXX') 
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.DIR_COUNT 
                        + " directories in " + path);
            }
            for (int i = 0; i < NSBenchmark.DIR_COUNT; i++) {
                String dir = path + "/" + String.format("dir%03d", i);
                logger.debug("  mkdir {}", dir);
                sftp.mkdir(dir, 0755);
            }
            

            // Create SUBDIR_COUNT sub-directories in each directory 
            // ('/dir000/subdir000' to '/dirXXX/subdirYYY')
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.SUBDIR_COUNT 
                        + " subdirectories in each directory");
            }
            
            for (SFTPv3DirectoryEntry entry : listDir(sftp, path)) {
                String name = path + "/" + entry.filename;
                attrs = sftp.stat(name);
                if (attrs.isDirectory()) {
                    for (int j = 0; j < NSBenchmark.SUBDIR_COUNT; j++) {
                        String subdir = name + "/" + String.format("subdir%03d", j);
                        logger.debug("  mkdir {}", subdir);
                        sftp.mkdir(subdir, 0755);
                     }
                }
            }
            
            // in each sub-directory, create FILE_COUNT empty text files ('file000' to 
            // 'fileZZZ').
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.FILE_COUNT 
                        + " files in each subdirectory");
            }
            
            for (SFTPv3DirectoryEntry entry : listDir(sftp, path)) {
                String name = path + "/" + entry.filename;
                attrs = sftp.stat(name);
                if (attrs.isDirectory()) {
                    for (SFTPv3DirectoryEntry subentry : listDir(sftp, name)) {
                        String subname = name + "/" + subentry.filename;
                        for (int i = 0; i < NSBenchmark.FILE_COUNT; i++) {
                            String file = subname + "/" + String.format("file%03d", i);
                            
                            if (logger.isDebugEnabled()) {
                                logger.debug("  touch " + file);
                            }
                            
                            sftp.closeFile(sftp.createFile(file));
                        }
                    }
                }
            }
            
            // print the type (file or directory) and size (in bytes) of all 
            // entries in the volume
            if (logger.isInfoEnabled()) {
                logger.info("Logging type and size of all entries (at DEBUG level)");
            }
            listDirectory(sftp, path);
             
            
            // Move all subdirs
            if (logger.isInfoEnabled()) {
                logger.info("Moving all directories");
            }
            for (SFTPv3DirectoryEntry entry : listDir(sftp, path)) {
                String name = path + "/" + entry.filename;
                String newName = path + "/" + entry.filename.replace("dir", "d");
                logger.debug("  mv {} --> {}", name, newName);
                sftp.mv(name, newName);
            }

            // Copy all files
            for (SFTPv3DirectoryEntry entry : listDir(sftp, path)) {
                String name = path + "/" + entry.filename;
                attrs = sftp.stat(name);
                if (attrs.isDirectory()) {
                    for (SFTPv3DirectoryEntry subentry : listDir(sftp, name)) {
                        String subname = name + "/" + subentry.filename;
                        for (SFTPv3DirectoryEntry e : listDir(sftp, subname)) {
                            copy(sftp, subname + "/" + e.filename,
                                    subname + "/" + e.filename.replace("file", "f"));
                        }
                    }
                }
            }
            
            // delete all files and directories
            if (logger.isInfoEnabled()) {
                logger.info("Deleting all files and directories");
            }
            
            for (SFTPv3DirectoryEntry entry : listDir(sftp, path)) {
                String name = path + "/" + entry.filename; 
                logger.debug("rm -r {}", name);
                remove(sftp, name);
            }
            
        } catch (Exception e) {
            throw new Error(e);
        } finally {
            if (sftp != null) {
                sftp.close();
            }
        }
    }
    
    private void remove(SFTPv3Client sftp, String dir) throws IOException {
        for (SFTPv3DirectoryEntry entry : listDir(sftp, dir)) {
            String name = dir + "/" + entry.filename;
            SFTPv3FileAttributes attrs = sftp.stat(name);
            if (attrs.isDirectory()) {
                remove(sftp, name);
            } else {
                sftp.rm(name);
            }
        }
        sftp.rmdir(dir);
    }

    private void listDirectory(SFTPv3Client sftp, String dir) throws IOException {
        for (SFTPv3DirectoryEntry entry : listDir(sftp, dir)) {
            String name = dir + "/" + entry.filename;
            SFTPv3FileAttributes attrs = sftp.stat(name);
            if (attrs.isDirectory()) {
                String s = String.format("d          %s", name);
                logger.debug(s);
                listDirectory(sftp, name);
            } else {
                String s = String.format("- %8d %s", attrs.size.intValue(), name);
                logger.debug(s);
            }
        }
    }

    private Vector<SFTPv3DirectoryEntry> listDir(SFTPv3Client sftp, String dir) throws IOException {
        Vector<?> result = sftp.ls(dir);
        Vector<SFTPv3DirectoryEntry> newRes = new Vector<SFTPv3DirectoryEntry>();
        for (int i = 0; i < result.size(); i++) {
            SFTPv3DirectoryEntry entry = (SFTPv3DirectoryEntry) result.get(i);
            if (!entry.filename.equals(".") && !entry.filename.equals("..")) {
                newRes.add(entry);
            }
        }
        return newRes;
    }
    
    private void copy(SFTPv3Client sftp, String source, String dest) throws IOException {
        // copy 'foo' to 'bar'
        if (logger.isInfoEnabled()) {
            logger.info("Copying " + source + " to " + dest);
        }
        SFTPv3FileHandle handleW = sftp.createFileTruncate(dest);
        SFTPv3FileHandle handleR = sftp.openFileRO(source);
        
        long done = 0;
        for (;;) {
            int len = sftp.read(handleR, done, copyBuf, 0, copyBuf.length);
            if (len <= 0) {
                break;
            }
            sftp.write(handleW, done, copyBuf, 0, len);
            done += len;
        }
        sftp.closeFile(handleR);
        sftp.closeFile(handleW);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("usage: java " + SshNSBenchmark.class.getName() 
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        test = new SshNSBenchmark(baseDirUrl);
   
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
}

