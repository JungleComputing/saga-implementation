package org.ogf.saga.apps.shell.command;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
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

        File f = null;

        try {
            URL u = URLFactory.createURL(args[1]);

            Directory cwd = env.getCwd();
            f = cwd.openFile(u);

            Buffer buf = BufferFactory.createBuffer(READ_SIZE);

            boolean done = false;
            while (!done) {
                int len = f.read(buf);
                if (len > 0) {
                    String s = new String(buf.getData(), 0, len);
                    System.out.print(s);
                } else {
                    done = true;
                }
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (SagaException e) {
                    Util.printSagaException(e);
                }
            }
        }
    }

}
