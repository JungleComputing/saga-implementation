package org.ogf.saga.apps.shell.command;

import java.util.List;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.url.URL;

public class FindEntries extends EnvironmentCommand {

    private static final String PARAM_RECURSIVE = "-r";
    
    public FindEntries(Environment env) {
        super(env);
    }

    public String getHelpArguments() {
        return "[" + PARAM_RECURSIVE + "] <pattern>";
    }

    public String getHelpExplanation() {
        return "lists entries with matching names in the current working directory";
    }

    public void execute(String[] args) {
        int flags = Flags.NONE.getValue();
        
        if (args.length < 2 || args.length > 3 ||
            (args.length == 3 && !PARAM_RECURSIVE.equals(args[1]))) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            System.err.println("available wildcard patterns:");
            System.err.println(" *      : matches any string");
            System.err.println(" ?      : matches a single character");
            System.err.println(" [abc]  : matches any of a set of characters");
            System.err.println(" [a-z]  : matches any of a range of characters");
            System.err.println(" [!abc] : matches none of a set of characters");
            System.err.println(" [!a-z] : matches none of a range of characters");
            System.err.println(" {a,bc} : matches any of a set of strings");
            return;
        }
        
        if (args.length == 3) {
            flags = Flags.RECURSIVE.getValue();
        }
        String pattern = args[args.length - 1];

        Directory cwd = env.getCwd();

        try {
            List<URL> matches = cwd.find(pattern, flags);
            Util.sortAlphabetically(matches);
            
            for (URL entry: matches) {
               System.out.println(entry); 
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
