package benchmarks.job;

import java.util.Arrays;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class LocalJobBenchmark implements Benchmark {
    
    private final String[] command;
    private int commandRuns;

    public LocalJobBenchmark(int commandRuns, String[] command) {
        this.command = command;
        this.commandRuns = commandRuns;
    }

    public void close() {
        // nothing
    }

    public void run() {
        for (int i = 0; i < commandRuns; i++) {
            try {
                Process p = Runtime.getRuntime().exec(command);
                p.waitFor();
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
       
    public static void main(String args[]) {
        if (args.length < 3) {
            System.out.println("usage: java " + LocalJobBenchmark.class.getName()
                    + " <#runs> <#commandRuns> <executable> [arg]*");
            return;
        }
        
        int runs = Integer.parseInt(args[0]);
        int commandRuns = Integer.parseInt(args[1]);
        
        String[] command = Arrays.copyOfRange(args, 2, args.length);
    
        Benchmark test;
        test = new LocalJobBenchmark(commandRuns, command);
        
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
    

}
