package org.ogf.saga.apps.shell.command;

import java.util.List;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.FlagsParser;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.url.URL;

public class FindEntries extends EnvironmentCommand {

    private static final char FLAG_RECURSIVE = 'r';
    private static final String ALL_FLAGS = "" + FLAG_RECURSIVE;
    
    public FindEntries(Environment env) {
        super(env);
    }

    public String getHelpArguments() {
        return "[" + FlagsParser.FLAG_PREFIX + ALL_FLAGS + "] <pattern>";
    }

    public String getHelpExplanation() {
        return "lists entries with matching names in the current working directory";
    }

    public void execute(String[] args) {
        FlagsParser flagsParser = new FlagsParser(ALL_FLAGS);
        int patternIndex = flagsParser.parse(args, 1);
        
        if (args.length - patternIndex != 1) {
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
        
        int flags = Flags.NONE.getValue();
        if (flagsParser.getBooleanValue(FLAG_RECURSIVE)) {
            flags = Flags.RECURSIVE.getValue();
        }
        
        String pattern = args[patternIndex];

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
