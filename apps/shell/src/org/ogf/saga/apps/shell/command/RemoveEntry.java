package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class RemoveEntry extends EnvironmentCommand {

	public RemoveEntry(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "<entry>";
    }

    public String getHelpExplanation() {
        return "remove a file or an empty directory";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }
        
        String dir = args[1];

        try {
            URL u = URLFactory.createURL(dir);
            Directory cwd = env.getCwd();
            
            if (cwd.isDir(u)) {
                // for safety, do not remove non-empty directories
                Directory d = cwd.openDirectory(u);
                
                if (d.getNumEntries() > 0) {
                    System.err.println("Cannot remove directory '" + args[1] + 
                            "': it is not empty");
                } else {
                    cwd.remove(u, Flags.RECURSIVE.getValue());
                }
            } else {
                cwd.remove(u);
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
