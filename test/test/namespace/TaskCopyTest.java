package test.namespace;

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
 * It creates two tasks, one to copy an ftp file to a local file, and one
 * to copy a http file to a local file. These tasks are stored in a
 * task container, which has a callback installed on it, which just prints
 * a message, demonstrating that it gets invoked.
 * The task container is run and waited for. Any exceptions thrown by the tasks
 * are printed.
 */
public class TaskCopyTest implements Callback {
    
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
    
    private static TaskContainer createTasks(Session session) throws Exception {
        NSDirectory dir = NSFactory.createNSDirectory(session, new URL("."));

        // Create two copy tasks and a task container 
        Task task1 = dir.copy(TaskMode.TASK,
                new URL("ftp://ftp.cs.vu.nl/pub/ceriel/LLgen.tar.gz"),
                new URL("LLgen.tar.gz"), Flags.OVERWRITE.getValue());
        Task task2 = dir.copy(TaskMode.TASK,
                new URL("http://www.cs.vu.nl/ibis/index.html"),
                new URL("index.html"), Flags.OVERWRITE.getValue());
        TaskContainer container = TaskFactory.createTaskContainer();
        
        // Install a callback detecting state changes in the tasks
        container.addCallback(TaskContainer.TASKCONTAINER_STATE,
                new TaskCopyTest());
        
        container.add(task2); 
        container.add(task1);
        return container;
    }
    
    public static void main(String[] args) throws Exception {
        Session session = null;
        try {
            session = SessionFactory.createSession(true);
            
            // Create an ftp context.
            Context ftpContext = ContextFactory.createContext("ftp");
            session.addContext(ftpContext);
            
            TaskContainer container = createTasks(session);
            
            container.run();
            while (container.size() != 0) {
                Task t = container.waitTasks(WaitMode.ANY);
                System.out.println("Task finished, state = " + t.getState());
                if (State.FAILED.equals(t.getState())) {
                    try {
                        t.rethrow();
                    } catch(Throwable e) {
                        System.err.println("Task threw exception: " + e);
                        e.printStackTrace(System.err);
                    }
                }
            }           
        } finally {
            // Explicit close is needed, unfortunately, for termination.
            // This should be fixed.
            session.close();
        }
    }
}
