package benchmarks.namespace;

import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;
import benchmarks.Util;

public class SagaNSBenchmark implements Benchmark {

    private Logger logger = LoggerFactory.getLogger(SagaNSBenchmark.class);

    private URL baseDirUrl;
    
    public SagaNSBenchmark(String baseDirUrl) throws SagaException {
        this.baseDirUrl = URLFactory.createURL(baseDirUrl);
    }

    public void run() {
        try {
            Directory baseDir = FileFactory.createDirectory(baseDirUrl);
           
            // sanity check: is the base directory empty? If not, bail out
            if (baseDir.getNumEntries() != 0) {
                throw new Error("base dir '" + baseDirUrl  + "' is not empty!");
            }
            
            // create DIR_COUNT directories ('/dir000' to '/dirXXX') 
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.DIR_COUNT 
                        + " directories in " + baseDirUrl);
            }
            for (int i = 0; i < NSBenchmark.DIR_COUNT; i++) {
                String dir = String.format("dir%03d", i);
                URL dirUrl = URLFactory.createURL(dir);
                baseDir.makeDir(dirUrl);
            }

            // Create SUBDIR_COUNT sub-directories in each directory 
            // ('/dir000/subdir000' to '/dirXXX/subdirYYY')
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.SUBDIR_COUNT 
                        + " subdirectories in each directory");
            }
            for (URL dirUrl: baseDir.list()) {
                if (baseDir.isDir(dirUrl)) {
                    NSDirectory dir = baseDir.openDir(dirUrl);
                    for (int j = 0; j < NSBenchmark.SUBDIR_COUNT; j++) {
                        String subdir = String.format("subdir%03d", j);
                        URL subdirUrl = URLFactory.createURL(subdir);
                        dir.makeDir(subdirUrl);
                    }
                    dir.close();
                }
            }
                        
            // in each sub-directory, create FILE_COUNT empty text files ('file000' to 
            // 'fileZZZ').
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.FILE_COUNT 
                        + " files in each subdirectory");
            }
            for (URL dirUrl: baseDir.list()) {
                if (baseDir.isDir(dirUrl)) {
                    NSDirectory dir = baseDir.openDir(dirUrl);
                    
                    for (URL subdirUrl: dir.list()) {
                        NSDirectory subdir = dir.openDir(subdirUrl);

                        for (int i = 0; i < NSBenchmark.FILE_COUNT; i++) {
                            String file = String.format("file%03d", i);
                            URL fileUrl = URLFactory.createURL(file);
                            
                            NSEntry f = subdir.open(fileUrl, Flags.CREATE.getValue());                          
                            f.close();
                        }
         
                        subdir.close();
                    }
                    
                    dir.close();
                }
            }
                            
            // print the type (file or directory) and size (in bytes) of all 
            // entries in the volume
            if (logger.isInfoEnabled()) {
                logger.info("Logging type and size of all entries (at DEBUG level)");
            }
            listDirectory(baseDir);
            
            /* Commented out, because C++ implementation does not implement
             * patterns yet.

            // find all entries with '01' in their name
            if (logger.isInfoEnabled()) {
                logger.info("Finding all entries with '01' in their name");
            }
            List<URL> matches = baseDir.find("*01*", Flags.RECURSIVE.getValue());
            if (logger.isInfoEnabled()) {
                logger.info("Found " + matches.size() + " entries");
            }

            if (logger.isDebugEnabled()) {
                for (URL match: matches) {
                    logger.debug(match.toString());
                }
            }
            */
            
            // Move all subdirs
            if (logger.isInfoEnabled()) {
                logger.info("Moving all directories");
            }
            for (URL dirUrl: baseDir.list()) {
                if (baseDir.isDir(dirUrl)) {
                    NSDirectory dir = baseDir.openDir(dirUrl);
                    for (URL subdirUrl: dir.list()) {
                        URL targetUrl = URLFactory.createURL(
                                subdirUrl.toString().replace("dir", "d"));
                        dir.move(subdirUrl, targetUrl, Flags.RECURSIVE.getValue());
                    }
                    dir.close();
                }
                URL targetUrl = URLFactory.createURL(
                        dirUrl.toString().replace("dir", "d"));
                baseDir.move(dirUrl, targetUrl, Flags.RECURSIVE.getValue());
            }
            
            // Move all files
            for (URL dirUrl: baseDir.list()) {
                if (baseDir.isDir(dirUrl)) {
                    NSDirectory dir = baseDir.openDir(dirUrl);
                    
                    for (URL subdirUrl: dir.list()) {
                        NSDirectory subdir = dir.openDir(subdirUrl);
                        
                        for (URL filenameUrl : subdir.list()) {
                            URL targetUrl = URLFactory.createURL(
                                    filenameUrl.toString().replace("file", "f"));
                            subdir.copy(filenameUrl, targetUrl);
                        }
         
                        subdir.close();
                    }
                    
                    dir.close();
                }
            }
            
            // delete all files and directories
            if (logger.isInfoEnabled()) {
                logger.info("Deleting all files and directories");
            }
            
            for (URL dirUrl: baseDir.list()) {
                baseDir.remove(dirUrl, Flags.RECURSIVE.getValue());
            }
            
            baseDir.close();
            
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

    private void listDirectory(Directory d) throws SagaException {
        for (URL entry : d.list()) {
            if (d.isEntry(entry)) {
                long size = d.getSize(entry);
                String s = String.format("- %8d %s/%s", size, d.getURL(), entry);
                logger.debug(s);
            } else {
                String s = String.format("d          %s/%s", d.getURL(), entry);
                logger.debug(s);
                Directory subDir = d.openDirectory(entry);
                listDirectory(subDir);
                subDir.close();
            }
        }
    }
    
    public void close() {
        try {
            if (logger.isDebugEnabled()) {
                logger.info("Cleaning up");
            }
            Session defaultSession = SessionFactory.createSession(true);
            defaultSession.close();
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("usage: java " + SagaNSBenchmark.class.getName()
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new SagaNSBenchmark(baseDirUrl);
        } catch (SagaException e) {
            Util.printSagaException(e);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
    
}
