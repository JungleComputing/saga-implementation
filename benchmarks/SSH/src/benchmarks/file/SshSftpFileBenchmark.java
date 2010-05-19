package benchmarks.file;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SFTPException;
import com.trilead.ssh2.SFTPv3Client;
import com.trilead.ssh2.SFTPv3DirectoryEntry;
import com.trilead.ssh2.SFTPv3FileAttributes;
import com.trilead.ssh2.SFTPv3FileHandle;
import com.trilead.ssh2.sftp.ErrorCodes;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;
import benchmarks.Connecter;
import benchmarks.HostKeyVerifier;

public class SshSftpFileBenchmark implements Benchmark {
    
    private static Logger logger = LoggerFactory.getLogger(SshSftpFileBenchmark.class);
    
    private static byte[] writebuf = new byte[FileBenchmark.WRITE_BUF_SIZE];
    
    private static byte[] readbuf = new byte[FileBenchmark.READ_BUF_SIZE];
    
    private static byte[] copybuf = new byte[FileBenchmark.COPY_BUF_SIZE];
    
    private final URI uri;
    private final Connection connection;
    private final String path;
    
    public SshSftpFileBenchmark(String baseDirUrl) throws URISyntaxException {
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
            
            // sanity check: is the base directory empty? If not, bail out
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
            Vector<?> result = sftp.ls(path);
            Vector<String> newRes = new Vector<String>();
            for (int i = 0; i < result.size(); i++) {
                SFTPv3DirectoryEntry entry = (SFTPv3DirectoryEntry) result.get(i);
                if (!entry.filename.equals(".") && !entry.filename.equals("..")) {
                    newRes.add(entry.filename);
                }
            }
            if (! newRes.isEmpty()) {
                throw new Error("Directory " + path + " is not empty");
            }
            
            // create a big file 'foo'
            if (logger.isInfoEnabled()) {
                logger.info("Creating file " + uri + "/foo of " 
                        + FileBenchmark.BIG_FILE_SIZE + " bytes");
            }
            
            SFTPv3FileHandle handle = sftp.createFileTruncate(path + "/foo");
            long written = 0;
            while (written < FileBenchmark.BIG_FILE_SIZE) {
                int max = (int)Math.min(writebuf.length, FileBenchmark.BIG_FILE_SIZE - written);
                sftp.write(handle, written, writebuf, 0, max);
                written += max;
                if (logger.isDebugEnabled()) {
                    logger.debug("Wrote {} bytes", written);
                }
            }
            sftp.closeFile(handle);

            // copy 'foo' to 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Copying " + uri + "/foo to " + uri + "/bar");
            }
            SFTPv3FileHandle handleW = sftp.createFileTruncate(path + "/bar");
            SFTPv3FileHandle handleR = sftp.openFileRO(path + "/foo");
            
            long done = 0;
            for (;;) {
                int len = sftp.read(handleR, done, copybuf, 0, copybuf.length);
                if (len <= 0) {
                    break;
                }
                sftp.write(handleW, done, copybuf, 0, len);
                done += len;
            }
            sftp.closeFile(handleR);
            sftp.closeFile(handleW);
            
            // read 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Reading " + uri + "/bar");
            }
            handle = sftp.openFileRO(path + "/bar");
            done = 0;
            for (;;) {
                int len = sftp.read(handle, done, readbuf, 0, readbuf.length);
                if (len <= 0) {
                    break;
                }
                done += len;
                if (logger.isDebugEnabled()) {
                    logger.debug("Read {} bytes", done);
                }
            }
            sftp.closeFile(handleR);
            
            // delete all files
            if (logger.isInfoEnabled()) {
                logger.info("Deleting 'foo' and 'bar'");
            }
            sftp.rm(path + "/foo");
            sftp.rm(path + "/bar");
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            if (sftp != null) {
                sftp.close();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("usage: java " + SshSftpFileBenchmark.class.getName() 
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        test = new SshSftpFileBenchmark(baseDirUrl);
   
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
}
