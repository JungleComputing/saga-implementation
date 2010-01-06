package benchmarks.job;

import java.util.Arrays;

import benchmarks.Benchmark;
import benchmarks.BenchmarkRunner;

public class LocalJobBenchmark implements Benchmark {
    
    private final String[] command;

    public LocalJobBenchmark(String[] command) {
        this.command = command;
    }

    public void close() {
        // nothing
    }

    public void run() {
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        } catch (Exception e) {
            throw new Error(e);
        }
    }
       
    public static void main(String args[]) {
        if (args.length < 2) {
            System.out.println("usage: java " + LocalJobBenchmark.class.getName()
                    + " <#runs> <executable> [arg]*");
            return;
        }
        
        int runs = Integer.parseInt(args[0]);
        
        String[] command = Arrays.copyOfRange(args, 1, args.length);
    
        Benchmark test;
        test = new LocalJobBenchmark(command);
        
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
