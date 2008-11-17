package demo.namespace;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URLFactory;

class CopyTest {

    public static void main(String[] args) {
        try {
            Session session = SessionFactory.createSession(true);
            
            Context ftpContext = ContextFactory.createContext("ftp");
            session.addContext(ftpContext);
            // Possibly add other contexts ...
            
            NSDirectory entry = NSFactory.createNSDirectory(session, URLFactory.createURL("/tmp"),
                    Flags.NONE.getValue());
            // entry  = (NSDirectory) entry.clone();
            entry.copy(URLFactory.createURL(args[0]), URLFactory.createURL(args[1]), Integer.parseInt(args[2]));
            System.out.println("copied!");
        } catch (Throwable t) {
            System.out.println("ouch..." + t);
            t.printStackTrace();
        }
    }
}
