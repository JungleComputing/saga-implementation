package benchmarks;

import java.util.Arrays;

public class BenchmarkRunner implements Runnable {

    private Benchmark test;
    private int runs;
    
    public BenchmarkRunner(Benchmark test, int runs) {
        this.test = test;
        this.runs = runs;
    }

    @Override
    public void run() {
        double totalTime = 0;
        double minTime = Double.MAX_VALUE;
        double maxTime = 0;
        double[] times = new double[runs];
        
        try {

            for (int run = 1; run <= runs; run++) {
                System.out.printf("Run %2d: ", run);

                long time = System.currentTimeMillis();
                test.run();
                time = System.currentTimeMillis() - time;

                double sec = time / 1000.0;

                System.out.printf("%2.2f sec\n", sec);

                if (sec < minTime) minTime = sec;
                if (sec > maxTime) maxTime = sec;
                totalTime += sec;
                times[run - 1] = sec;
                
                // wait one second between runs to let the system 'recover'
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    // ignore
                }
            }

            System.out.println();
            System.out.println("Results:");

            System.out.printf("- min:    %2.2f sec\n", minTime);
            System.out.printf("- max:    %2.2f sec\n", maxTime);

            double medianTime = 0;
            Arrays.sort(times);
            if (runs % 2 == 0) {
                int leftMiddle = (int)Math.floor((runs / 2) - 1);
                int rightMiddle = (int)Math.ceil(runs / 2);
                medianTime = (times[leftMiddle] + times[rightMiddle]) / 2.0;
            } else {
                medianTime = times[runs / 2];
            }
            System.out.printf("- median: %2.2f sec\n", medianTime);

            double averageTime = totalTime / runs;
            System.out.printf("- avg:    %2.2f sec\n", averageTime);
            System.out.println();
        } finally {
            test.close();
        }
    }
    
}
