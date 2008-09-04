package test.job;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
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

public class JobAdaptorTest {

    /**
     * @param args
     *     first argument names the adaptor tested,
     *     second argument gives an URL for the server,
     *     third argument gives an URL for the "data" sub-directory on THIS host.
     */
    public static void main(String[] args) {
        System.setProperty("JobService.adaptor.name", args[0]);
        JobAdaptorTest a = new JobAdaptorTest();
        a.test(args[0], args[1], args[2]).print();
    }

    public AdaptorTestResult test(String adaptor, String serverURL, String dirURL) {
        
        AdaptorTestResult adaptorTestResult = new AdaptorTestResult(adaptor, serverURL);
        
        URL url;
        try {
            url = URLFactory.createURL(serverURL);
            Session defaultSession = SessionFactory.createSession();
            Context context = ContextFactory.createContext("ftp");
            defaultSession.addContext(context);
        } catch (Throwable e) {
            e.printStackTrace();
            adaptorTestResult.put("init", new AdaptorTestResultEntry(false, 0, e));
            return adaptorTestResult;
        }
        
        adaptorTestResult.put("submit job easy  ", submitJobEasy(url));
        adaptorTestResult.put("submit job parallel", submitJobParallel(url));
        adaptorTestResult.put("submit job stdout", submitJobStdout(url, dirURL));
        adaptorTestResult.put("submit job stderr", submitJobStderr(url, dirURL));
        adaptorTestResult.put("submit job prestage", submitJobPreStage(url, dirURL));
        adaptorTestResult.put("submit job poststage", submitJobPostStage(url, dirURL));
        adaptorTestResult.put("submit job environment", submitJobEnvironment(url, dirURL));
        return adaptorTestResult;
    }

    private AdaptorTestResultEntry submitJobEasy(URL url) {
        JobService js;
        JobDescription jd;
        try {      
            jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/echo");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "test", "1", "2", "3"});

            js = JobFactory.createJobService(url);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long start = System.currentTimeMillis();
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }

    private AdaptorTestResultEntry submitJobParallel(URL url) {
        JobService js;
        JobDescription jd;
        try {      
            jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/echo");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "test", "1", "2", "3"});
            jd.setAttribute(JobDescription.NUMBEROFPROCESSES, "2");
            jd.setAttribute(JobDescription.PROCESSESPERHOST, "2");
            js = JobFactory.createJobService(url);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long start = System.currentTimeMillis();
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(true, (stop - start), null);
    }


    private AdaptorTestResultEntry submitJobStdout(URL url, String dirURL) {
        JobService js;
        JobDescription jd;
        URL du;
        try { 
            du = URLFactory.createURL(dirURL);
            du.setPath(du.getPath() + "/stdout");
            jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/echo");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "test", "1", "2", "3"});
            jd.setAttribute(JobDescription.OUTPUT, "stdout");
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { du.getURL() + " < stdout" });
            js = JobFactory.createJobService(url);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long start = System.currentTimeMillis();
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }

        long stop = System.currentTimeMillis();
        String result;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new java.io.FileInputStream("data/stdout")));
            result = reader.readLine();
            reader.close();
        } catch (Exception e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        return new AdaptorTestResultEntry(result.equals("test 1 2 3"),
                (stop - start), null);

    }

    private AdaptorTestResultEntry submitJobStderr(URL url, String dirURL) {
        JobService js;
        JobDescription jd;
        URL du;
        try {
            du = URLFactory.createURL(dirURL);
            du.setPath(du.getPath() + "/stderr");
            jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/ls");
            jd.setVectorAttribute(JobDescription.ARGUMENTS,
                    new String[] { "floep"});
            jd.setAttribute(JobDescription.ERROR, "stderr");
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { du.getURL() + " < stderr"});
            js = JobFactory.createJobService(url);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long start = System.currentTimeMillis();
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        } 

        long stop = System.currentTimeMillis();
        String result;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new java.io.FileInputStream("data/stderr")));
            result = reader.readLine();
            reader.close();
        } catch (Exception e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        return new AdaptorTestResultEntry(result != null
                && result.startsWith("/bin/ls:")
                && result.endsWith(": No such file or directory"),
                (stop - start), null);

    }

    private AdaptorTestResultEntry submitJobPreStage(URL url, String dirURL) {
        JobService js;
        JobDescription jd;
        URL du;
        try {  
            du = URLFactory.createURL(dirURL);
            du.setPath(du.getPath() + "/stdout");
            jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/ls");
            jd.setAttribute(JobDescription.OUTPUT, "stdout");
            jd.setVectorAttribute(JobDescription.ARGUMENTS, new String[] { "floep"});
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] {
                        "ftp://ftp.cs.vu.nl/pub/ceriel/LLgen.tar.gz > floep",
                        du.getURL() + " < stdout"});
            js = JobFactory.createJobService(url);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long start = System.currentTimeMillis();
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        } 
 
        long stop = System.currentTimeMillis();
        String result;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new java.io.FileInputStream("data/stdout")));
            result = reader.readLine();
            reader.close();
        } catch (Exception e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        return new AdaptorTestResultEntry(result != null, (stop - start), null);

    }

    private AdaptorTestResultEntry submitJobPostStage(URL url, String dirURL) {
        JobService js;
        JobDescription jd;
        URL du;
        try {  
            du = URLFactory.createURL(dirURL);
            du.setPath(du.getPath() + "/flap.txt");
            jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/touch");
            jd.setVectorAttribute(JobDescription.ARGUMENTS, new String[] { "flap.txt"});
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] {
                        du.getURL() + " < flap.txt"});
            js = JobFactory.createJobService(url);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }

        long start = System.currentTimeMillis();
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        } 
 
        long stop = System.currentTimeMillis();
        return new AdaptorTestResultEntry(
                new java.io.File("data/flap.txt").exists(), (stop - start), null);

    }

    private AdaptorTestResultEntry submitJobEnvironment(URL url, String dirURL) {
        JobService js;
        JobDescription jd;
        URL du;
        try { 
            du = URLFactory.createURL(dirURL);
            du.setPath(du.getPath() + "/stdout");
            jd = JobFactory.createJobDescription();
            String serverHost = url.getHost();
            jd.setVectorAttribute(JobDescription.CANDIDATEHOSTS,
                    new String[] { serverHost });
            jd.setAttribute(JobDescription.EXECUTABLE, "/usr/bin/env");
            jd.setAttribute(JobDescription.OUTPUT, "stdout");
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { du.getURL() + " < stdout"});
            jd.setVectorAttribute(JobDescription.ENVIRONMENT,
                    new String[] { "SAGA_TEST_KEY=blablabla"});
            js = JobFactory.createJobService(url);
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        long start = System.currentTimeMillis();
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        } 
 
        long stop = System.currentTimeMillis();
        boolean success = false;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new java.io.FileInputStream("data/stdout")));
            while (true) {
                String result = reader.readLine();
                if (result == null) {
                    break;
                }
                if (result.contains("SAGA_TEST_KEY")
                        && result.contains("blablabla")) {
                    success = true;
                }
            }
            reader.close();
        } catch (Throwable e) {
            return new AdaptorTestResultEntry(false, 0L, e);
        }
        return new AdaptorTestResultEntry(success, (stop - start), null);

    }
}
