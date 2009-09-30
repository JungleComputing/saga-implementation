package org.ogf.saga.apps.shell.command;

import java.io.IOException;

import jline.ConsoleReader;

import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

public class AddContext implements Command {

    private static final String ASK_VALUE = "ask";
    private static final char ASK_MASK = '*';

    private ConsoleReader console;

    public AddContext(ConsoleReader console) {
        this.console = console;
    }

    public String getHelpArguments() {
        return "<type> [key=val]*";
    }

    public String getHelpExplanation() {
        return "add a security context of a certain type with the given attributes";
    }

    public void execute(String[] args) {
        if (args.length < 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());

            try {
                Context c = ContextFactory.createContext();
                Util.printPossibleKeys(c, System.err);
            } catch (SagaException e) {
                Util.printSagaException(e);
            }

            System.err.println("Use the value '" + ASK_VALUE
                    + "' to enter sensitive information interactively (e.g. '"
                    + Context.USERPASS + "=" + ASK_VALUE + "')");

            return;
        }

        String type = args[1];
        Context c = null;

        try {
            c = ContextFactory.createContext(type);
        } catch (SagaException e) {
            Util.printSagaException(e);
            return;
        }

        for (int i = 2; i < args.length; i++) {
            String[] kv = args[i].split("=");

            if (kv.length != 2) {
                System.err.println("Illegal key-value pair: '" + args[i] + "'");
                System.err.println("Expected 'key=value', e.g. 'type=ssh'");
                return;
            }

            if (ASK_VALUE.equals(kv[1])) {
                // read a line from stdin using masked chars (useful for 
                // entering passwords and other sensitive information)
                try {
                    kv[1] = console.readLine("Value of '" + kv[0] + "': ",
                            ASK_MASK);
                } catch (IOException e) {
                    System.err.println(e);
                    return;
                }
            }

            try {
                c.setAttribute(kv[0], kv[1]);
            } catch (SagaException e) {
                Util.printSagaException(e);
                return;
            }
        }

        try {
            Session def = SessionFactory.createSession(true);
            def.addContext(c);
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
