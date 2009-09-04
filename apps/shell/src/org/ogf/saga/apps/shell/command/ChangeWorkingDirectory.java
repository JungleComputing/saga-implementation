package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class ChangeWorkingDirectory extends EnvironmentCommand {

	public ChangeWorkingDirectory(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "[url]";
    }

    public String getHelpExplanation() {
        return "change the current working directory";
    }

    public void execute(String[] args) {
        String newDir = null;

        if (args.length < 2) {
            newDir = "file://localhost" + System.getProperty("user.home");
        } else if (args.length > 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        } else {
            newDir = args[1];
        }

        Directory cwd = env.getCwd();
        
        try {
            URL newDirUrl = URLFactory.createURL(newDir);
            Directory newCwd = cwd.openDirectory(newDirUrl);
            env.setCwd(newCwd);
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
