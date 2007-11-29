package test.file;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

public class FileSize {

    /*
     * Security context test for globus (avoid grid-proxy-init)
     * 
     * PREREQUISITES: 1. have a local private key (e.g.
     * $HOME/.globus/userkey.pem) 2. have a local certificate (e.g.
     * $HOME/.globus/usercert.pem) 3. have a local $HOME/.globus/certificates
     * with the certificates
     * 
     * In case of problems: 1. bad certificate --> make sure your local time
     * settings match the compute server's
     * 
     * 2. job submission error (the job manager failed to open stdout, gram
     * error 73) --> see http://www.afs.enea.it/scio/faq_errors.html#stdout
     */

    private static String getPassphrase() {
        JPasswordField pwd = new JPasswordField();
        Object[] message = { "grid-proxy-init\nPlease enter your passphrase.",
                pwd };
        JOptionPane.showMessageDialog(null, message, "Grid-Proxy-Init",
                JOptionPane.QUESTION_MESSAGE);
        return new String(pwd.getPassword());
    }
    
    public static void main(String[] args) throws Exception {

        // Create session and add some contexts
        Session session = SessionFactory.createSession();
        URL url = new URL(args[0]);
        String scheme = url.getScheme();
        if ("ftp".equals(scheme)) {
            // FTP context. Default is anonymous.
            session.addContext(ContextFactory.createContext("ftp"));
        } else if ("gsiftp".equals(scheme)) {
            // Gridftp context.
            Context context = ContextFactory.createContext("gridftp");
            context.setAttribute(Context.USERPASS, getPassphrase());
            session.addContext(context);
        }
        
        // Create file object, determine size.
        File file = FileFactory.createFile(session, new URL(args[0]));
        System.out.println("URL " + args[0] + " has " + file.getSize() + " bytes");
        file.close();
        
        session.close();
    }
}
