package benchmarks.job;

import java.util.Arrays;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.icenigrid.gridsam.client.common.ClientSideJobManager;
import org.icenigrid.gridsam.core.JobInstance;
import org.icenigrid.gridsam.core.JobStage;
import org.icenigrid.gridsam.core.JobState;
import org.icenigrid.schema.jsdl.posix.y2005.m11.ArgumentType;
import org.icenigrid.schema.jsdl.posix.y2005.m11.FileNameType;
import org.icenigrid.schema.jsdl.posix.y2005.m11.POSIXApplicationDocument;
import org.icenigrid.schema.jsdl.posix.y2005.m11.POSIXApplicationType;
import org.icenigrid.schema.jsdl.y2005.m11.ApplicationType;
import org.icenigrid.schema.jsdl.y2005.m11.JobDefinitionDocument;
import org.icenigrid.schema.jsdl.y2005.m11.JobDefinitionType;
import org.icenigrid.schema.jsdl.y2005.m11.JobDescriptionType;
import org.icenigrid.schema.jsdl.y2005.m11.JobIdentificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class GridsamJobBenchmark implements Benchmark {
    
    private Logger logger = LoggerFactory.getLogger(SagaJobBenchmark.class);
    
    private final JobDefinitionDocument jobDefinitionDocument;
    private final ClientSideJobManager jobManager;
    private JobInstance jobInstance;
    private int firedEventCount;
    private Object savedState;
 
    public GridsamJobBenchmark(String jsUrl, String exec, String[] args) throws Exception {
        jobDefinitionDocument = generateJSDL(exec, args);
        jobManager = new ClientSideJobManager(new String[] { "-s", jsUrl },
                ClientSideJobManager.getStandardOptions());
    }
    
    private JobDefinitionDocument generateJSDL(String exec, String[] args) {
        JobDefinitionDocument jd = JobDefinitionDocument.Factory.newInstance();
        JobDefinitionType jobDef = jd.addNewJobDefinition();
        JobDescriptionType jobDescr = jobDef.addNewJobDescription();
        JobIdentificationType jobid = jobDescr.addNewJobIdentification();
        jobid.addJobProject("gridsam");

        ApplicationType appl = jobDescr.addNewApplication();
        XmlCursor cursor = appl.newCursor();
        cursor.toEndToken();

        POSIXApplicationDocument posixDoc = POSIXApplicationDocument.Factory
                .newInstance();
        POSIXApplicationType posixAppl = posixDoc.addNewPOSIXApplication();
        FileNameType f = posixAppl.addNewExecutable();
        f.setStringValue(exec);
        for (String argument : args) {
            ArgumentType arg = posixAppl.addNewArgument();
            arg.setStringValue(argument);
        } 

        XmlCursor c = posixDoc.newCursor();
        c.toStartDoc();
        c.toNextToken();
        c.moveXml(cursor);
        
        return jd;
    }
    
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("usage: java " + GridsamJobBenchmark.class.getName()
                    + " <jobservice-url> <#runs> <command> <arg>*");
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
            test = new GridsamJobBenchmark(jsUrl, exec, arguments);
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
            logger.debug("Submitted job");
            PollingThread pollingThread;
            pollingThread = new PollingThread(this);
            pollingThread.setDaemon(true);
            logger.debug("starting poller thread");
            pollingThread.start();
            logger.debug("waiting for poller thread");
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
