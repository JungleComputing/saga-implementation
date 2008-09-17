package test.file;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

import test.misc.AdaptorTestResult;
import test.misc.AdaptorTestResultEntry;

public class FileInputStreamAdaptorTest {

    public static void main(String[] args) {
        System.setProperty("FileInputStream.adaptor.name", args[0]);
        System.setProperty("JobService.adaptor.name", "javagat");
        FileInputStreamAdaptorTest a = new FileInputStreamAdaptorTest();
        a.test(args[0], args[1]).print();
    }

    public AdaptorTestResult test(String adaptor, String host) {
        
        try {
            Session session = SessionFactory.createSession(true);
            
            Context preferences  = ContextFactory.createContext("preferences");
            preferences.setAttribute("resourcebroker.adaptor.name", "sshtrilead,commandlinessh,local");
            session.addContext(preferences);
        } catch (Throwable e) {
            System.err.println("Could not create session");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        run(host, "fileinputstream-adaptor-test-init.sh");

        AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor,
                host);

        FileInputStream in = null;
        URL url;
        try {
            url = URLFactory.createURL("any://" + host + "/tmp/Saga-test-fileinputstream");

            in = FileFactory.createFileInputStream(url);
        } catch (Throwable e) {
            adaptorTestResult.put("open", new AdaptorTestResultEntry(false, 0, e));
        }
        adaptorTestResult.put("markSupported      ", markSupportedTest(in));
        adaptorTestResult
                .put("available:         ", availableTest(in, 0, true));
        adaptorTestResult.put("read: single char a", readTest(in, 'a', true));
        adaptorTestResult.put("read: single char b", readTest(in, 'b', true));
        adaptorTestResult.put("read: single char c", readTest(in, 'c', true));
        adaptorTestResult.put("read: single char !d", readTest(in, 'q', false));
        adaptorTestResult.put("read: small       ", readTest(in, "efg"
                .getBytes(), true));
        byte[] bytes = new byte[1024 * 1024 * 10];
        byte current = 'h';
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = current;
            if (current == 'z') {
                current = '\n';
            } else if (current == '\n') {
                current = 'a';
            } else {
                current++;
            }
        }
        adaptorTestResult.put("read: large          ",
                readTest(in, bytes, true));
        adaptorTestResult.put("skip: small          ", skipTest(in, 100));
        adaptorTestResult.put("skip: large          ", skipTest(in,
                10 * 1024 * 1024));
        adaptorTestResult.put("close                ", closeTest(in));

        run(host, "fileinputstream-adaptor-test-clean.sh");

        return adaptorTestResult;

    }

    private void run(String host, String script) {
        JobService js;
        JobDescription jd;
        try {
            jd = JobFactory.createJobDescription();
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/sh");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                     new String[] {script});
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { script + " > " + script });
            URL url = URLFactory.createURL("any://" + host);
            js = JobFactory.createJobService(url);
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private AdaptorTestResultEntry availableTest(FileInputStream in,
            long correctValue, boolean correctResult) {
        long start = System.currentTimeMillis();
        boolean correct;
        try {
            correct = in.available() >= correctValue;
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(correct == correctResult,
                (stop - start), null);
    }

    private AdaptorTestResultEntry readTest(FileInputStream in,
            char correctValue, boolean correctResult) {
        long start = System.currentTimeMillis();
        boolean correct;
        try {
            correct = (((char) in.read()) == correctValue);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(correct == correctResult,
                (stop - start), null);
    }

    private AdaptorTestResultEntry readTest(FileInputStream in,
            byte[] correctValue, boolean correctResult) {
        byte[] result = new byte[correctValue.length];
        int read = 0;
        boolean correct = true;
        long start = System.currentTimeMillis();
        while (read != correctValue.length) {
            try {
                read += in.read(result, read, correctValue.length - read);
            } catch (Throwable e) {
                return new AdaptorTestResultEntry(false, 0, e);
            }
        }
        long stop = System.currentTimeMillis();
        boolean printed = false;
        for (int i = 0; i < read; i++) {
            correct = correct && (result[i] == correctValue[i]);
            if (result[i] != correctValue[i] && !printed) {
                System.out.println("i[" + i + "]: result[i]="
                        + (char) result[i] + ", correct[i]="
                        + (char) correctValue[i]);
                printed = true;
            }
        }
        return new AdaptorTestResultEntry(correct, (stop - start), null);
    }

    private AdaptorTestResultEntry skipTest(FileInputStream in, long n) {
        long start = System.currentTimeMillis();
        try {
            long skipped = in.skip(n);
            if (skipped != n) {
                return new AdaptorTestResultEntry(true, System
                        .currentTimeMillis()
                        - start, new Exception("skipped less bytes (" + skipped
                        + ") than correct value (" + n
                        + "), but the correct execution"));
            }
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry markSupportedTest(FileInputStream in) {
        long start = System.currentTimeMillis();
        boolean markSupported = in.markSupported();
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start),
                markSupported ? new Exception("mark supported")
                        : new Exception("mark not supported"));
    }

    private AdaptorTestResultEntry closeTest(FileInputStream in) {
        long start = System.currentTimeMillis();
        try {
            in.close();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }
}
