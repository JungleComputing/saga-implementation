package benchmarks.file;

import java.io.IOException;

import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;
import benchmarks.Util;

public class SagaFileStreamBenchmark implements Benchmark {

    private static Logger logger = LoggerFactory.getLogger(SagaFileStreamBenchmark.class);

    private URL baseDirUrl;
    private byte[] buf;
    
    public SagaFileStreamBenchmark(String baseDirUrl) throws SagaException {
        this.baseDirUrl = URLFactory.createURL(baseDirUrl);
        
        buf = new byte[FileBenchmark.WRITE_BUF_SIZE];
    }

    public void run() {
        try {
            Directory baseDir = FileFactory.createDirectory(baseDirUrl, Flags.READWRITE.getValue());
            
            // sanity check: is the base directory empty? If not, bail out
            if (baseDir.getNumEntries() != 0) {
                throw new Error("base dir '" + baseDirUrl  + "' is not empty!");
            }
            
            // create a big file 'foo'
            if (logger.isInfoEnabled()) {
                logger.info("Creating file " + baseDirUrl + "/foo of " 
                        + FileBenchmark.BIG_FILE_SIZE + " bytes");
            }
            URL fooUrl = URLFactory.createURL("foo");
            FileOutputStream foo = baseDir.openFileOutputStream(fooUrl, false);
            long written = 0;
            while (written < FileBenchmark.BIG_FILE_SIZE) {
                int max = (int)Math.min(buf.length, 
                        FileBenchmark.BIG_FILE_SIZE - written);
                foo.write(buf, 0, max);
                written += max;
                if (logger.isDebugEnabled()) {
                    logger.debug("Wrote {} bytes", written);
                }
            }
            foo.close();
            
            // copy 'foo' to 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Copying " + baseDirUrl + "/foo to " + baseDirUrl + "/bar");
            }
            URL barUrl = URLFactory.createURL("bar");
            baseDir.copy(fooUrl, barUrl);

            // read 'bar'
            if (logger.isInfoEnabled()) {
                logger.info("Reading " + baseDirUrl + "/bar");
            }
            FileInputStream bar = baseDir.openFileInputStream(barUrl);
            long totalRead = 0;
            int read = 0;
            do {
                read = bar.read(buf);
                if (read > 0) {
                    totalRead += read;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Read {} bytes", totalRead);
                    }
                }
            } while (read > 0);
            bar.close();
            
            // delete all files
            if (logger.isInfoEnabled()) {
                logger.info("Deleting 'foo' and 'bar'");
            }
            baseDir.remove("foo");
            baseDir.remove("bar");
            
            baseDir.close();
            
        } catch (SagaException e) {
            Util.printSagaException(e);
        } catch(IOException e) {
            System.err.println("Got IOException" + e);
            e.printStackTrace(System.err);
        }
    }

    public void close() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Cleaning up");
            }
            Session defaultSession = SessionFactory.createSession();
            defaultSession.close();
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("usage: java " + SagaFileStreamBenchmark.class.getName() 
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new SagaFileStreamBenchmark(baseDirUrl);
        } catch (SagaException e) {
            Util.printSagaException(e);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }    
}
