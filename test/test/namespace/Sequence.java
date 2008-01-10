package test.namespace;

import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
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
            URL urlFile = new URL(filename);
            URL urlNewFile = new URL(newFilename);
            
            Session session = SessionFactory.createSession();
            File file = FileFactory.createFile(session, new URL(dir + "/" + filename),
                    Flags.READWRITE.or(Flags.CREATE.or(Flags.CREATEPARENTS)));
            System.out.println("File size (" + file.getURL().toString() + "): "
                    + file.getSize() + " bytes (before write)");
            Buffer buffer = BufferFactory.createBuffer();
            buffer.setData(message.getBytes());
            file.write(buffer, message.length());
            System.out.println("Written: '" + message + "' to "
                    + file.getURL().toString());
            System.out.println("File size (" + file.getURL().toString() + "): "
                    + file.getSize() + " bytes (after write)");
            System.out.println("File getCWD(): " + file.getCWD());
            NSDirectory parentDir = NSFactory.createNSDirectory(session,
                    file.getCWD(),
                    Flags.CREATE.or(Flags.CREATEPARENTS));
            System.out
                    .println("before move: check for existence of both files");
            System.out.println(" " + filename + ": "
                    + parentDir.exists(urlFile));
            System.out.println(" " + newFilename + ": "
                    + parentDir.exists(urlNewFile));
            
            // NewFile considered relative to cwd of file.
            file.move(urlNewFile, Flags.NONE.getValue());
            System.out.println("moved '" + filename + "' to '" + newFilename
                    + "'");
            // old file still exists?
            System.out.println("after move: check for existence of both files");
            System.out.println(" " + filename + ": "
                    + parentDir.exists(urlFile));
            System.out.println(" " + newFilename + ": "
                    + parentDir.exists(urlNewFile));
            System.out.println("copied '" + newFilename + "' to '" + filename
                    + "'");
            
            // Both urls considered relative to parentDir.
            parentDir.copy(urlNewFile, urlFile, Flags.NONE.getValue());
            System.out.println("after copy: check for existence of both files");
            System.out.println(" " + filename + ": "
                    + parentDir.exists(urlFile));
            System.out.println(" " + newFilename + ": "
                    + parentDir.exists(urlNewFile));
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
            parentDir.remove(urlFile, Flags.NONE.getValue());
        } catch (Throwable t) {
            System.out.println("exception: " + t);
        }
    }
}
