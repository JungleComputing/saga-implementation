package benchmarks.file;

import java.io.IOException;
import java.net.URI;
import java.util.Vector;

import org.globus.ftp.FileInfo;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.HostPort;
import org.globus.ftp.exception.FTPException;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.io.streams.FTPInputStream;
import org.globus.io.streams.FTPOutputStream;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class GlobusFileBenchmark implements Benchmark {

    private static Logger logger = LoggerFactory.getLogger(GlobusFileBenchmark.class);
    
    static final int DEFAULT_GRIDFTP_PORT = 2811;
    
    private GridFTPClient client;

    private String baseDir;
    
    private final URI uri;

    private final String host;

    private int port;

    private final GSSCredential credential;
    
    class GridFTPOutputStream extends FTPOutputStream {
        public GridFTPOutputStream(String file, boolean passive, int type,
                GridFTPClient gridFtp, boolean append) throws IOException,
                FTPException {
            ftp = gridFtp;

            put(passive, type, file, append);
        }
    }

    class GridFTPInputStream extends FTPInputStream {
        public GridFTPInputStream(String file, boolean passive, int type,
                GridFTPClient gridFtp) throws IOException, FTPException {
            ftp = gridFtp;

            get(passive, type, file);
        }
    }

    private GridFTPClient createClient() {
        GridFTPClient c = null;
        try {
            c = new GridFTPClient(host, port);
            c.authenticate(credential);
            c.setType(GridFTPSession.TYPE_IMAGE);
        } catch(Throwable e) {
            if (c != null) {
                try {
                    c.close();
                } catch(Throwable x) {
                    // ignored
                }
            }
            throw new Error("Could not create gridftp client", e);
        }
        return c;
    }
    
    public GlobusFileBenchmark(String uriString, GSSCredential credential) throws Exception {
        uri = new URI(uriString);
        host = uri.getHost();
        port = uri.getPort();
        this.credential = credential;
        if (port == -1) {
            port = DEFAULT_GRIDFTP_PORT;
        }
        
        baseDir = uri.getPath();
        
        if (baseDir.equals("")) {
            baseDir = "/";
        }
    }

    @SuppressWarnings("unchecked")
    public void run() {
        client = createClient();
        // client.setPassive();
        // client.setLocalActive();
        try {
            // sanity check: is the mounted directory empty? If not, bail out
             
            client.changeDir(baseDir);

            Vector<FileInfo> list = client.list();

            for (FileInfo f : list) {
                String name = f.getName();
                if (name.equals(".") || name.equals("..")) {
                    continue;
                }
                throw new Error("baseDir " + uri + " is not empty");
            }

            // create a big file 'foo'
            if (logger.isInfoEnabled()) {
                logger.info("Creating file foo of {} bytes",
                        FileBenchmark.BIG_FILE_SIZE);
            }

            java.io.OutputStream fos = new GridFTPOutputStream("foo",
                        true /* passive */, GridFTPSession.TYPE_IMAGE, client, false);

            byte[] buf = new byte[FileBenchmark.WRITE_BUF_SIZE];
            
            long written = 0;
            while (written < FileBenchmark.BIG_FILE_SIZE) {
                int max = (int)Math.min(buf.length, 
                        FileBenchmark.BIG_FILE_SIZE - written);
                fos.write(buf, 0, max);
                written += max;
                if (logger.isDebugEnabled()) {
                    logger.debug("Wrote {} bytes", written);
                }
            }
            fos.close();

            // copy 'foo' to 'bar'

            if (logger.isInfoEnabled()) {
                logger.info("Copying foo to bar");
            }
            GridFTPClient c1 = createClient();
            GridFTPClient c2 = createClient();
            HostPort hp = c2.setPassive();
            c1.setActive(hp);
            c1.changeDir(baseDir);
            c2.changeDir(baseDir);
            c1.transfer("foo", c2, "bar", false, null);
            c2.close();
            c1.close();
 
            // read 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Reading bar");
            }
            java.io.InputStream in = null;
            try {
                client = createClient();
                client.changeDir(baseDir);
                in = new GridFTPInputStream("bar",
                    true /* passive */, GridFTPSession.TYPE_IMAGE, client);
                
                byte[] readBuf = new byte[FileBenchmark.READ_BUF_SIZE];
                long totalRead = 0;
                int readBytes = 0;
                while (readBytes != -1) {
                    readBytes = in.read(readBuf);
                    if (readBytes > 0) totalRead += readBytes;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Read {} bytes", totalRead);
                    }
                }
            } finally {
                if (in != null) in.close();
            }
            
            // delete all files and directories
            if (logger.isInfoEnabled()) {
                logger.info("Deleting all files and directories");
            }
            
            client = createClient();
            client.changeDir(baseDir);
            removeRecursively();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }

    @SuppressWarnings("unchecked")
    private void removeRecursively() throws Exception {
        Vector<FileInfo> list = client.list();
        client.setPassive();
        client.setLocalActive();
        for (FileInfo entry: list) {
            String name = entry.getName();
            if (name.equals(".") || name.equals("..")) {
                continue;
            }
            if (entry.isDirectory()) {
                client.changeDir(name);
                removeRecursively();
                client.changeDir("..");
                client.deleteDir(name);
            } else {
                client.deleteFile(name);
            }
        }
    }

    public void close() {
        try {
            client.close();
        } catch (Throwable e) {
            // ignored
        }
    }
    
    /**
     * This method returns the default globus credential. The strategy used is
     * as follows.
     * 
     * <P>
     * First, it tries to use the CredentialSecurityContext to retrieve the
     * credential.
     * 
     * <P>
     * Next, it tries to read the proxy from the location specified in the
     * "X509_USER_PROXY" environment variable. This variable is used by the
     * globus commandline tools (e.g., grid-proxy-init, globus-url-copy) as
     * well.
     * 
     * <P>
     * Finally, it tries to get the default proxy from the default location.
     */
    private static GSSCredential getDefaultCredential()
            throws Exception {

        GSSCredential credential = null;

        if (logger.isDebugEnabled()) {
            logger
            .debug("trying to get credential from location specified in environment");
        }

        String proxyLocation = System.getenv("X509_USER_PROXY");

        if (proxyLocation == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("no credential location found in environment");
            }
        } else {

            try {
                GlobusCredential globusCred = new GlobusCredential(proxyLocation);
                credential = new GlobusGSSCredentialImpl(globusCred,
                        GSSCredential.INITIATE_AND_ACCEPT);

                if (logger.isDebugEnabled()) {
                    logger.debug("loaded credential from file " + proxyLocation);
                }
                return credential;
            } catch (Throwable t) {
                if (logger.isDebugEnabled()) {
                    logger.debug("loading credential from file " + proxyLocation
                            + " failed: " + t);
                }
            }
        }

        // next try to get default credential
        if (logger.isDebugEnabled()) {
            logger.debug("trying to get default credential");
        }

        // Get the user credential
        ExtendedGSSManager manager 
            = (ExtendedGSSManager) ExtendedGSSManager.getInstance();

        // try to get default user proxy certificate from file in /tmp
        return manager.createCredential(GSSCredential.INITIATE_AND_ACCEPT);
    }


    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("usage: java " + GlobusFileBenchmark.class.getName()
                    + " <basedir> <#runs>");
            return;
        }

        String baseDir = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            GSSCredential credential = getDefaultCredential();
            test = new GlobusFileBenchmark(baseDir, credential);
        } catch (Exception e) {
            System.err.println("Got exception " + e);
            e.printStackTrace(System.err);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
}
