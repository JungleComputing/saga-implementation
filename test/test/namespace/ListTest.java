package test.namespace;

import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

// Run this for instance with: 
// $SAGA_HOME/bin/run_saga_app test.namespace.ListTest \
//              ftp://ftp.cs.vu.nl/pub/ceriel '*' '*.gz' '{L,M}*' '*tar*' 
// First argument is the directory, the other arguments are patterns to list.

public class ListTest {

    public static void main(String[] args) {
        try {
            Session session = SessionFactory.createSession(true);
            
            Context ftpContext = ContextFactory.createContext("ftp");
            session.addContext(ftpContext);
            // Possibly add other contexts ...
            
            NSDirectory entry = NSFactory.createNSDirectory(session, new URL(args[0]),
                    Flags.NONE.getValue());
            List<URL> list = entry.list();
            System.out.println("Contents of " + args[0] + ":");
            for (URL u : list) {
                System.out.println("    " + u);
            }
            for (int i = 1; i < args.length; i++) {
                System.out.println("Matching with " + args[i] + ":");
                list = entry.list(args[i]);
                for (URL u : list) {
                    System.out.println("    " + u);
                }
            }
        } catch (Throwable t) {
            System.out.println("ouch..." + t);
            t.printStackTrace();
        }
    }
}