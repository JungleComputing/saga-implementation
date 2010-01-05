package benchmarks.file;

import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileFactory;
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

public class SagaFileBenchmark implements Benchmark {

    private Logger logger = LoggerFactory.getLogger(SagaFileBenchmark.class);

    private URL baseDirUrl;
    private Buffer buf;
    
    public SagaFileBenchmark(String baseDirUrl) throws SagaException {
        this.baseDirUrl = URLFactory.createURL(baseDirUrl);
        
        buf = BufferFactory.createBuffer(new byte[FileBenchmark.WRITE_BUF_SIZE]);
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
            
            // create a big file 'foo'
            logger.info("Creating file " + baseDirUrl + "/foo of " 
                    + FileBenchmark.BIG_FILE_SIZE + " bytes");
            URL fooUrl = URLFactory.createURL("foo");
            File foo = baseDir.openFile(fooUrl, Flags.CREATE.getValue());
            long written = 0;
            while (written < FileBenchmark.BIG_FILE_SIZE) {
                int max = (int)Math.min(buf.getSize(), 
                        FileBenchmark.BIG_FILE_SIZE - written);
                written += foo.write(buf, 0, max);
                logger.debug("Wrote {} bytes", written);
            }
            foo.close();
            
            // copy 'foo' to 'bar'
            logger.info("Copying " + baseDirUrl + "/foo to " + baseDirUrl + "/bar");
            URL barUrl = URLFactory.createURL("bar");
            baseDir.copy(fooUrl, barUrl);

            // read 'bar'
            logger.info("Reading " + baseDirUrl + "/bar");
            File bar = baseDir.openFile(barUrl, Flags.READ.getValue());
            long totalRead = 0;
            int read = 0;
            do {
                read = bar.read(buf);
                totalRead += read;
                logger.debug("Read {} bytes", totalRead);
            } while (read > 0);
            bar.close();
            
            // delete all files
            logger.info("Deleting 'foo' and 'bar'");
            baseDir.remove("foo");
            baseDir.remove("bar");
            
            baseDir.close();
            
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

    public void close() {
        try {
            logger.info("Cleaning up");
            Session defaultSession = SessionFactory.createSession();
            defaultSession.close();
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("usage: java " + SagaFileBenchmark.class.getName() 
                    + " <basedir-url> <#runs>");
            return;
        }

        String baseDirUrl = args[0];
        int runs = Integer.parseInt(args[1]);

        Benchmark test;
        try {
            test = new SagaFileBenchmark(baseDirUrl);
        } catch (SagaException e) {
            Util.printSagaException(e);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
    
}
