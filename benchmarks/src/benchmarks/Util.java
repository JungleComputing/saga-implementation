package benchmarks;

import org.ogf.saga.error.SagaException;

public class Util {

    public static void printSagaException(SagaException e) {
        int i = 1;
        for (SagaException nested : e) {
            System.err.print("Adaptor " + (i++) + ": ");
            nested.printStackTrace(System.err);
        }
    }
    
}
