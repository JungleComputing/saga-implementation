package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;

public class Exit extends EnvironmentCommand {

    public Exit(Environment env) {
        super(env);
    }

    public String getHelpArguments() {
        return "";
    }

    public String getHelpExplanation() {
        return "end the SAGA shell";
    }

    public void execute(String[] args) {
        env.terminate();
    }

}
