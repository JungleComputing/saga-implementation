package benchmarks.job;

import java.util.Arrays;

import org.ogf.saga.error.SagaException;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.task.State;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;
import benchmarks.Util;

public class SagaJobBenchmark implements Benchmark {

    private Logger logger = LoggerFactory.getLogger(SagaJobBenchmark.class);
    
    private JobService js;
    private JobDescription jd;
        
    public SagaJobBenchmark(String jsUrl, String exec, String[] args) 
    throws SagaException 
    {
        logger.info("Creating job service '" + jsUrl + "'");
        URL u = URLFactory.createURL(jsUrl);
        js = JobFactory.createJobService(u);
        
        jd = JobFactory.createJobDescription();
        jd.setAttribute(JobDescription.EXECUTABLE, exec);
        jd.setVectorAttribute(JobDescription.ARGUMENTS, args);
        logger.info("Job to run: " + exec + " " + Arrays.toString(args) + "'");
    }
    
    public void run() {
        try {
            Job job = js.createJob(jd);
            job.run();
            job.waitFor();
            if (job.getState().equals(State.FAILED)) {
                throw new Error("Job failed");
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
            throw new Error("");
        }
    }

    public void close() {
        try {
            logger.info("Cleaning up");
            Session defaultSession = SessionFactory.createSession(true);
            defaultSession.close();
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }
    
    public static void main(String args[]) {
        if (args.length < 3) {
            System.out.println("usage: java " + SagaJobBenchmark.class.getName()
                    + " <jobservice-url> <#runs> <executable> [arg]*");
            return;
        }
        
        String jsUrl = args[0];
        int runs = Integer.parseInt(args[1]);
        String exec = args[2];
        
        String[] arguments = null;
        if (args.length > 3) {
            arguments = Arrays.copyOfRange(args, 3, args.length);
        }
    
        Benchmark test;
        try {
            test = new SagaJobBenchmark(jsUrl, exec, arguments);
        } catch (SagaException e) {
            Util.printSagaException(e);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
    
}
