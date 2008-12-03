package test.file;

import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

import test.misc.AdaptorTestResult;
import test.misc.AdaptorTestResultEntry;

public class FileOutputStreamAdaptorTest {

    public static void main(String[] args) {
        System.setProperty("FileOutputStream.adaptor.name", args[0]);
        FileOutputStreamAdaptorTest a = new FileOutputStreamAdaptorTest();
        a.test(args[0], args[1]).print();
    }

    public AdaptorTestResult test(String adaptor, String host) {

        AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor,
                host);

        FileOutputStream out = null;
        URL url;
        try {
            url = URLFactory.createURL("any://" + host
                    + "/tmp/JavaGAT-test-fileoutputstream");
            out = FileFactory.createFileOutputStream(url);
        } catch (Throwable e) {
            adaptorTestResult.put("create", new AdaptorTestResultEntry(false,
                    0, e));
            return adaptorTestResult;
        }
        byte[] large = new byte[10 * 1024 * 1024];
        for (int i = 0; i < large.length; i++) {
            large[i] = 'a';
        }
        adaptorTestResult.put("write (small)", writeTest(out, "test\n"));
        adaptorTestResult.put("write (large)",
                writeTest(out, new String(large)));
        adaptorTestResult.put("flush        ", flushTest(out));
        adaptorTestResult.put("close        ", closeTest(out));
        adaptorTestResult.put("remove       ", removeTest(url));

        return adaptorTestResult;
    }

    private AdaptorTestResultEntry writeTest(FileOutputStream out, String text) {
        long start = System.currentTimeMillis();
        try {
            out.write(text.getBytes());
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry flushTest(FileOutputStream out) {
        long start = System.currentTimeMillis();
        try {
            out.flush();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry closeTest(FileOutputStream out) {
        long start = System.currentTimeMillis();
        try {
            out.close();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry removeTest(URL url) {
        long start = System.currentTimeMillis();
        try {
            NSEntry ns = NSFactory.createNSEntry(url);
            ns.remove();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }
}
