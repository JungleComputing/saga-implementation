package benchmarks.namespace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class LocalNSBenchmark implements Benchmark {

    private Logger logger = LoggerFactory.getLogger(LocalNSBenchmark.class);

    private File baseDir;
    
    public LocalNSBenchmark(String basePath) throws Exception {
        baseDir = new File(basePath);
        
        if (!baseDir.exists()) {
            throw new FileNotFoundException("base directory '" + baseDir
                    + "' does not exist");
        } else if (!baseDir.isDirectory()) {
            throw new FileNotFoundException("base directory '" + baseDir
                    + "' is not a directory");
        }
    }

    public void run() {
        try {
            // sanity check: is the base directory empty? If not, bail out
            if (baseDir.list().length != 0) {
                throw new Error("base directory '" + baseDir + " is not empty!");
            }
            
            // create DIR_COUNT directories ('/dir000' to '/dirXXX') 
            if (logger.isInfoEnabled()) {
                logger.info("Creating {} directories in {}", 
                        NSBenchmark.DIR_COUNT, baseDir);
            }
            for (int i = 0; i < NSBenchmark.DIR_COUNT; i++) {
                String name = String.format("dir%03d", i);
                File dir = new File(baseDir, name);
                dir.mkdir();
            }

            // Create SUBDIR_COUNT sub-directories in each directory 
            // ('/dir00/subdir000' to '/dirXXX/subdirYYY')
            if (logger.isInfoEnabled()) {
                logger.info("Creating " + NSBenchmark.SUBDIR_COUNT 
                        + " subdirectories in each directory");
            }
            for (File dir: baseDir.listFiles()) {
                if (dir.isDirectory()) {
                    for (int j = 0; j < NSBenchmark.SUBDIR_COUNT; j++) {
                        String name = String.format("subdir%03d", j);
                        File subdir = new File(dir, name);
                        subdir.mkdir();
                    }
                }
            }
                        
            // in each sub-directory, create FILE_COUNT empty text files ('file00' to 
            // 'fileZZZ').
            if (logger.isInfoEnabled()) {
                logger.info("Creating {} files in each subdirectory", 
                        NSBenchmark.FILE_COUNT);
            }
                     
            for (File dir: baseDir.listFiles()) {
                if (dir.isDirectory()) {
                    for (File subdir: dir.listFiles()) {
                        for (int i = 0; i < NSBenchmark.FILE_COUNT; i++) {
                            String name = String.format("file%03d", i);
                            File file = new File(subdir, name);
                            
                            FileWriter w = new FileWriter(file);
                            w.close();
                        }
                    }
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
            for (File dir: baseDir.listFiles()) {
                /*
                for (File subdir: dir.listFiles()) {
                    if (subdir.isDirectory()) {
                        subdir.renameTo(new File(subdir.getParent(),
                                subdir.getName().replace("dir", "d")));
                    }
                }
                */
                dir.renameTo(new File(dir.getParent(),
                        dir.getName().replace("dir", "d")));
            }
            
            // Copy all files
            if (logger.isInfoEnabled()) {
                logger.info("Copying all files");
            }
            byte[] copyBuf = new byte[1024 * 32];
            for (File dir: baseDir.listFiles()) {
                for (File subdir: dir.listFiles()) {
                    for (File file: subdir.listFiles()) {
                        FileInputStream fin = null;
                        FileOutputStream fout = null;
                        
                        try {
                            fin = new FileInputStream(file);
                            File newFile = new File(file.getParent(),
                                    file.getName().replace("file", "f"));
                            fout = new FileOutputStream(newFile);
                        
                            int readBytes = 0;
                            while ((readBytes = fin.read(copyBuf)) != -1) {
                                fout.write(copyBuf, 0, readBytes);
                            }
                        } finally {
                            if (fin != null) fin.close();
                            if (fout != null) fout.close();
                        }
                    }
                }
            }
            
            // delete all files and directories
            if (logger.isInfoEnabled()) {
                logger.info("Deleting all files and directories");
            }
            for (File entry: baseDir.listFiles()) {
                removeRecursively(entry, true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listDirectory(File d) throws Exception {
        for (File entry : d.listFiles()) {
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

    private void removeRecursively(File dir, boolean removeDir)
    throws Exception 
    {
        for (File entry: dir.listFiles()) {
            if (entry.isDirectory()) {
                removeRecursively(entry, true);
            } else {
                if (!entry.delete()) {
                    throw new Exception("Could not remove '" + entry + "'");
                }
            }
        }

        if (removeDir && !dir.delete()) {
            throw new Exception("Could not remove '" + dir + "'");
        }
    }
    
    public void close() {
        // nothing to do
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("usage: java " + LocalNSBenchmark.class.getName()
                    + " <basedir> <#runs>");
            return;
        }

        String baseDir = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new LocalNSBenchmark(baseDir);
        } catch (Exception e) {
            System.err.println("Got exception " + e);
            e.printStackTrace(System.err);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }

}
