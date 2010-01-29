package benchmarks.job;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;
import benchmarks.Connecter;
import benchmarks.HostKeyVerifier;
import benchmarks.ScheduledExecutor;
import benchmarks.StreamForwarder;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;

public class SshJobBenchmark implements Benchmark {
    
    private static Logger logger = LoggerFactory.getLogger(SshJobBenchmark.class);

    private final String command;
    private final int commandRuns;
    private final Connection connection;
        
    public SshJobBenchmark(String host, int commandRuns, String exec, String[] args) {
        String s = "exec " + protectAgainstShellMetas(exec);
        if (args != null) {
            for (String arg : args) {
                s += " " + protectAgainstShellMetas(arg);
            }
        }
        command = s;

        HostKeyVerifier verifier = new HostKeyVerifier(false, true, true);

        try {
            connection = Connecter.getConnection(host, 22, verifier);
        } catch (Exception e) {
            throw new Error("Unable to connect!", e);
        }
        this.commandRuns = commandRuns;
    }
    
    
    private static String protectAgainstShellMetas(String s) {
        char[] chars = s.toCharArray();
        StringBuffer b = new StringBuffer();
        b.append('\'');
        for (char c : chars) {
            if (c == '\'') {
                b.append('\'');
                b.append('\\');
                b.append('\'');
            }
            b.append(c);
        }
        b.append('\'');
        return b.toString();
    }

    public void close() {
        logger.info("Cleaning up");
        connection.close();
    }


    public void run() {
        for (int i = 0; i < commandRuns; i++) {
            Session session = null;
            try {  
                session = connection.openSession();
                new StreamForwarder(session.getStderr(), null);
                new StreamForwarder(session.getStdout(), null);
                session.execCommand(command);
                session.waitForCondition(ChannelCondition.EXIT_STATUS, 0);
            } catch(IOException e) {
                throw new Error(e);
            } finally {
                if (session != null) {
                    session.close();
                }
            }            
        }
    }
    
    public static void main(String args[]) {
        if (args.length < 4) {
            System.out.println("usage: java " + SshJobBenchmark.class.getName()
                    + " <jobservice-host> <#runs> <#commandruns> <executable> [arg]*");
            return;
        }
        
        String jsUrl = args[0];
        int runs = Integer.parseInt(args[1]);
        int commandRuns = Integer.parseInt(args[2]);
        String exec = args[3];
        
        String[] arguments = null;
        if (args.length > 4) {
            arguments = Arrays.copyOfRange(args, 4, args.length);
        }
    
        try {
            Benchmark test = new SshJobBenchmark(jsUrl, commandRuns, exec, arguments);
            BenchmarkRunner runner = new BenchmarkRunner(test, runs);
            runner.run();
        } finally {
            ScheduledExecutor.end();
        }
    }
}
