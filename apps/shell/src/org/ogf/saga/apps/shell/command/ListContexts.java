package org.ogf.saga.apps.shell.command;

import java.util.Arrays;

import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

public class ListContexts implements Command {

    public String getHelpArguments() {
        return "";
    }

    public String getHelpExplanation() {
        return "show all security contexts";
    }

    public void execute(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: " + args[0]);
            return;
        }
        
        try {
            Session defaultSession = SessionFactory.createSession(true);
            
            Context[] contexts = defaultSession.listContexts();
            
            Context defaultContext = ContextFactory.createContext();
            
            for (int i = 0; i < contexts.length; i++) {
                System.out.print(" [" + i + "] ");
                System.out.print(contexts[i].getAttribute(Context.TYPE));
                
                for (String attr: contexts[i].listAttributes()) {
                    if (!attr.equals(Context.TYPE)) {
                        String value = null;
                        String defaultValue = null;
                        
                        if (contexts[i].isVectorAttribute(attr)) {
                            String[] values = contexts[i].getVectorAttribute(attr);
                            value = Arrays.toString(values);
                            defaultValue = Arrays.toString(new String[0]);
                        } else {
                            value = contexts[i].getAttribute(attr);
                            defaultValue = "";
                        }
                       
                        try {
                            defaultValue = defaultContext.getAttribute(attr);
                        } catch (DoesNotExistException ignored) {
                            // contexts[i] contains non-default attributes
                            // (like the 'xtreemos' context)
                        }
                        
                        if (!value.equals(defaultValue)) {
                            System.out.print(" " + attr + "=");
                            
                            if (attr.equals(Context.USERPASS)) {
                                System.out.print("<secret>");
                            } else {
                                System.out.print(value);
                            }
                        }
                    }
                }
                System.out.println();
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

}
