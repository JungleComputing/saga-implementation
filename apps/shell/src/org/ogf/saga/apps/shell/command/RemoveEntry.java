package org.ogf.saga.apps.shell.command;

import java.io.IOException;

import jline.ConsoleReader;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.FlagsParser;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class RemoveEntry extends EnvironmentCommand {

    private static final char FLAG_RECURSIVE = 'r';
    private static final char FLAG_WILDCARDS = 'w';
    private static final String ALL_FLAGS = "" + FLAG_RECURSIVE + FLAG_WILDCARDS;
    
    private ConsoleReader console;
    
    public RemoveEntry(Environment env, ConsoleReader console) {
        super(env);
        this.console = console;
    }

    public String getHelpArguments() {
        return "[" + FlagsParser.FLAG_PREFIX + ALL_FLAGS + "] <entry>";
    }

    public String getHelpExplanation() {
        return "remove a file or directory";
    }

    public void execute(String[] args) {
        FlagsParser flagsParser = new FlagsParser(ALL_FLAGS);
        int entryIndex = flagsParser.parse(args, 1);
        
        if (args.length - entryIndex != 1) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            System.err.println(" " + FlagsParser.FLAG_PREFIX + FLAG_RECURSIVE
                    + " removes directories recursively");
            System.err.println(" " + FlagsParser.FLAG_PREFIX + FLAG_WILDCARDS
                    + " interprets <entry> as a wildcard pattern");
            return;
        }

        String entry = args[entryIndex];
        boolean recursive = flagsParser.getBooleanValue(FLAG_RECURSIVE);
        boolean wildcards = flagsParser.getBooleanValue(FLAG_WILDCARDS);
        
        Directory cwd = env.getCwd();
        Flags flags = recursive ? Flags.RECURSIVE : Flags.NONE;

        // for safety, confirm using wildcards or the 'Recursive' flag
        if ((wildcards || recursive) && !confirm(entry, recursive)) {
            return;
        }

        try {
            if (wildcards) {
                cwd.remove(entry, flags.getValue());
            } else {
                URL entryUrl = URLFactory.createURL(entry);
                cwd.remove(entryUrl, flags.getValue());
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }
    
    private boolean confirm(String entry, boolean recursive) {
        String question = "Do you REALLY want to delete '" + entry + "'";
        if (recursive) {
            question += " recursively";
        }
        question += " [y/N]? ";
    
        boolean done = false;
        String answer = null;
        
        while (!done) {
            try {
                answer = console.readLine(question);
            } catch (IOException e) {
                return false;
            }
            
            if (answer == null) {
                done = true;
            } else if (!answer.isEmpty() && "yn".indexOf(answer.charAt(0)) < 0) {
                System.err.println("Please type 'y' or 'n'");
            } else {
                done = true;
            }
        }

        return "y".equalsIgnoreCase(answer);
    }

}
