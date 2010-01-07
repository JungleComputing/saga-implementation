package benchmarks.job;

import java.io.File;
import java.util.List;

import org.icenigrid.gridsam.client.common.ClientSideJobManager;
import org.icenigrid.gridsam.core.JobInstance;
import org.icenigrid.gridsam.core.JobStage;
import org.icenigrid.gridsam.core.JobState;
import org.icenigrid.schema.jsdl.y2005.m11.JobDefinitionDocument;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class GridsamJobBenchmark implements Benchmark {
    
    private final JobDefinitionDocument jobDefinitionDocument;
    private final ClientSideJobManager jobManager;
    private JobInstance jobInstance;
    private int firedEventCount;
    private Object savedState;
 
    public GridsamJobBenchmark(String jsUrl, File exec) throws Exception {
        jobDefinitionDocument = JobDefinitionDocument.Factory.parse(exec);
        jobManager = new ClientSideJobManager(new String[] { "-s", jsUrl },
                ClientSideJobManager.getStandardOptions());
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("usage: java " + GridsamJobBenchmark.class.getName()
                    + " <jobservice-url> <#runs> <JSDL file>");
            return;
        }
        
        String jsUrl = args[0];
        int runs = Integer.parseInt(args[1]);
        File exec = new File(args[2]);
        
        Benchmark test;
        try {
            test = new GridsamJobBenchmark(jsUrl, exec);
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }
        
        BenchmarkRunner runner = new BenchmarkRunner(test, runs);
        try {
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

    public void close() {
        // nothing for now.
    }

    public void run() {
        firedEventCount = 0;
        savedState = null;

        try {
            jobInstance = jobManager.submitJob(
                jobDefinitionDocument, true);
            PollingThread pollingThread;
            pollingThread = new PollingThread(this);
            pollingThread.setDaemon(true);
            pollingThread.join();
        } catch(Throwable e) {
            throw new Error(e);
        }
    }
    
    private boolean stateChange(JobStage stage) {
        JobState state = stage.getState();
        if (state.equals(savedState)) {
            return false;
        }
        savedState = state;
        if (state.equals(JobState.TERMINATED)
                || state.equals(JobState.DONE) || state.equals(JobState.FAILED)) {
            return true;
        }
        return false;
    }
    
    public boolean poll() {
        List<?> stages = jobInstance.getJobStages();
        if (stages.size() > firedEventCount) {
            for (int i = firedEventCount; i < stages.size(); i++) {
                if (stateChange((JobStage) stages.get(i))) {
                    return true;
                }
            }

            // finally we set that we have fired every event
            firedEventCount = stages.size();
        }
        return false;
    }
    
    private static class PollingThread extends Thread {

        private GridsamJobBenchmark parent;

        public PollingThread(GridsamJobBenchmark parent) {
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            while (! parent.poll()) {
                // update the manager state of the job
                 try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // ignored
                }
            }
        }
    }
}
