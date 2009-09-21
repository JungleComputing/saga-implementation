package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class MakeDirectory extends EnvironmentCommand {

	public MakeDirectory(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "<url>";
    }

    public String getHelpExplanation() {
        return "create a new directory";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }
        
        String newDir = args[1];

        try {
            URL u = URLFactory.createURL(newDir);
            Directory cwd = env.getCwd();
            cwd.makeDir(u, Flags.EXCL.getValue());
        } catch (AlreadyExistsException e) {
            System.err.println("Directory already exists: " + newDir);
        } catch (DoesNotExistException e) {
            System.err.println("One or more parent directories do not exist");
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
