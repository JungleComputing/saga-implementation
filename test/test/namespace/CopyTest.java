package test.namespace;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

class CopyTest {

    public static void main(String[] args) {
        try {
            Session session = SessionFactory.createSession(true);
            
            Context ftpContext = ContextFactory.createContext("ftp");
            session.addContext(ftpContext);
            // Possibly add other contexts ...
            
            NSDirectory entry = NSFactory.createNSDirectory(session, new URL("."),
                    Flags.NONE.getValue());
            entry.copy(new URL(args[0]), new URL(args[1]), Integer.parseInt(args[2]));
            System.out.println("copied!");
        } catch (Throwable t) {
            System.out.println("ouch..." + t);
            t.printStackTrace();
        }
    }
}
