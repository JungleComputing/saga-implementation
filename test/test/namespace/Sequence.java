package test.namespace;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;

class Sequence {

    /*
     * Usage of Sequence
     * 
     * touch dirname/filename1 (create non-existing dirs on the fly) write
     * message to filename1 test whether filename1 and filename2 exist filename1
     * should exist, filename2 shouldn't move filename1 to filename2 and test
     * again (filename1 doesn't exist, filename2 does) copy filename2 to
     * filename1 and test again (both files exist) list the dir (both files
     * should be displayed) filename2 removes itself list the dir (only
     * filename1 should be visible) filename1 gets removed by dir
     */
    
    private static String getPassphrase() {
        JPasswordField pwd = new JPasswordField();
        Object[] message = { "grid-proxy-init\nPlease enter your passphrase.",
                pwd };
        JOptionPane.showMessageDialog(null, message, "Grid-Proxy-Init",
                JOptionPane.QUESTION_MESSAGE);
        return new String(pwd.getPassword());
    }
   
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: run_saga_app test.namespace.Sequence filename1 filename2 dirname message");
            System.exit(1);
        }
        
        String filename = args[0];
        String newFilename = args[1];
        String dir = args[2];
        String message = args[3];
              
        try {
            URL fileUrl = new URL(filename);
            URL newFileUrl = new URL(newFilename);
            URL completeFileUrl = new URL(dir + "/" + filename);
            
            Session session = SessionFactory.createSession(true);
            String scheme = completeFileUrl.getScheme();
            if ("gsiftp".equals(scheme)) {
                // Gridftp context.
                Context context = ContextFactory.createContext("gridftp");
                context.setAttribute(Context.USERPASS, getPassphrase());
                session.addContext(context);
            }

            NSDirectory parentDir = NSFactory.createNSDirectory(session,
                    new URL(dir), Flags.CREATE.or(Flags.CREATEPARENTS));
            FileOutputStream stream = FileFactory.createFileOutputStream(session, completeFileUrl);
            stream.write(message.getBytes());
            stream.close();
            System.out.println("Written: '" + message + "' to "
                    + completeFileUrl);
            NSEntry file = NSFactory.createNSEntry(session, completeFileUrl);
            System.out.println("File getCWD(): " + file.getCWD());
            System.out
                    .println("before move: check for existence of both files");
            System.out.println(" " + filename + ": "
                    + parentDir.exists(fileUrl));
            System.out.println(" " + newFilename + ": "
                    + parentDir.exists(newFileUrl));
            
            // NewFile considered relative to cwd of file.
            file.move(newFileUrl, Flags.NONE.getValue());
            System.out.println("moved '" + filename + "' to '" + newFilename
                    + "'");
            // old file still exists?
            System.out.println("after move: check for existence of both files");
            System.out.println(" " + filename + ": "
                    + parentDir.exists(fileUrl));
            System.out.println(" " + newFilename + ": "
                    + parentDir.exists(newFileUrl));
            System.out.println("copied '" + newFilename + "' to '" + filename
                    + "'");
            
            // Both urls considered relative to parentDir.
            parentDir.copy(newFileUrl, fileUrl, Flags.NONE.getValue());
            System.out.println("after copy: check for existence of both files");
            System.out.println(" " + filename + ": "
                    + parentDir.exists(fileUrl));
            System.out.println(" " + newFilename + ": "
                    + parentDir.exists(newFileUrl));
            System.out.println("list of dir '" + dir + "'");
            List<URL> urls = parentDir.list("", Flags.NONE.getValue());
            URL[] urlsArray = urls.toArray(new URL[urls.size()]);
            for (URL url : urlsArray) {
                System.out.println(" " + url.toString());
            }
            file.remove(Flags.NONE.getValue());
            System.out.println("removed '" + newFilename + "'");
            System.out.println("list of dir '" + dir + "'");
            urls = parentDir.list("", Flags.NONE.getValue());
            urlsArray = urls.toArray(new URL[urls.size()]);
            for (URL url : urlsArray) {
                System.out.println(" " + url.toString());
            }
            System.out.println("removed '" + filename + "' from dir '" + dir
                    + "'");
            parentDir.remove(fileUrl, Flags.NONE.getValue());
        } catch (Throwable t) {
            System.out.println("exception: " + t);
            t.printStackTrace(System.out);
            Throwable e = t.getCause();
            if (e != null) {
                System.out.println("nested exception: " + e);
                e.printStackTrace(System.out);
            }
        }
    }
}
