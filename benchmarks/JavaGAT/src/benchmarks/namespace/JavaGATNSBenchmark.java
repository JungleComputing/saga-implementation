package benchmarks.namespace;

import java.net.URISyntaxException;

import org.gridlab.gat.GAT;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class JavaGATNSBenchmark implements Benchmark {

    private Logger logger = LoggerFactory.getLogger(SagaNSBenchmark.class);

    private URI baseDirUrl;
    
    public JavaGATNSBenchmark(String baseDirUrl) throws URISyntaxException {
        this.baseDirUrl = new URI(baseDirUrl);
    }

    public void close() {
        GAT.end();        
    }

    public void run() {
        try {
            File baseDir = GAT.createFile(baseDirUrl);
           
            // sanity check: is the base directory empty? If not, bail out
            if (baseDir.list().length != 0) {
                throw new Error("base directory '" + baseDir + " is not empty!");
            }
 
            // create DIR_COUNT directories ('/dir000' to '/dirXXX') 
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.DIR_COUNT 
                        + " directories in " + baseDirUrl);
            }
            for (int i = 0; i < NSBenchmark.DIR_COUNT; i++) {
                String dir = String.format("dir%03d", i);
                URI dirUrl = new URI(baseDirUrl.toString() + "/" + dir);
                logger.debug("  mkdir {}", dirUrl);
                File f = GAT.createFile(dirUrl);
                if (! f.mkdir()) {
                    throw new Error("Could not create dir " + dirUrl);
                }
            }

            // Create SUBDIR_COUNT sub-directories in each directory 
            // ('/dir000/subdir000' to '/dirXXX/subdirYYY')
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.SUBDIR_COUNT 
                        + " subdirectories in each directory");
            }
            for (java.io.File dir: baseDir.listFiles()) {
                if (dir.isDirectory()) {
                    for (int j = 0; j < NSBenchmark.SUBDIR_COUNT; j++) {
                        String subdir = String.format("subdir%03d", j);
                        URI subdirUrl = new URI(((File)dir).toGATURI().toString() + "/" + subdir);
                        logger.debug("  mkdir ", subdirUrl);
                        File f = GAT.createFile(subdirUrl);
                        if (! f.mkdir()) {
                            throw new Error("Could not create dir " + subdirUrl);
                        }
                    }
                }
            }
                        
            // in each sub-directory, create FILE_COUNT empty text files ('file000' to 
            // 'fileZZZ').
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.FILE_COUNT 
                        + " files in each subdirectory");
            }
            for (java.io.File dir: baseDir.listFiles()) {
                if (dir.isDirectory()) {
                    for (java.io.File subdir : dir.listFiles()) {
                        for (int i = 0; i < NSBenchmark.FILE_COUNT; i++) {
                            String file = String.format("file%03d", i);
                            URI fileUrl = new URI(((File)subdir).toGATURI().toString() + "/" + file);
                            
                            if (logger.isDebugEnabled()) {
                                logger.debug("  touch " + fileUrl);
                            }
                            
                            File f = GAT.createFile(fileUrl);
                            if (! f.createNewFile()) {
                                throw new Error("Could not create file " + fileUrl);
                            }
                        }
                    }
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
            for (java.io.File dir: baseDir.listFiles()) {
                /*
                if (baseDir.isDir(dirUrl)) {
                    NSDirectory dir = baseDir.openDir(dirUrl);
                    for (URL subdirUrl: dir.list()) {
                        URL targetUrl = URLFactory.createURL(
                                subdirUrl.toString().replace("dir", "d"));
                        dir.move(subdirUrl, targetUrl, Flags.RECURSIVE.getValue());
                    }
                    dir.close();
                }
                */
                File d = (File) dir;
                URI targetUrl = new URI(
                        ((File)dir).toGATURI().toString().replace("dir", "d"));
                if (logger.isDebugEnabled()) {
                    logger.debug("  mv {} --> {}", ((File)dir).toGATURI(), targetUrl);
                }
                d.move(targetUrl);
            }
            
            // Copy all files
            
            for (java.io.File dir: baseDir.listFiles()) {
                if (dir.isDirectory()) {
                    for (java.io.File subdir : dir.listFiles()) {
                        for (java.io.File file : subdir.listFiles()) {
                            URI targetUrl = new URI(
                                    ((File)file).toGATURI().toString().replace("file", "f"));
                            if (logger.isDebugEnabled()) {
                                logger.debug("  cp " + file.toURI() + " --> " 
                                        + targetUrl); 
                            }
                            ((File) file).copy(targetUrl);
                        }
                    }
                }
            }
            
            // delete all files and directories
            if (logger.isInfoEnabled()) {
                logger.info("Deleting all files and directories");
            }
            
            for (java.io.File dir: baseDir.listFiles()) {
                ((File) dir).recursivelyDeleteDirectory();
            }          
        } catch (Throwable e) {
            throw new Error(e);
        }
    }
    

    private void listDirectory(java.io.File d) throws Exception {
        for (java.io.File entry : d.listFiles()) {
            if (entry.isFile()) {
                long size = entry.length();
                String s = String.format("- %8d %s/%s", size, 
                        d.getAbsolutePath(), entry.getName());
                logger.debug(s);
            } else {
                String s = String.format("d          %s/%s", 
                        d.getAbsolutePath(), entry.getName());
                logger.debug(s);
                listDirectory(entry);
            }
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: java " + JavaGATNSBenchmark.class.getName()
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new JavaGATNSBenchmark(baseDirUrl);
        
            BenchmarkRunner runner = new BenchmarkRunner(test, runs);
            runner.run();
        } catch(Throwable e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                cause.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
    }

}
