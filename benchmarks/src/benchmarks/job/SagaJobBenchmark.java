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
    private int commandRuns;
        
    public SagaJobBenchmark(String jsUrl, int commandRuns, String exec, String[] args) 
    throws SagaException 
    {
        logger.info("Creating job service '" + jsUrl + "'");
        URL u = URLFactory.createURL(jsUrl);
        js = JobFactory.createJobService(u);
        
        jd = JobFactory.createJobDescription();
        jd.setAttribute(JobDescription.EXECUTABLE, exec);
        jd.setVectorAttribute(JobDescription.ARGUMENTS, args);
        logger.info("Job to run: " + exec + " " + Arrays.toString(args) + "'");
        this.commandRuns = commandRuns;
    }
    
    public void run() {
        for (int i = 0; i < commandRuns; i++) {
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
        if (args.length < 4) {
            System.out.println("usage: java " + SagaJobBenchmark.class.getName()
                    + " <jobservice-url> <#runs> <#commandruns> <executable> [arg]*");
            return;
        }
        
        String jsUrl = args[0];
        int runs = Integer.parseInt(args[1]);
        int commandRuns = Integer.parseInt(args[2]);
        String exec = args[3];
        
        String[] arguments = null;
        if (args.length > 4) {
            arguments = new String[args.length - 4];
            for (int i = 4; i < args.length; i++) {
                arguments[i - 4] = args[i];
            }
        }
    
        Benchmark test;
        try {
            test = new SagaJobBenchmark(jsUrl, commandRuns, exec, arguments);
        } catch (SagaException e) {
            Util.printSagaException(e);
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        runner.run();
    }
    
}
