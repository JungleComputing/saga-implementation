package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskContainer;

public class KillJob extends EnvironmentCommand {

	public KillJob(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "<#job>";
    }

    public String getHelpExplanation() {
        return "kill a background job";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        } 
        
        int cookie = -1;
        try {
            cookie = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Illegal job number: " + args[1]);
            return;
        }
        
        TaskContainer bg = env.getBackgroundJobs();

        try {
            Task<?, ?> t = bg.getTask(cookie);
            t.cancel();
        } catch (DoesNotExistException e) {
            System.err.println("Unknown job number: " + cookie);
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
