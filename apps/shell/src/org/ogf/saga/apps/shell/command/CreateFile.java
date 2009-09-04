package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class CreateFile extends EnvironmentCommand {

	public CreateFile(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "<url>";
    }

    public String getHelpExplanation() {
        return "create an empty file";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }
        
        String fileName = args[1];
    
        Directory cwd = env.getCwd();
        
        try {
            URL url = URLFactory.createURL(fileName);
            NSEntry e = cwd.open(url, Flags.CREATE.getValue());
            e.close();
        } catch (SagaException e) {
            Util.printSagaException(e); 
        }
    }
    
}
