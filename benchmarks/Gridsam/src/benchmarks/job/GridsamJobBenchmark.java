package benchmarks.job;

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
    
    private static Logger logger = LoggerFactory.getLogger(GridsamJobBenchmark.class);
    
    private final JobDefinitionDocument jobDefinitionDocument;
    private final ClientSideJobManager jobManager;
    private int firedEventCount;
    private Object savedState;
    private String jobID;
    private int commandRuns;
 
    public GridsamJobBenchmark(String jsUrl, int commandRuns, String exec, String[] args) throws Exception {
        jobDefinitionDocument = generateJSDL(exec, args);
        jobManager = new ClientSideJobManager(new String[] { "-s", jsUrl },
                ClientSideJobManager.getStandardOptions());
        this.commandRuns = commandRuns;
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
        if (args.length < 4) {
            System.out.println("usage: java " + GridsamJobBenchmark.class.getName()
                    + " <jobservice-url> <#runs> <#command runs> <command> <arg>*");
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
            test = new GridsamJobBenchmark(jsUrl, commandRuns, exec, arguments);
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

        for (int i = 0; i < commandRuns; i++) {
            try {
                firedEventCount = 0;
                savedState = null;
                JobInstance jobInstance = jobManager.submitJob(
                        jobDefinitionDocument, true);
                logger.debug("Submitted job, jobInstance = " + jobInstance);
                jobID = jobInstance.getID();
                PollingThread pollingThread;
                pollingThread = new PollingThread(this);
                pollingThread.setDaemon(true);
                jobManager.startJob(jobID);
                logger.debug("starting poller thread");
                pollingThread.start();
                logger.debug("waiting for poller thread");
                pollingThread.join();
            } catch(Throwable e) {
                throw new Error(e);
            }
        }
    }
    
    private boolean stateChange(JobStage stage) {
        JobState state = stage.getState();
        logger.debug("Got state " + state);
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
    
    public boolean poll() throws Exception {
        JobInstance jobInstance = jobManager.findJobInstance(jobID);
        List<?> stages = jobInstance.getJobStages();
        logger.debug("poll gave " + stages.size() + " events");
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

        public void run() {
            try {
                while (! parent.poll()) {
                    // update the manager state of the job
                     try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        // ignored
                    }
                }
            } catch(Throwable e) {
                GridsamJobBenchmark.logger.error("Got exception", e);
            }
        }
    }
}
