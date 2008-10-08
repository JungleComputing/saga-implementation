package test.logicalfile;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

import test.misc.AdaptorTestResult;
import test.misc.AdaptorTestResultEntry;

public class LogicalFileAdaptorTest {

    public static void main(String[] args)  {
        System.setProperty("LogicalFile.adaptor.name", args[0]);
        LogicalFileAdaptorTest a = new LogicalFileAdaptorTest();
        a.test(args[0], args[1].split(",")).print();
    }

    public AdaptorTestResult test(String adaptor, String[] hosts) {

        if (hosts.length != 2) {
            System.err
                    .println("please provide 2 hosts (comma separated, no spaces)");
            throw new Error("Provided wrong number of hosts");
        }

        AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor,
                hosts[0]);

        LogicalFile logicalFile = null;
        URL src;
        URL url1;
        URL url2;
        try {
            Session session = SessionFactory.createSession(true);
            
            Context ftpContext = ContextFactory.createContext("ftp");
            session.addContext(ftpContext);
            
            Context c = ContextFactory.createContext("preferences");
            c.setAttribute("file.adaptor.name", "ftp,local,commandlinessh,sshtrilead");
            session.addContext(c);
            
            logicalFile = LogicalFileFactory.createLogicalFile(
                    URLFactory.createURL("test-logical-file"),Flags.CREATE.or(Flags.READWRITE));
            src = URLFactory.createURL("ftp://ftp.cs.vu.nl/pub/ceriel/LLgen.tar.gz");
            url1 = URLFactory.createURL("any://" + hosts[0] + "/tmp/Saga-test-logical-file");
            url2 = URLFactory.createURL("any://" + hosts[1] + "/tmp/Saga-test-logical-file");
        } catch (Throwable e) {
            adaptorTestResult.put("create", new AdaptorTestResultEntry(false, 0, e));
            return adaptorTestResult;
        }
        
        adaptorTestResult.put("replicate [0]", replicateTest(logicalFile, url1, false));
        adaptorTestResult.put("addLoc    [0]", addLocationTest(logicalFile, src, true));
        adaptorTestResult.put("replicate [1]", replicateTest(logicalFile, url1, true));
        adaptorTestResult.put("removeLoc [1]", removeLocationTest(logicalFile, url1, true));
        adaptorTestResult.put("addLoc    [1]", addLocationTest(logicalFile, url1, true));
        adaptorTestResult.put("replicate2[1]", replicateTest(logicalFile, url1, false));
        adaptorTestResult.put("replicate [2]", replicateTest(logicalFile, url2, true));
        adaptorTestResult.put("remove       ", removeTest(logicalFile));
        
        // cleanup
        try {
            NSEntry n = NSFactory.createNSEntry(url1);
            n.remove();
        } catch(Throwable e) {
            e.printStackTrace();
        }
        
        try {
            NSEntry n = NSFactory.createNSEntry(url2);
            n.remove();
        } catch(Throwable e) {
            e.printStackTrace();
        }
        
        return adaptorTestResult;

    }

    private AdaptorTestResultEntry addLocationTest(LogicalFile logicalFile,
            URL toBeAdded, boolean expectSuccess) {
        long start = System.currentTimeMillis();
        try {
            logicalFile.addLocation(toBeAdded);
        } catch (Throwable e) {
            if (expectSuccess) {
                return new AdaptorTestResultEntry(false, 0L, e);
            }
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry replicateTest(LogicalFile logicalFile,
            URL toBeReplicated, boolean expectSuccess) {
        long start = System.currentTimeMillis();
        try {
            logicalFile.replicate(toBeReplicated);
        } catch (Throwable e) {
            if (expectSuccess) {
                return new AdaptorTestResultEntry(false, 0L, e);
            }
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry removeLocationTest(LogicalFile logicalFile,
            URL toBeRemoved, boolean expectSuccess) {
        long start = System.currentTimeMillis();
        try {
            logicalFile.removeLocation(toBeRemoved);
        } catch (Throwable e) {
            if (expectSuccess) {
                return new AdaptorTestResultEntry(false, 0L, e);
            }
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }
    

    private AdaptorTestResultEntry removeTest(LogicalFile logicalFile) {
        long start = System.currentTimeMillis();
        try {
            logicalFile.remove();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }
}
