
    package demo.namespace;

    import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

    // Run this for instance with: 
    // $SAGA_HOME/bin/run-saga-app test.namespace.FindTest \
//                  ftp://ftp.cs.vu.nl/pub/ceriel '*'
    // First argument is the directory, the other arguments are patterns for the find.

    public class FindTest {
        
        private static String getPassphrase(String m) {
            JPasswordField pwd = new JPasswordField();
            Object[] message = { m + "\nPlease enter your passphrase.",
                    pwd };
            JOptionPane.showMessageDialog(null, message, m,
                    JOptionPane.QUESTION_MESSAGE);
            return new String(pwd.getPassword());
        }

        public static void main(String[] args) {
            try {
                URL directory = URLFactory.createURL(args[0]);
                
                Session session = SessionFactory.createSession(true);
                
                String scheme = directory.getScheme();
                
                if ("ftp".equals(scheme)) {
                    // FTP context. Default is anonymous.
                    session.addContext(ContextFactory.createContext("ftp"));
                } else if ("gsiftp".equals(scheme)) {
                    // Gridftp context.
                    Context context = ContextFactory.createContext("gridftp");
                    context.setAttribute(Context.USERPASS, getPassphrase("Grid-Proxy-Init"));
                    session.addContext(context);
                }            

                // Print a listing of the directory indicated in the first argument.
                NSDirectory entry = NSFactory.createNSDirectory(directory);

                // The rest of the arguments are patterns to find the same
                // directory with.
                for (int i = 1; i < args.length; i++) {
                    System.out.println("Recursive find " + directory + ", matching with "
                            + args[i] + ":");
                    List<URL> list = entry.find(args[i], Flags.RECURSIVE.getValue());
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


