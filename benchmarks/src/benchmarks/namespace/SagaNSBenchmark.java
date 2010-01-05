package benchmarks.namespace;

import java.util.List;

import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
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
                System.err.println("ERROR: base dir '" + baseDirUrl 
                        + "' is not empty!");
                return;
            }
            
            // create DIR_COUNT directories ('/dir000' to '/dirXXX') 
            logger.info("Creating " + NSBenchmark.DIR_COUNT 
                    + " directories in " + baseDirUrl);
            for (int i = 0; i < NSBenchmark.DIR_COUNT; i++) {
                String dir = String.format("dir%03d", i);
                URL dirUrl = URLFactory.createURL(dir);
                baseDir.makeDir(dirUrl);
            }

            // Create SUBDIR_COUNT sub-directories in each directory 
            // ('/dir000/subdir000' to '/dirXXX/subdirYYY')
            logger.info("Creating " + NSBenchmark.SUBDIR_COUNT 
                    + " subdirectories in each directory");
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
                        
            // in each sub-directory, create FILE_COUNT text files ('file000' to 
            // 'fileZZZ'). The contents of each file is its own URL. 
            logger.info("Creating " + NSBenchmark.FILE_COUNT 
                    + " files in each subdirectory");
            for (URL dirUrl: baseDir.list()) {
                if (baseDir.isDir(dirUrl)) {
                    Directory dir = baseDir.openDirectory(dirUrl);
                    
                    for (URL subdirUrl: dir.list()) {
                        Directory subdir = dir.openDirectory(subdirUrl);

                        for (int i = 0; i < NSBenchmark.FILE_COUNT; i++) {
                            String file = String.format("file%03d", i);
                            URL fileUrl = URLFactory.createURL(file);
                            
                            File f = subdir.openFile(fileUrl, 
                                    Flags.CREATE.getValue());

                            String url = f.getURL().toString() + "\n";
                            Buffer b = BufferFactory.createBuffer(url.getBytes());
                            f.write(b);
                            
                            f.close();
                        }
         
                        subdir.close();
                    }
                    
                    dir.close();
                }
            }
                            
            // print the type (file or directory) and size (in bytes) of all 
            // entries in the volume
            logger.info("Logging type and size of all entries (at DEBUG level)");
            listDirectory(baseDir);
            
            // find all entries with '01' in their name
            logger.info("Finding all entries with '01' in their name");
            List<URL> matches = baseDir.find("*01*", Flags.RECURSIVE.getValue());
            logger.info("Found " + matches.size() + " entries");

            if (logger.isDebugEnabled()) {
                for (URL match: matches) {
                    logger.debug(match.toString());
                }
            }
            
            // delete all files and directories
            logger.info("Deleting all files and directories");
            baseDir.remove("*", Flags.RECURSIVE.getValue());
            
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
            logger.info("Cleaning up");
            Session defaultSession = SessionFactory.createSession(true);
            defaultSession.close();
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("usage: java " + SagaNSBenchmark.class.getName()
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
