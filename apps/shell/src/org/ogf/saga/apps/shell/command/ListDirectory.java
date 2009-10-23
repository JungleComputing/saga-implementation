package org.ogf.saga.apps.shell.command;

import java.util.List;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.FlagsParser;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.url.URL;

public class ListDirectory extends EnvironmentCommand {

    private static final char FLAG_LONG = 'l';
    private static final String ALL_FLAGS = "" + FLAG_LONG;
    
    public ListDirectory(Environment env) {
        super(env);
    }

    public String getHelpArguments() {
        return "[" + FlagsParser.FLAG_PREFIX + ALL_FLAGS + "]";
    }

    public String getHelpExplanation() {
        return "list all entries in the current working directory";
    }

    public void execute(String[] args) {
        FlagsParser flagsParser = new FlagsParser(ALL_FLAGS);
        int index = flagsParser.parse(args, 1);
        
        if (index != args.length) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }

        boolean longFormat = flagsParser.getBooleanValue(FLAG_LONG);

        Directory cwd = env.getCwd();

        try {
            List<URL> entryList = cwd.list();
            Util.sortAlphabetically(entryList);

            for (URL entry : entryList) {
                if (longFormat) {
                    if (cwd.isEntry(entry)) {
                        long size = cwd.getSize(entry);
                        System.out.printf("- %8d ", size);
                    } else {
                        System.out.print("d          ");
                    }
                }

                System.out.println(entry);
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
