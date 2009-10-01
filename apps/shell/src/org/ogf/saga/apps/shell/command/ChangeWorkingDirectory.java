package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeWorkingDirectory extends EnvironmentCommand {

    private Logger logger = LoggerFactory
            .getLogger(ChangeWorkingDirectory.class);

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
        Directory newCwd = null;

        try {
            URL newDirUrl = URLFactory.createURL(newDir);
            newCwd = cwd.openDirectory(newDirUrl);
        } catch (SagaException e) {
            Util.printSagaException(e);
            return;
        }

        if (newCwd != null) {
            try {
                cwd.close();
            } catch (SagaException e) {
                logger.debug("Error closing current working directory", e);
            } finally {
                env.setCwd(newCwd);
            }
        }
    }

}
