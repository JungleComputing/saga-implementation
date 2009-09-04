package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class ChangeResourceManager extends EnvironmentCommand {

	public ChangeResourceManager(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "[url]";
    }

    public String getHelpExplanation() {
        return "change the current resource manager";
    }

    public void execute(String[] args) {
        String newRm = null;
        
        if (args.length < 2) {
            newRm = "local://localhost";
        } else if (args.length > 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        } else {
            newRm = args[1];
        }
        
        try {
            URL newRmUrl = URLFactory.createURL(newRm);
            env.setResourceManager(newRmUrl);
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
