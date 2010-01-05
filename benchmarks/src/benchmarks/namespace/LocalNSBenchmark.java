package benchmarks.namespace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

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
                logger.error("base directory '" + baseDir + " is not empty!");
                return;
            }
            
            // create DIR_COUNT directories ('/dir000' to '/dirXXX') 
            logger.info("Creating {} directories in {}", 
                    NSBenchmark.DIR_COUNT, baseDir);
            for (int i = 0; i < NSBenchmark.DIR_COUNT; i++) {
                String name = String.format("dir%03d", i);
                File dir = new File(baseDir, name);
                dir.mkdir();
            }

            // Create SUBDIR_COUNT sub-directories in each directory 
            // ('/dir00/subdir000' to '/dirXXX/subdirYYY')
            logger.info("Creating " + NSBenchmark.SUBDIR_COUNT 
                    + " subdirectories in each directory");
            for (File dir: baseDir.listFiles()) {
                if (dir.isDirectory()) {
                    for (int j = 0; j < NSBenchmark.SUBDIR_COUNT; j++) {
                        String name = String.format("subdir%03d", j);
                        File subdir = new File(dir, name);
                        subdir.mkdir();
                    }
                }
            }
                        
            // in each sub-directory, create FILE_COUNT text files ('file00' to 
            // 'fileZZZ'). The contents of each file is its own filename 
            logger.info("Creating {} files in each subdirectory", 
                    NSBenchmark.FILE_COUNT);
                     
            for (File dir: baseDir.listFiles()) {
                if (dir.isDirectory()) {
                    for (File subdir: dir.listFiles()) {
                        for (int i = 0; i < NSBenchmark.FILE_COUNT; i++) {
                            String name = String.format("file%03d", i);
                            File file = new File(subdir, name);
                            
                            FileWriter w = new FileWriter(file);
                            w.write(file.getAbsolutePath() + "\n");
                            w.close();
                        }
                    }
                }
            }
                            
            // print the type (file or directory) and size (in bytes) of all 
            // entries in the volume
            logger.info("Listing type and size of all entries (on loglevel DEBUG)");
            listDirectory(baseDir);
            
            // find all entries with '01' in their name
            logger.info("Finding all entries with '01' in their name");
            List<String> matches = new LinkedList<String>();
            findMatches(baseDir, ".*01.*", matches);
                        
            logger.info("Found " + matches.size() + " entries");
            if (logger.isDebugEnabled()) {
                for (String match: matches) {
                    logger.debug(match);
                }
            }
            
            // delete all files and directories
            logger.info("Deleting all files and directories");
            removeRecursively(baseDir, false);
            
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
            System.out.println("usage: java " + LocalNSBenchmark.class.getName()
                    + " <basedir> <#runs>");
            return;
        }

        String baseDir = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new LocalNSBenchmark(baseDir);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }

}
