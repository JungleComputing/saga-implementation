package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;

public abstract class EnvironmentCommand implements Command {

	protected Environment env;
	
	public EnvironmentCommand(Environment env) {
		this.env = env;
	}
	
}
