package benchmarks.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class LocalFileBenchmark implements Benchmark {

    private static Logger logger = LoggerFactory.getLogger(LocalFileBenchmark.class);

    private File baseDir;
    
    public LocalFileBenchmark(String basePath) throws Exception {
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
            // sanity check: is the mounted directory empty? If not, bail out
            if (baseDir.list().length != 0) {
                throw new Error("base directory '" + baseDir + " is not empty!");
            }
            
            // create a big file 'foo'
            File foo = new File(baseDir, "foo");
            if (logger.isInfoEnabled()) {
                logger.info("Creating file {} of {} bytes", foo,
                        FileBenchmark.BIG_FILE_SIZE);
            }

            FileOutputStream fos = new FileOutputStream(foo);
            FileChannel fc = fos.getChannel();
            ByteBuffer buf = 
                    ByteBuffer.allocateDirect(FileBenchmark.WRITE_BUF_SIZE);
            
            long written = 0;
            while (written < FileBenchmark.BIG_FILE_SIZE) {
                int max = (int)Math.min(buf.capacity(), 
                        FileBenchmark.BIG_FILE_SIZE - written);
                buf.limit(max);
                written += fc.write(buf);
                if (logger.isDebugEnabled()) {
                    logger.debug("Wrote {} bytes", written);
                }
                buf.flip();
            }
            fc.close();
            
            // copy 'foo' to 'bar'
            File bar = new File(baseDir, "bar");
            if (logger.isInfoEnabled()) {
                logger.info("Copying {} to {}", foo, bar);
            }
                        
            FileInputStream fin = null;
            FileOutputStream fout = null;
            try {
                fin = new FileInputStream(foo);
                fout = new FileOutputStream(bar);
                
                byte[] copyBuf = new byte[FileBenchmark.COPY_BUF_SIZE];
                int readBytes = 0;
                while ((readBytes = fin.read(copyBuf)) != -1) {
                    fout.write(copyBuf, 0, readBytes);
                }
            } finally {
                if (fin != null) fin.close();
                if (fout != null) fout.close();
            }
            
            // read 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Reading {}", bar);
            }
            FileInputStream in = null;
            try {
                in = new FileInputStream(foo);
                
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
            removeRecursively(baseDir, false);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRecursively(File dir, boolean removeDir) throws Exception {
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
            System.err.println("usage: java " + LocalFileBenchmark.class.getName()
                    + " <basedir> <#runs>");
            return;
        }

        String baseDir = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new LocalFileBenchmark(baseDir);
        } catch (Exception e) {
            System.err.println("Got exception " + e);
            e.printStackTrace(System.err);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }

}
