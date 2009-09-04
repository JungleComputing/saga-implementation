package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class MoveEntry extends EnvironmentCommand {

	public MoveEntry(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "<src> <tgt>";
    }

    public String getHelpExplanation() {
        return "move a file or directory to another file or into a directory";
    }

    public void execute(String[] args) {
        if (args.length != 3) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }
        
        Directory cwd = env.getCwd();
        
        try {
            URL src = URLFactory.createURL(args[1]);
            URL target = URLFactory.createURL(args[2]);

            if (cwd.isDir(src)) {
                cwd.move(src, target, Flags.RECURSIVE.getValue());
            } else {
                cwd.move(src, target);
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
