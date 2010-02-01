package benchmarks.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;
import benchmarks.Connecter;
import benchmarks.HostKeyVerifier;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SFTPv3Client;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;

public class SshFileBenchmark implements Benchmark {
    
    private static Logger logger = LoggerFactory.getLogger(SshFileBenchmark.class);
    
    private static byte[] writebuf = new byte[FileBenchmark.WRITE_BUF_SIZE];
    
    private static byte[] readbuf = new byte[FileBenchmark.READ_BUF_SIZE];
    
    private static byte[] copybuf = new byte[FileBenchmark.COPY_BUF_SIZE];
    
    static final int STDOUT = 0, STDERR = 1, EXIT_VALUE = 2;
    
    private final URI uri;
    private final Connection connection;
    private final String path;
    
    private String[] execCommand(Session session, String cmd) throws IOException, Exception {
        logger.info("command: " + cmd);
        String[] result = new String[3];
        if (session == null) {
            session = connection.openSession();
        }
        session.execCommand(cmd);
        // see http://www.trilead.com/Products/Trilead-SSH-2-Java/FAQ/#blocking
        InputStream stdout = new StreamGobbler(session.getStdout());
        InputStream stderr = new StreamGobbler(session.getStderr());
        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
        result[STDOUT] = "";
        result[STDERR] = "";
        StringBuffer out = new StringBuffer();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            out.append(line);
            out.append("\n");
        }
        result[STDOUT] = out.toString();
        br = new BufferedReader(new InputStreamReader(stderr));
        StringBuffer err = new StringBuffer();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            err.append(line);
            err.append("\n");
        }
        result[STDERR] = err.toString();
        while (session.getExitStatus() == null) {
            Thread.sleep(500);
        }
        result[EXIT_VALUE] = "" + session.getExitStatus();
        session.close();
        if (logger.isDebugEnabled()) {
            logger.debug("STDOUT: " + result[STDOUT]);
            logger.debug("STDERR: " + result[STDERR]);
            logger.debug("EXIT:   " + result[EXIT_VALUE]);
        }
        return result;
    }

    
    public SshFileBenchmark(String baseDirUrl) throws URISyntaxException {
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
            String[] resultTest;
            try {
                resultTest = execCommand(null, "test -d " + path);
            } catch (Exception e) {
                throw new Error(e);
            }
            // 0=dir 1=other
            boolean isDir = resultTest[EXIT_VALUE].equals("0");
            if (! isDir) {
                throw new Error(path + " is not a directory");
            }
 
            try {
                resultTest = execCommand(null, "ls -1 " + path);
            } catch (Exception e) {
                throw new Error(e);
            }
            if (! resultTest[STDOUT].equals("")) {
                throw new Error(path + " is not empty");
            }
            
            // create a big file 'foo'
            if (logger.isInfoEnabled()) {
                logger.info("Creating file " + uri + "/foo of " 
                        + FileBenchmark.BIG_FILE_SIZE + " bytes");
            }
            
            Session session;
            try {
                session = connection.openSession();
            } catch(Throwable e) {
                throw new Error(e);
            }
            OutputStream sessionInputStream = session.getStdin();
            String command = "cat > " + path + "/foo";
            OutputStreamRunner job = new OutputStreamRunner(session, command);
            job.setDaemon(true);
            job.start();

            long written = 0;
            while (written < FileBenchmark.BIG_FILE_SIZE) {
                int max = (int)Math.min(writebuf.length, FileBenchmark.BIG_FILE_SIZE - written);
                sessionInputStream.write(writebuf, 0, max);
                written += max;
                if (logger.isDebugEnabled()) {
                    logger.debug("Wrote {} bytes", written);
                }
            }
            sessionInputStream.close();

            // copy 'foo' to 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Copying " + uri + "/foo to " + uri + "/bar");
            }
            
            String[] copyResult;
            try {
                copyResult = execCommand(null, "cp " + path + "/foo " + path + "/bar");
            } catch (Exception e) {
                throw new Error(e);
            }
            if (copyResult[STDERR].length() != 0
                    && ! copyResult[STDERR].startsWith("Warning:")) {
                throw new Error("Copy failed: " + copyResult[STDERR]);
            }

            // read 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Reading " + uri + "/bar");
            }
            try {
                session = connection.openSession();
            } catch(Throwable e) {
                throw new Error(e);
            }
            InputStream sessionOutputStream = session.getStdout();
            command = "cat < " + path + "/bla";
            InputStreamRunner job1 = new InputStreamRunner(session, command);
            job1.setDaemon(true);
            job1.start();   
            
            long done = 0;
            for (;;) {
                int len = sessionOutputStream.read(readbuf, 0, readbuf.length);
                if (len < 0) {
                    break;
                }
                done += len;
                if (logger.isDebugEnabled()) {
                    logger.debug("Read {} bytes", done);
                }
            }
            sessionOutputStream.close();
            
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
            System.err.println("usage: java " + SshFileBenchmark.class.getName() 
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        test = new SshFileBenchmark(baseDirUrl);
   
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
        
    private class OutputStreamRunner extends Thread {
        private final String command;
        private final Session session;
        
        public OutputStreamRunner(Session session, String command) {
            this.command = command;
            this.session = session;
        }
        
        public void run() {
            try {
                execCommand(session, command);
            } catch (Exception e) {
                // TODO: what to do here?
            } finally {
                session.close();
            }
        }
    }
    
    private class InputStreamRunner extends Thread {
        private final String command;
        private final Session session;
        
        public InputStreamRunner(Session session, String command) {
            this.command = command;
            this.session = session;
        }
        
        public void run() {
            try {
                execCommand(command);
            } catch (Exception e) {
                // TODO: what to do here?
            }
        }
        
        private String[] execCommand(String cmd) throws Exception {
            try {
                String[] result = new String[3];

                if (logger.isInfoEnabled()) {
                    logger.info("command: " + cmd);
                }
                session.execCommand(cmd);
                // see http://www.trilead.com/Products/Trilead-SSH-2-Java/FAQ/#blocking
                InputStream stderr = new StreamGobbler(session.getStderr());

                BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
                StringBuffer err = new StringBuffer();
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    err.append(line);
                    err.append("\n");
                }
                result[STDERR] = err.toString();
                while (session.getExitStatus() == null) {
                    Thread.sleep(500);
                }
                result[EXIT_VALUE] = "" + session.getExitStatus();

                if (logger.isDebugEnabled()) {
                    logger.debug("STDERR: " + result[STDERR]);
                    logger.debug("EXIT:   " + result[EXIT_VALUE]);
                }
                return result;
            } finally {
                session.close();
            }
        }
    }
}

