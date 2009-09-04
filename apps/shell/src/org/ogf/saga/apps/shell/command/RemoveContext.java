package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

public class RemoveContext implements Command {

    public String getHelpArguments() {
        return "<#context>";
    }

    public String getHelpExplanation() {
        return "remove a security context";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        } 
        
        int num = -1;
        try {
            num = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Illegal context number: " + args[1]);
            return;
        }
        
        try {
            Session defaultSession = SessionFactory.createSession(true);
            Context[] contexts = defaultSession.listContexts();
            
            if (num < 0 || num >= contexts.length) {
                System.err.println("Context " + num + " does not exist");
                return;
            }
            
            defaultSession.removeContext(contexts[num]);
            
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
