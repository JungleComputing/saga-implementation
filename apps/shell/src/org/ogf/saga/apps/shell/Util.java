package org.ogf.saga.apps.shell;

import java.io.PrintStream;
import java.util.Arrays;

import org.ogf.saga.attributes.Attributes;
import org.ogf.saga.error.SagaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * Prints the details of a SAGA exception on System.err. It iterates over
     * all causes (one per adaptor tried), and prints each cause.
     * 
     * @param e
     *            the SAGA exception to print
     */
    public static void printSagaException(SagaException e) {
        int count = 1;

        for (Exception cause : e) {
            System.err.print("Adaptor " + count + ": ");
            System.err.println(cause);

            if (logger.isDebugEnabled()) {
                cause.printStackTrace(System.err);
            }

            count++;
        }
    }

    /**
     * Print all key names that can be used for the given attributes object.
     * 
     * @param a
     *            the attributes object
     * @param out
     *            the stream to print the key names on
     * 
     * @throws SagaException
     */
    public static void printPossibleKeys(Attributes a, PrintStream out)
            throws SagaException {
        String[] keys = a.listAttributes();
        Arrays.sort(keys);

        if (keys.length > 0) {
            out.print("Possible keys: ");
            String concat = "";
            for (String key : keys) {
                out.print(concat);
                out.print(key);
                concat = ", ";
            }
            out.println();
        }
    }

}
