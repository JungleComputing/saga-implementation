package org.ogf.saga.apps.shell.command;

import java.io.IOException;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.namespace.NSEntry;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class PrintFile extends EnvironmentCommand {

    private static final int READ_SIZE = 2048; // bytes 

    public PrintFile(Environment env) {
        super(env);
    }

    public String getHelpArguments() {
        return "<url>";
    }

    public String getHelpExplanation() {
        return "show the contents of a file";
    }

    public void execute(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }

        FileInputStream in = null;

        try {
            URL u = URLFactory.createURL(args[1]);

            Directory cwd = env.getCwd();
            NSEntry entry = cwd.open(u);
            in = FileFactory.createFileInputStream(entry.getURL());
            
            byte[] buf = new byte[READ_SIZE];
            
            int bytesRead = 0; 
            do {
                bytesRead = in.read(buf);
                
                if (bytesRead > 0) {
                    String s = new String(buf, 0, bytesRead);
                    System.out.print(s);
                }
            } while (bytesRead >= 0);
        } catch (SagaException e) {
            Util.printSagaException(e);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }

    }

}
