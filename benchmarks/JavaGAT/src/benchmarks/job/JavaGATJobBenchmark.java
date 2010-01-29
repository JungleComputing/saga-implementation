package benchmarks.job;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

import java.net.URISyntaxException;
import java.util.Arrays;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
import org.gridlab.gat.monitoring.MetricEvent;
import org.gridlab.gat.monitoring.MetricListener;
import org.gridlab.gat.resources.Job;
import org.gridlab.gat.resources.JobDescription;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.SoftwareDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGATJobBenchmark implements Benchmark, MetricListener {
    
    private Logger logger = LoggerFactory.getLogger(JavaGATJobBenchmark.class);
    
    private ResourceBroker broker;
    private JobDescription job;

    private boolean jobFinished;
    private int commandRuns;
    
    public JavaGATJobBenchmark(String jsUrl, int commandRuns, String exec, String[] arguments)
            throws URISyntaxException, GATObjectCreationException {
        
        logger.info("Creating job service '" + jsUrl + "'");
        URI u = new URI(jsUrl);
        broker = GAT.createResourceBroker(u);
        SoftwareDescription sw = new SoftwareDescription();
        sw.setExecutable(exec);
        sw.setArguments(arguments);
        job = new JobDescription(sw);
        this.commandRuns = commandRuns;

        logger.info("Job to run: " + exec + " " + Arrays.toString(arguments) + "'");
    }

    public void close() {
        GAT.end();
    }

    public void run() {
        for (int i = 0; i < commandRuns; i++) {
            jobFinished = false;
            try {
                Job j = broker.submitJob(job, this, "job.status");
                synchronized(this) {
                    while (! jobFinished) {
                        try {
                            wait();
                        } catch(Throwable e) {
                            // ignored
                        }
                    }
                }
                if (j.getState() != Job.JobState.STOPPED) {
                    throw new Error("Failed job");
                }
            } catch(Error e) {
                throw e;
            } catch(Throwable e) {
                throw new Error(e);
            }
        }
    }

    
    public static void main(String args[]) {
        if (args.length < 4) {
            System.out.println("usage: java " + JavaGATJobBenchmark.class.getName()
                    + " <resourcebroker-url> <#runs> <#commandruns> <executable> [arg]*");
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
            test = new JavaGATJobBenchmark(jsUrl, commandRuns, exec, arguments);
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

    public void processMetricEvent(MetricEvent val) {
        if (val.getValue().equals(Job.JobState.STOPPED)
                || val.getValue().equals(Job.JobState.SUBMISSION_ERROR)) {
            synchronized (this) {
                jobFinished = true;
                notifyAll();
            }
        }        
    }
    
}
