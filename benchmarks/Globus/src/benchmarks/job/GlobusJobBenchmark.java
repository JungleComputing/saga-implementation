package benchmarks.job;

import java.util.Arrays;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

import org.globus.gram.Gram;
import org.globus.gram.GramJob;
import org.globus.gram.GramJobListener;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;

public class GlobusJobBenchmark implements Benchmark, GramJobListener {
    private final String jobDefinition;
    private final String jsUrl;
    private final GSSCredential credential;
    private boolean jobDone = false;
 
    public GlobusJobBenchmark(String jsUrl, String exec, String[] args) throws Exception {
        this.jsUrl = stripScheme(jsUrl);

        jobDefinition = generateJSDL(exec, args);
        // Get the user credential
        ExtendedGSSManager manager = (ExtendedGSSManager) ExtendedGSSManager
                    .getInstance();

        // try to get default user proxy certificate from file in /tmp
        credential = manager
                    .createCredential(GSSCredential.INITIATE_AND_ACCEPT);
    }
    
    private String stripScheme(String s) {
        if (s.contains("://")) {
            s = s.replaceFirst(".*://", "");
        }
        return s;
    }
    
    private String generateJSDL(String exec, String[] args) {
        
        StringBuffer buf = new StringBuffer();

        buf.append("& (executable = ");
        buf.append(exec);
        buf.append(')');
        if (args.length > 0) {
            buf.append(" (arguments = ");
            for (String arg : args) {
                buf.append(" \"" + arg + "\" ");
            }
            buf.append(")");
        }

        return buf.toString();
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
            test = new GlobusJobBenchmark(jsUrl, exec, arguments);
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
        try {
            Gram.deactivateAllCallbackHandlers();
        } catch(Throwable e) {
            // ignored
        }
    }

    public void run() {
        GramJob j = new GramJob(credential, jobDefinition);
        j.addListener(this);
        
        synchronized (this) {
            jobDone = false;
        }

        try {
            j.request(jsUrl);
        } catch (Throwable e) {
            throw new Error(e);
        }
        synchronized (this) {
            while (!jobDone) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored
                }
            }
        }
    }
    
    public void statusChanged(GramJob newJob) {
        int globusState = newJob.getStatus();
        if ((globusState == org.globus.gram.internal.GRAMConstants.STATUS_DONE)
                || (globusState == org.globus.gram.internal.GRAMConstants.STATUS_FAILED)) {
            synchronized (this) {
                jobDone  = true;
                notifyAll();
            }
        }
    }    
}
