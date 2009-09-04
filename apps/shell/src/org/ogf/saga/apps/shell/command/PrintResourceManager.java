package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.url.URL;

public class PrintResourceManager extends EnvironmentCommand {

	public PrintResourceManager(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "";
    }

    public String getHelpExplanation() {
        return "print the current resource manager";
    }

    public void execute(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: " + args[0]);
            return;
        }
        
        URL u = env.getResourceManager();
        System.out.println(u);
    }

}
