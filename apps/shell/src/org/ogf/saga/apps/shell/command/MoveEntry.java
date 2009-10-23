package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.FlagsParser;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class MoveEntry extends EnvironmentCommand {

    private static final char FLAG_RECURSIVE = 'r';
    private static final char FLAG_WILDCARDS = 'w';
    private static final String ALL_FLAGS = "" + FLAG_RECURSIVE + FLAG_WILDCARDS;
    
    public MoveEntry(Environment env) {
        super(env);
    }

    public String getHelpArguments() {
        return "[" + FlagsParser.FLAG_PREFIX + ALL_FLAGS + "] <src> <tgt>";
    }

    public String getHelpExplanation() {
        return "move a file or directory, possibly into an existing directory";
    }

    public void execute(String[] args) {
        FlagsParser flagsParser = new FlagsParser(ALL_FLAGS);
        int srcIndex = flagsParser.parse(args, 1);
        
        if (args.length - srcIndex != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            System.err.println(" " + FlagsParser.FLAG_PREFIX + FLAG_RECURSIVE
                    + " moves directories recursively");
            System.err.println(" " + FlagsParser.FLAG_PREFIX + FLAG_WILDCARDS
                    + " interprets <src> as a wildcard pattern");
            return;
        }

        String source = args[srcIndex];
        String target = args[srcIndex + 1];
        boolean recursive = flagsParser.getBooleanValue(FLAG_RECURSIVE);
        boolean wildcards = flagsParser.getBooleanValue(FLAG_WILDCARDS);
        
        Directory cwd = env.getCwd();
        Flags flags = recursive ? Flags.RECURSIVE : Flags.NONE;
        
        try {
            URL targetUrl = URLFactory.createURL(target);

            if (wildcards) {
                cwd.move(source, targetUrl, flags.getValue());
            } else {
                URL sourceUrl = URLFactory.createURL(source);
                cwd.move(sourceUrl, targetUrl, flags.getValue());
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
