package test.namespace;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.ogf.saga.URL;
import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.task.State;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskContainer;
import org.ogf.saga.task.TaskFactory;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.task.WaitMode;

/**
 * A small test for TaskContainers (and copy()).
 * It creates a temporary directory, and then copies the files
 * indicated by the arguments given to it, each copy action being
 * a separate task. These tasks are stored in a
 * task container, which has a callback installed on it, which just prints
 * a message, demonstrating that it gets invoked.
 * The task container is run and waited for. Any exceptions thrown by the tasks
 * are printed.
 */
public class TaskCopyTest implements Callback {

    private static String getPassphrase() {
        JPasswordField pwd = new JPasswordField();
        Object[] message = { "grid-proxy-init\nPlease enter your passphrase.",
                pwd };
        JOptionPane.showMessageDialog(null, message, "Grid-Proxy-Init",
                JOptionPane.QUESTION_MESSAGE);
        return new String(pwd.getPassword());
    }
    
    public boolean cb(Monitorable m, Metric metric, Context ctxt) {
        try {
            String value = metric.getAttribute(Metric.VALUE);
            TaskContainer container = (TaskContainer) m;
            try {
                Task t = container.getTask(Integer.parseInt(value));
                System.out.println("Metric " + metric.getAttribute(Metric.NAME)
                        + " got triggered, task " + value + " now has state "
                        + t.getState());
            } catch(DoesNotExist e) {
                System.out.println("Metric " + metric.getAttribute(Metric.NAME)
                        + " got triggered, task " + value
                        + " does not exist anymore?");
            }
        } catch(Throwable e) {
            System.err.println("error" + e);
            e.printStackTrace(System.err);
        }
        return true;
    }
    
    private static TaskContainer createTasks(Session session, URL[] urls) throws Exception {
        NSDirectory dir = NSFactory.createNSDirectory(session, new URL("tmp"), Flags.CREATE.getValue());
        
        // Create task container with callbacks.
        TaskContainer container = TaskFactory.createTaskContainer();
        container.addCallback(TaskContainer.TASKCONTAINER_STATE,
                new TaskCopyTest());
        
        // Create copy tasks, add them to container.
        for (URL url : urls) {
            String path = url.getPath();
            path = path.substring(path.lastIndexOf('/') + 1);
            
            Task task = dir.copy(TaskMode.TASK, url, new URL(path), Flags.OVERWRITE.getValue());
            container.add(task);
        }

        return container;
    }
    
    public static void main(String[] args) throws Exception {
        Session session = SessionFactory.createSession(true);

        URL[] urls = new URL[args.length];

        for (int i = 0; i < args.length; i++) {
            urls[i] = new URL(args[i]);
        }

        // Create an ftp context.
        Context ftpContext = ContextFactory.createContext("ftp");
        session.addContext(ftpContext);

        for (URL url : urls) {
            String scheme = url.getScheme();
            if ("gsiftp".equals(scheme)) {
                Context context = ContextFactory.createContext("gridftp");
                context.setAttribute(Context.USERPASS, getPassphrase());
                session.addContext(context);
                break;
            }
        }

        TaskContainer container = createTasks(session, urls);
        container.run();

        while (container.size() != 0) {
            Task t = container.waitFor(WaitMode.ANY);
            System.out.println("Task finished, state = " + t.getState());
            if (State.FAILED.equals(t.getState())) {
                try {
                    t.rethrow();
                } catch (Throwable e) {
                    System.err.println("Task threw exception: " + e);
                    e.printStackTrace(System.err);
                }
            }
        }           
    }
}
