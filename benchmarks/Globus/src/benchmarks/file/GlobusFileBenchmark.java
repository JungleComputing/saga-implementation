package benchmarks.file;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Vector;

import org.globus.ftp.Buffer;
import org.globus.ftp.DataSink;
import org.globus.ftp.DataSource;
import org.globus.ftp.FileInfo;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.HostPort;
import org.globus.ftp.RetrieveOptions;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
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
       
    private GridFTPClient createClient() {
        GridFTPClient c = null;
        try {
            c = new GridFTPClient(host, port);
            c.authenticate(credential);
            c.setType(GridFTPSession.TYPE_IMAGE);
            c.setClientWaitParams(30000, 10);
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
    private void removeRecursively(GridFTPClient client) throws Exception {
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
                removeRecursively(client);
                client.changeDir("..");
                client.deleteDir(name);
            } else {
                client.deleteFile(name);
            }
        }
    }

    
    public void close() {
        // nothing
    }

    @SuppressWarnings("unchecked")
    public void run() {
        try {
            client = createClient();
            client.changeDir(baseDir);

            Vector<FileInfo> list = client.list();

            for (FileInfo f : list) {
                String name = f.getName();
                if (name.equals(".") || name.equals("..")) {
                    continue;
                }
                throw new Error("baseDir " + uri + " is not empty");
            }
            client.close();

            // create a big file 'foo'
            if (logger.isInfoEnabled()) {
                logger.info("Creating file foo of {} bytes",
                        FileBenchmark.BIG_FILE_SIZE);
            }
            client = createClient();
            client.changeDir(baseDir);
            client.setMode(GridFTPSession.MODE_EBLOCK);
            client.setOptions(new RetrieveOptions(1));
            client.setPassive();
            client.setLocalActive();
            byte[] buf = new byte[FileBenchmark.WRITE_BUF_SIZE];
            DataWriter dataWriter = new DataWriter(client, 0, "foo");
            long written = 0;
            while (written < FileBenchmark.BIG_FILE_SIZE) {
                int sz = (int)Math.min(buf.length, 
                        FileBenchmark.BIG_FILE_SIZE - written);

                dataWriter.add(buf, 0, sz);
                Throwable e = dataWriter.waitForEmptyList();
                if (e != null) {
                    dataWriter.finish();
                    throw new Error("write failed", e);
                }
                written += sz;
                if (logger.isDebugEnabled()) {
                    logger.debug("Wrote {} bytes", written);
                }
            }
            dataWriter.finish();
            client.close();
            
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
            client = createClient();
            client.changeDir(baseDir);
            client.setMode(GridFTPSession.MODE_EBLOCK);
            client.setOptions(new RetrieveOptions(1));
            client.setLocalPassive();
            client.setActive();
            byte[] readBuf = new byte[FileBenchmark.READ_BUF_SIZE];
            
            int readBytes;
            int totalRead = 0;
            do {
                MyDataSink sink = new MyDataSink(buf, 0, buf.length);                
                client.extendedGet("bar", totalRead, buf.length, sink, null);
                readBytes = sink.getSize();
                totalRead += readBytes;
                if (logger.isDebugEnabled()) {
                    logger.debug("Read {} bytes", totalRead);
                }
            } while (readBytes > 0);
            client.close();
            
            // delete all files and directories
            client = createClient();
            client.changeDir(baseDir);
            removeRecursively(client);
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
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
   
    
    private static class MyDataSink implements DataSink {   
        byte[] buf;
        int off;
        int len;
        int writtenLen = 0;
        
        MyDataSink(byte[] buf, int off, int len) {
            this.buf = buf;
            this.off = off;
            this.len = len;
        }

        public void close() throws IOException {
            // No resources to release.
        }

        public void write(Buffer buffer) throws IOException {
            byte[] b = buffer.getBuffer();
            int l = buffer.getLength();
            long o = buffer.getOffset();
            if (logger.isDebugEnabled()) {
                logger.debug("Read buffer, b.length = " + b.length + ", l = " + l + ", o = " + o);
                logger.debug("buf.length = " + buf.length + ", off = " + off
                        + ", len = " + len);
            }
            System.arraycopy(b, 0, buf, off + (int) o, l);
            synchronized(this) {
                writtenLen += l;
            }
        }
        
        int getSize() {
            return writtenLen;
        }
    }
  
    private static class DataWriter implements DataSource, Runnable {

        private LinkedList<Buffer> list = new LinkedList<Buffer>();
        
        private final GridFTPClient client;
        
        private long offset = 0;
        
        private final long baseOffset;

        private boolean finished = false;
        
        private boolean done = false;
        
        private final String fileName;
        
        private Throwable exception = null;
        
        private boolean readerWaiting = false;
        
        public DataWriter(GridFTPClient client, long baseOffset, String fileName) {
            this.client = client;
            this.baseOffset = baseOffset;
            this.fileName = fileName;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        public void add(byte[] buf, int off, int len) {
            // Copy, because we don't know when we can touch the buffer again.
            byte[] b = new byte[len];
            System.arraycopy(buf, off, b, 0, len);

            synchronized(this) {
                Buffer buffer = new Buffer(b, len, offset);
                offset += len;
                list.add(buffer);
                notifyAll();
            }
        }
        
        public synchronized Throwable waitForEmptyList() {
            // Wait until client is ready to read the next buffer. Can we get
            // closer to actually knowing that the buffer is written?
            while (! finished && ! readerWaiting && getException() == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored
                }
            }
            return getException();
        }
        
        public synchronized void finish() {
            finished = true;
            notifyAll();
            while (! done) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored
                }
            }
        }

        public void close() {
        }

        public synchronized Buffer read() throws IOException {
            while (! finished && list.isEmpty()) {
                readerWaiting = true;
                notifyAll();
                try {
                    wait();
                } catch(InterruptedException e) {
                    // ignored
                }
                readerWaiting = false;
            }
            if (! list.isEmpty()) {
                return list.remove();
            }
            return null;
        }
        
        public synchronized Throwable getException() {
            return exception;
        }

        public void run() {
            try {
                client.extendedPut(fileName, baseOffset, this, null);
            } catch(Throwable e) {
                synchronized(this) {
                    exception = e;
                }
            }
            synchronized(this) {
                done = true;
                notifyAll();
            }
        }
    }

}
