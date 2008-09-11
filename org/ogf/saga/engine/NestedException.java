package org.ogf.saga.engine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.ogf.saga.error.SagaException;

/**
 * This class defines an exception that can have multiple causes. The causes can
 * again be nested exceptions. This exception is used by the Saga engine. If a
 * file.copy method is invoked, for instance, the Saga engine will try all
 * loaded file adaptors until one succeeds. If none of the adaptors can copy
 * the file, a NestedException is used, containing the exceptions thrown by each
 * separate adaptor. The methods getMessage and printStrackTrace will reflect
 * this hierarchy.
 */
class NestedException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    ArrayList<SagaException> exceptions = new ArrayList<SagaException>();
    ArrayList<String> adaptors = new ArrayList<String>();

    /**
     * Constructs a NestedException.
     */
    public NestedException() {
        super();
    }

    /**
     * Constructs a NestedException which includes the information
     * that the specified adaptor threw the specified exception.
     */
    public NestedException(String adaptor, SagaException t) {
        super();
        add(adaptor, t);
    }

    /**
     * Adds an exception to this NestedException, which is caused by the given
     * adaptor
     * 
     * @param adaptor
     *                the adaptor that caused the exception
     * @param t
     *                the exception that is caused by the adaptor
     */
    public void add(String adaptor, SagaException t) {
        exceptions.add(t);
        adaptors.add(adaptor);
    }

    /**
     * Gets the String representation of the NestedException.
     * @return the String representation of the exception
     */
    public String toString() {

        StringBuffer res = new StringBuffer("");

        for (int i = 0; i < exceptions.size(); i++) {
            res.append("[" + adaptors.get(i) + "] ");
            res.append(exceptions.get(i).toString());
            if (exceptions.size() > 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }

    public String getMessage() {
        StringBuffer res = new StringBuffer("");

        for (int i = 0; i < exceptions.size(); i++) {
            res.append("[" + adaptors.get(i) + "] ");
            String s = exceptions.get(i).getMessage();
            if (s == null) {
                s = exceptions.get(i).toString();
            }
            res.append(s);
            if (exceptions.size() > 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }

    public void printStackTrace(PrintStream s) {
        s.println(stackTrace());
    }
    
    public void printStackTrace(PrintWriter s) {
        s.println(stackTrace());
    }

    private String stackTrace() {
        StringBuffer res = new StringBuffer("");

        for (int i = 0; i < exceptions.size(); i++) {
            res.append("[" + adaptors.get(i) + "] ");
            StringWriter writer = new StringWriter();
            exceptions.get(i).printStackTrace(new PrintWriter(writer, true));
            res.append(writer.toString());
            res.append("\n");
        }
        return res.toString();
    }
}
