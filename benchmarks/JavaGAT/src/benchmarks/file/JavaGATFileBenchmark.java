package benchmarks.file;

import java.net.URISyntaxException;

import org.gridlab.gat.GAT;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;
import org.gridlab.gat.io.FileInputStream;
import org.gridlab.gat.io.FileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class JavaGATFileBenchmark implements Benchmark {

    private Logger logger = LoggerFactory.getLogger(SagaFileBenchmark.class);

    private URI baseDirUrl;
    private byte[] buf = new byte[FileBenchmark.WRITE_BUF_SIZE];
    
    public JavaGATFileBenchmark(String baseDirUrl) throws URISyntaxException {
        this.baseDirUrl = new URI(baseDirUrl);
    }
    
    public void run() {
        try {
            File baseDir = GAT.createFile(baseDirUrl);
            
            // sanity check: is the base directory empty? If not, bail out
            if (baseDir.list().length != 0) {
                throw new Error("base directory '" + baseDir + " is not empty!");
            }
 
            // create a big file 'foo'
            URI foo = new URI(baseDir.toGATURI() +  "/foo");
            if (logger.isInfoEnabled()) {
                logger.info("Creating file {} of {} bytes", foo,
                        FileBenchmark.BIG_FILE_SIZE);
            }

            FileOutputStream fos = GAT.createFileOutputStream(foo);
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
            URI bar = new URI(baseDir.toGATURI() +  "/bar");
            if (logger.isInfoEnabled()) {
                logger.info("Copying {} to {}", foo, bar);
            }
                        
            FileInputStream fin = null;
            FileOutputStream fout = null;
            try {
                fin = GAT.createFileInputStream(foo);
                fout = GAT.createFileOutputStream(bar);
                
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
                in = GAT.createFileInputStream(foo);
                
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
            
            File f = GAT.createFile(foo);
            f.delete();
            f = GAT.createFile(bar);
            f.delete();         
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        GAT.end();        
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: java " + JavaGATFileBenchmark.class.getName()
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new JavaGATFileBenchmark(baseDirUrl);
        
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
