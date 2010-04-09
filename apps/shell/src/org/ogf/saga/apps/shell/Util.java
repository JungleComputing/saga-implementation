package org.ogf.saga.apps.shell;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ogf.saga.attributes.Attributes;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static Logger logger = LoggerFactory.getLogger(Util.class);

    private static final URLComparator URL_COMP = new URLComparator();


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

    /**
     * Updates an attribute according to an assignment like string.
     * Receives an update string of the form key=value or key=value1,value2 and tries to set the
     * attribute 'key' to the given value (or vector of value in case of vector attributes).
     * 
     * @param updateStr
     *          a string with the described format. 
     * @param a
     *          the Attribute object to update.
     * 
     * @throws ParseException
     *          in case the updateStr is invalid.
     *          
     * @throws BadParameterException 
     * @throws IncorrectStateException 
     * @throws NoSuccessException 
     * @throws TimeoutException 
     * @throws DoesNotExistException 
     * @throws PermissionDeniedException 
     * @throws AuthorizationFailedException 
     * @throws AuthenticationFailedException 
     * @throws NotImplementedException
     *         thrown from the Attributes object. 
     */
    public static void updateAttribute(String updateStr, Attributes a)
    throws ParseException, NotImplementedException, AuthenticationFailedException,
           AuthorizationFailedException, PermissionDeniedException, DoesNotExistException,
           TimeoutException, NoSuccessException, IncorrectStateException, BadParameterException {
        String[] kv = updateStr.split("=");
        
        if (kv.length != 2) {
            throw new ParseException("Expected exactly one equals in '" + updateStr + "'", -1);
        }

        if (a.isVectorAttribute(kv[0])) {
            String[] values = kv[1].split(",");
            
            if (logger.isDebugEnabled()) {
                logger.debug("Vector " + kv[0] + "=" + 
                    Arrays.toString(values));
            }
            
            a.setVectorAttribute(kv[0], values);
        } else {
            a.setAttribute(kv[0], kv[1]);
        }
    }
    
    /**
     * Sorts a lists of URLs alphabetically.
     */
    public static void sortAlphabetically(List<URL> list) {
        Collections.sort(list, URL_COMP);
    }
    
    private static class URLComparator implements Comparator<URL> {

        public int compare(URL u1, URL u2) {
            String s1 = u1.toString();
            String s2 = u2.toString();

            return s1.compareTo(s2);
        }

    }
    
}
