package demo.logicalfile;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.logicalfile.LogicalFile;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

public class LogicalFileCopy {
    public static void main(String[] args) {
        try {
            Session session = SessionFactory.createSession(true);
            
            Context ftpContext = ContextFactory.createContext("ftp");
            session.addContext(ftpContext);
            
            LogicalFile f = LogicalFileFactory.createLogicalFile(session,
                    new URL(args[0]), Flags.CREATE.or(Flags.READWRITE));
            f.addLocation(new URL("ftp://ftp.cs.vu.nl/pub/ceriel/LLgen.tar.gz"));
            f.replicate(new URL("file://localhost" + args[1]));
            f.close();
        } catch (Throwable t) {
            System.out.println("ouch..." + t);
            t.printStackTrace();
        }
    }
}
