package benchmarks.namespace;

import java.net.URI;
import java.util.Vector;

import org.globus.ftp.FileInfo;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.HostPort;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class GlobusNSBenchmark implements Benchmark {

    private static Logger logger = LoggerFactory.getLogger(GlobusNSBenchmark.class);

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

    // Apparently, you can do "list" only once with a client?
    // The next time it gives an "unexpected reply".
    // Somehow, this is fixed by doing the
    // client.setPassive(); client.setLocalActive(); sequence ???
    @SuppressWarnings("unchecked")
    private Vector<FileInfo> listDir() throws Exception {
        Vector<FileInfo> list = client.list(null, null);
        /*
        String dir = client.getCurrentDir();
        client.close();
        client = createClient();
        client.changeDir(dir);
        */
        client.setPassive();
        client.setLocalActive();
        return list;
    }

    // Apparently, you can do "put" only once with a client?
    // The next time it gives an "unexpected reply".
    // Somehow, this is fixed by doing the
    // client.setPassive(); client.setLocalActive(); sequence ???
    private void put(java.io.File src, String dst) throws Exception {
        client.put(src, dst, false);
        /*
        String dir = client.getCurrentDir();
        client.close();
        client = createClient();
        client.changeDir(dir);
        */
        client.setPassive();
        client.setLocalActive();
    }
    
    public GlobusNSBenchmark(String uriString, GSSCredential credential) 
            throws Exception {
        uri = new URI(uriString);
        host = uri.getHost();
        port = uri.getPort();
        this.credential = credential;
        if (port == -1) {
            port = DEFAULT_GRIDFTP_PORT;
        }
        
        baseDir = uri.getPath();
        
        client = createClient();
        // client.setLocalPassive();
        // client.setActive();

        if (baseDir.equals("")) {
            baseDir = "/";
        }
    }

    public void run() {
        try {
            // sanity check: is the base directory empty? If not, bail out
            client.changeDir(baseDir);

            Vector<FileInfo> list = listDir();

            for (FileInfo f : list) {
                String name = f.getName();
                if (name.equals(".") || name.equals("..")) {
                    continue;
                }
                throw new Error("baseDir " + uri + " is not empty");
            }

            // create DIR_COUNT directories ('/dir000' to '/dirXXX') 
            if (logger.isInfoEnabled()) {
                logger.info("Creating {} directories in {}", 
                        NSBenchmark.DIR_COUNT, baseDir);
            }
            for (int i = 0; i < NSBenchmark.DIR_COUNT; i++) {
                String name = String.format("dir%03d", i);
                logger.debug("  mkdir {}", name);
                client.makeDir(name);
            }

            // Create SUBDIR_COUNT sub-directories in each directory 
            // ('/dir00/subdir000' to '/dirXXX/subdirYYY')
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.SUBDIR_COUNT 
                        + " subdirectories in each directory");
            }

            list = listDir();
            for (FileInfo f : list) {
                String name = f.getName();
                if (name.equals(".") || name.equals("..")) {
                    continue;
                }
                if (f.isDirectory()) {
                    client.changeDir(name);
                    for (int j = 0; j < NSBenchmark.SUBDIR_COUNT; j++) {
                        String subdir = String.format("subdir%03d", j);
                        logger.debug("  mkdir {}/{}", name, subdir);
                        client.makeDir(subdir);
                    }
                    client.changeDir("..");
                }
            }
 
            // in each sub-directory, create FILE_COUNT empty text files ('file00' to 
            // 'fileZZZ').
            if (logger.isInfoEnabled()) {
                logger.info("Creating {} files in each subdirectory", 
                        NSBenchmark.FILE_COUNT);
            }

            java.io.File emptyFile = java.io.File.createTempFile(".GlobusTest", null);
            emptyFile.deleteOnExit();

            list = listDir();
            for (FileInfo f : list) {
                String name = f.getName();
                if (name.equals(".") || name.equals("..")) {
                    continue;
                }
                if (f.isDirectory()) {
                    client.changeDir(name);
                    Vector<FileInfo> list2 = listDir();
                    for (FileInfo f2 : list2) {
                        String name2 = f2.getName();
                        if (name2.equals(".") || name2.equals("..")) {
                            continue;
                        }
                        if (f2.isDirectory()) {
                            client.changeDir(name2);
                            for (int i = 0; i < NSBenchmark.FILE_COUNT; i++) {
                                String n = String.format("file%03d", i);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("  touch " + name + "/" 
                                            + name2 + "/" + n);
                                }
                                
                                put(emptyFile, n);
                            }
                            client.changeDir("..");
                        }
                    }
                    client.changeDir("..");
                }
            }
 
            // print the type (file or directory) and size (in bytes) of all 
            // entries in the volume
            if (logger.isInfoEnabled()) {
                logger.info("Listing type and size of all entries (on loglevel DEBUG)");
            }
            listDirectory(baseDir);
            
            /* Commented out, because C++ implementation does not implement
             * patterns yet.

            // find all entries with '01' in their name
            if (logger.isInfoEnabled()) {
                logger.info("Finding all entries with '01' in their name");
            }
            List<String> matches = new LinkedList<String>();
            findMatches(baseDir, ".*01.*", matches);
            
            if (logger.isInfoEnabled()) {            
                logger.info("Found " + matches.size() + " entries");
            }
            if (logger.isDebugEnabled()) {
                for (String match: matches) {
                    logger.debug(match);
                }
            }
            */
            
            // Move all subdirs
            if (logger.isInfoEnabled()) {
                logger.info("Moving all directories");
            }

            list = listDir();
            for (FileInfo f : list) {
                String name = f.getName();
                if (name.equals(".") || name.equals("..")) {
                    continue;
                }
                if (f.isDirectory()) {
                    String n = name.replace("dir", "d");
                    logger.debug("  mv {} --> {}", name, n);
                    client.rename(name, n);
                }
            }
            
            // Copy all files
            if (logger.isInfoEnabled()) {
                logger.info("Copying all files");
            }
            list = listDir();
            GridFTPClient c1 = createClient();
            GridFTPClient c2 = createClient();
            HostPort hp = c2.setPassive();
            c1.setActive(hp);
            c1.changeDir(baseDir);
            c2.changeDir(baseDir);
            for (FileInfo f : list) {
                String name = f.getName();
                if (name.equals(".") || name.equals("..")) {
                    continue;
                }
                if (f.isDirectory()) {
                    client.changeDir(name);
                    c1.changeDir(name);
                    c2.changeDir(name);
                    Vector<FileInfo> list2 = listDir();
                    for (FileInfo f2 : list2) {
                        String name2 = f2.getName();
                        if (name2.equals(".") || name2.equals("..")) {
                            continue;
                        }
                        if (f2.isDirectory()) {
                            client.changeDir(name2);
                            c1.changeDir(name2);
                            c2.changeDir(name2);
                            Vector<FileInfo> list3 = listDir();
                            for (FileInfo f3 : list3) {
                                String name3 = f3.getName();
                                if (name3.equals(".") || name3.equals("..")) {
                                    continue;
                                }
                                if (f3.isFile()) {
                                    String n = name3.replace("file", "f");
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("  cp " + name + "/" + name2 + "/"
                                                + name3 + " --> " + name + "/" + name2
                                                + "/" + n);
                                    }
                                    c1.transfer(name3, c2, n, false, null);
                                    hp = c2.setPassive();
                                    c1.setActive(hp);
                                }
                            }
                            client.changeDir("..");
                            c1.changeDir("..");
                            c2.changeDir("..");
                        }
                    }
                    client.changeDir("..");
                    c1.changeDir("..");
                    c2.changeDir("..");
                }
            }
            c2.close();
            c1.close();

            // delete all files and directories
            if (logger.isInfoEnabled()) {
                logger.info("Deleting all files and directories");
            }
            removeRecursively();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listDirectory(String d) throws Exception {
        Vector<FileInfo> list = listDir();
        for (FileInfo f : list) {
            String name = f.getName();
            if (name.equals(".") || name.equals("..")) {
                continue;
            }
            if (f.isDirectory()) {
                String s = String.format("d          %s/%s", d, name);
                logger.debug(s);
                client.changeDir(name);
                listDirectory(d + "/" + name);
                client.changeDir("..");
            } else {
                long size = f.getSize();
                String s = String.format("- %8d %s/%s", size, d, name);
                logger.debug(s);
            }
        }
    }
    
    /*
    private void findMatches(File d, String regex, List<String> matches) 
    throws Exception 
    {
        for (File entry : d.listFiles()) {
            if (entry.getName().matches(regex)) {
                matches.add(entry.getPath());
            }
            
            if (entry.isDirectory()) {
                findMatches(entry, regex, matches);
            }
        }
    }
    */

    private void removeRecursively() throws Exception {
        Vector<FileInfo> list = listDir();
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
            System.err.println("usage: java " + GlobusNSBenchmark.class.getName()
                    + " <basedir> <#runs>");
            return;
        }

        String baseDir = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            GSSCredential credential = getDefaultCredential();
            test = new GlobusNSBenchmark(baseDir, credential);
        } catch (Exception e) {
            System.err.println("Got exception " + e);
            e.printStackTrace(System.err);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }

}
