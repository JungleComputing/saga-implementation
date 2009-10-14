package org.ogf.saga.apps.shell.command;

import java.util.List;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.url.URL;

public class ListDirectory extends EnvironmentCommand {

    public ListDirectory(Environment env) {
        super(env);
    }

    public String getHelpArguments() {
        return "[-l]";
    }

    public String getHelpExplanation() {
        return "list all entries in the current working directory";
    }

    public void execute(String[] args) {
        if (args.length > 2 || (args.length == 2 && !args[1].equals("-l"))) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }

        boolean longFormat = args.length == 2 && args[1].equals("-l");

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
