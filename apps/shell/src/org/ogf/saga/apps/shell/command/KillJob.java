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
        return "<job id>";
    }

    public String getHelpExplanation() {
        return "kill a background job";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }

        String id = args[1];

        TaskContainer bg = env.getBackgroundJobs();

        try {
            Task<?, ?> t = bg.getTask(id);
            t.cancel();
        } catch (DoesNotExistException e) {
            System.err.println("Unknown job id: " + id);
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
