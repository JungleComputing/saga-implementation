package test.namespace;

import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.DoesNotExistException;
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

public class NSEntryAdaptorTest {

    public static void main(String[] args) {
        System.setProperty("NSEntry.adaptor.name", args[0]);
        System.setProperty("NSDirectory.adaptor.name", args[0]);
        System.setProperty("JobService.adaptor.name", "javagat");
        NSEntryAdaptorTest a = new NSEntryAdaptorTest();
        a.test(args[0], args[1]).print();
    }

    public AdaptorTestResult test(String adaptor, String host) {

        try {
            Session session = SessionFactory.createSession(true);
            
            Context ftpContext = ContextFactory.createContext("ftp");
            session.addContext(ftpContext);
            Context preferences  = ContextFactory.createContext("preferences");
            preferences.setAttribute("resourcebroker.adaptor.name", "sshtrilead,local");
            session.addContext(preferences);
        } catch (Throwable e) {
            System.err.println("Could not create session");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        
        run(host, "nsentry-adaptor-test-init.sh");
        run("localhost", "nsentry-adaptor-test-init.sh");

        AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor,
                host);

        adaptorTestResult.put("exists: absolute existing file     ", existTest(
                "any://" + host + "/tmp/Saga-test-exists-file", true));
        adaptorTestResult.put("exists: absolute existing dir      ", existDirTest(
                "any://" + host + "/tmp/Saga-test-exists-dir", true));
        adaptorTestResult.put("exists: absolute non-existing file ", existTest(
                "any://" + host + "/tmp/Saga-test-exists-fake", false));

        adaptorTestResult.put("exists: relative existing file     ", existTest(
                "/tmp/Saga-test-exists-file", true));
        adaptorTestResult.put("exists: absolute existing dir      ", existDirTest(
                "/tmp/Saga-test-exists-dir", true));
        adaptorTestResult.put("exists: absolute non-existing file ", existTest(
                "/tmp/Saga-test-exists-fake", false));

        adaptorTestResult.put("copy: absolute existing file       ", copyTest(
                "any://" + host + "/tmp/Saga-test-exists-file",
                "any://" + host + "/tmp/Saga-test-exists-file.copy"));
        adaptorTestResult.put("copy: relative existing file       ", copyTest(
                "/tmp/Saga-test-exists-file", "/tmp/Saga-test-exists-file.copy"));

        adaptorTestResult.put("remove: absolute existing file     ", removeTest(
                "any://" + host + "/tmp/Saga-test-exists-file.copy"));
        adaptorTestResult.put("remove: relative existing file     ", removeTest(
                "/tmp/Saga-test-exists-file.copy"));

        adaptorTestResult.put("move: absolute existing file       ", moveTest(
                "any://" + host + "/tmp/Saga-test-exists-file",
                "any://" + host + "/tmp/Saga-test-exists-file.copy"));
        adaptorTestResult.put("move: relative existing file       ", moveTest(
                "/tmp/Saga-test-exists-file", "/tmp/Saga-test-exists-file.copy"));

        run(host, "nsentry-adaptor-test-clean.sh");
        run("localhost", "nsentry-adaptor-test-clean.sh");

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
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS, new String[] {host});
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
    
    private void doClose(NSEntry e) {
        try {
            if (e != null) {
                e.close();
            }
        } catch(Throwable ex) {
            // ignored
        }
    }

    private AdaptorTestResultEntry existTest(String u, boolean correctValue) {
        long start = System.currentTimeMillis();

        URL url;
        try {
            url = URLFactory.createURL(u);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        
        NSEntry entry = null;
        try {
            entry = NSFactory.createNSEntry(url);
            if (! correctValue) {
                return new AdaptorTestResultEntry(false, 0, 
                        new Exception("Should throw DoesNotExist"));
            }
        } catch (DoesNotExistException e) {
            if (correctValue) {
                return new AdaptorTestResultEntry(false, 0, e);
            }
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        } finally {
            doClose(entry);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }
    

    private AdaptorTestResultEntry existDirTest(String u, boolean correctValue) {
        long start = System.currentTimeMillis();

        URL url;
        try {
            url = URLFactory.createURL(u);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        NSEntry entry = null;
        try {
            entry = NSFactory.createNSDirectory(url);
            if (! correctValue) {
                return new AdaptorTestResultEntry(false, 0, 
                        new Exception("Should throw DoesNotExist"));
            }
        } catch (DoesNotExistException e) {
            if (correctValue) {
                return new AdaptorTestResultEntry(false, 0, e);
            }
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        } finally {
            doClose(entry);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry removeTest(String u) {
        long start = System.currentTimeMillis();
        NSEntry entry = null;
        URL url;
        try {
            url = URLFactory.createURL(u);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }
        try {
            entry = NSFactory.createNSEntry(url);
            entry.remove();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        } finally {
            doClose(entry);
        }

        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry copyTest(String u1, String u2) {
        long start = System.currentTimeMillis();
        NSEntry entry = null;
        URL url1, url2;
        try {
            url1 = URLFactory.createURL(u1);
            url2 = URLFactory.createURL(u2);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }

        try {
            entry = NSFactory.createNSEntry(url1);
            entry.copy(url2);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        } finally {
            doClose(entry);
        }
        entry = null;
        try {
            entry = NSFactory.createNSEntry(url2);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Copy does not seem to exist?", e));
        } finally {
            doClose(entry);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry moveTest(String u1, String u2) {
        long start = System.currentTimeMillis();
        NSEntry entry = null;
        URL url1, url2;
        try {
            url1 = URLFactory.createURL(u1);
            url2 = URLFactory.createURL(u2);
        } catch(Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        }

        try {
            entry = NSFactory.createNSEntry(url1);
            entry.move(url2);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0, e);
        } finally {
            doClose(entry);
        }
        entry = null;
        try {
            entry = NSFactory.createNSEntry(url2);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Destination does not seem to exist?", e));
        } finally {
            doClose(entry);
        }
        entry = null;
        try {
            entry = NSFactory.createNSEntry(url1);
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("Source still seems to exist after move"));
        } catch (DoesNotExistException e) {
            // OK
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0,
                    new Exception("wrong exception", e));
        } finally {
            doClose(entry);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }
}
