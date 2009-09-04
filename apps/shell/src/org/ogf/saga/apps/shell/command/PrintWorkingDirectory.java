package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;

public class PrintWorkingDirectory extends EnvironmentCommand {

	public PrintWorkingDirectory(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "";
    }

    public String getHelpExplanation() {
        return "print the current working directory";
    }

    public void execute(String[] args) {
        Directory cwd = env.getCwd();
        
        try {
            System.out.println(cwd.getURL());
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
