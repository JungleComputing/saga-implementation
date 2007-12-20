package org.ogf.saga.impl.task;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.ogf.saga.ObjectType;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.State;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.WaitMode;

public class TaskContainer extends SagaObjectBase implements
        org.ogf.saga.task.TaskContainer {
    static Logger logger = Logger.getLogger(TaskContainer.class);
    private HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();
    private HashMap<Task, Integer> reverseMap = new HashMap<Task, Integer>();
    private HashMap<Task, Integer> callbackCookies = new HashMap<Task, Integer>();
    private int taskCount = 0;
    private Metric taskContainerMetric;
    
    private class ContainerCallback implements Callback {
        
        final TaskContainer taskContainer;

        ContainerCallback(TaskContainer taskContainer) {
            this.taskContainer = taskContainer;
        }
        
        // callback from each task in the container.
        // Notifies any state change, and notifies the container itself as well.
        public synchronized boolean cb(Monitorable mt,
                org.ogf.saga.monitoring.Metric metric, Context ctx)
                throws NotImplemented, AuthorizationFailed {
            Task t = (Task) mt;
            Integer cookie = reverseMap.get(t);
            if (logger.isDebugEnabled()) {
                logger.debug("TaskContainer callback called, cookie = " + cookie);
            }
            if (cookie != null) {
                try {
                    taskContainerMetric.setValue(cookie.toString());
                } catch(Throwable e) {
                    // ignored
                }
                taskContainerMetric.internalFire();
            }
            synchronized(taskContainer) {
                taskContainer.notifyAll();
            }
            return true;
        }       
    };
    
    ContainerCallback cb;
    
    TaskContainer() {
        super((Session) null);
        cb = new ContainerCallback(this);
        try {
            taskContainerMetric = new Metric(
                    this, null, TASKCONTAINER_STATE,
                    "fires on state changes of any task in the container, and has the value of that task's cookie",
                    "ReadOnly", "1", "Int", "" );
        } catch (Throwable e) {
            // Should not happen.
            throw new SagaError("Unexpected exception", e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        TaskContainer clone = (TaskContainer) super.clone();
        clone.tasks = new HashMap<Integer, Task>(tasks);
        clone.reverseMap = new HashMap<Task, Integer>(reverseMap);
        clone.callbackCookies = new HashMap<Task, Integer>(callbackCookies);
        return clone;
    }
    
    @Override
    public ObjectType getType() {
        return ObjectType.TASKCONTAINER;
    }

    public synchronized int add(Task task) throws NotImplemented, Timeout, NoSuccess {
        Integer v = reverseMap.get(task);
        if (v != null) {
           return v.intValue();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Add task " + taskCount);
        }
        Integer cookie = taskCount++;
        tasks.put(cookie, task);
        reverseMap.put(task, cookie);
        try {
            callbackCookies.put(task, task.addCallback(Task.TASK_STATE, cb));
        } catch(IncorrectState e) {
            // Ignore, the task is already in a final state.           
        } catch(Throwable e) {
            // Should not happen.
            throw new SagaError("Unexpected error", e);
        }
        
        return cookie; 
    }

    public void cancel() throws NotImplemented, IncorrectState, DoesNotExist,
            Timeout, NoSuccess {
        Task[] t = getTasks();
        if (t.length == 0) {
            throw new DoesNotExist("cancel() called on empty task container");
        }
        for (Task task : t) {
            task.cancel();
        }
    }

    public void cancel(float timeoutInSeconds) throws NotImplemented,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        cancel();
    }

    public synchronized State[] getStates() throws NotImplemented, Timeout, NoSuccess {
        State[] retval = new State[tasks.size()];
        int k = 0;
        for (Task t : tasks.values()) {
            retval[k++] = t.getState();
        }
        return retval; 
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> Task<T> getTask(int cookie) throws NotImplemented, DoesNotExist,
            Timeout, NoSuccess {
        Task<T> task = tasks.get(cookie);
        if (task == null) {
            throw new DoesNotExist("There is no task for cookie " + cookie);
        }
        return task;
    }

    public synchronized Task[] getTasks() throws NotImplemented, Timeout, NoSuccess {
        return tasks.values().toArray(new Task[tasks.size()]);
    }

    public synchronized int[] listTasks() throws NotImplemented, Timeout, NoSuccess {
        int[] retval = new int[reverseMap.size()];
        int k = 0;
        for (int i : reverseMap.values()) {
            retval[k++] = i;
        }
        return retval;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> Task<T> remove(int cookie) throws NotImplemented, DoesNotExist,
            Timeout, NoSuccess {
        if (logger.isDebugEnabled()) {
            logger.debug("TaskContainer.remove " + cookie);
        }
        Task<T> task = tasks.remove(cookie);
        if (task == null) {
            throw new DoesNotExist("There is no task for cookie " + cookie);
        }
        reverseMap.remove(task);
        Integer c = callbackCookies.remove(task);
        if (c != null) {
            try {
                task.removeCallback(Task.TASK_STATE, c);
            } catch(Throwable e) {
                // ignored
            }
        }
        return task;
    }

    public void run() throws NotImplemented, IncorrectState, DoesNotExist,
            Timeout, NoSuccess {
        Task[] t = getTasks();
        if (t.length == 0) {
            throw new DoesNotExist("run() called on empty task container");
        }
        for (Task task : t) {
            task.run();
        }
    }

    public synchronized int size() throws NotImplemented, Timeout, NoSuccess {
        return tasks.size();
    }

    public Task waitFor(WaitMode mode) throws NotImplemented,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return waitFor(-1.0F, mode);
    }
    
    public Task waitTasks() throws NotImplemented,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return waitFor(-1.0F, WaitMode.ALL);
    }

    public Task waitTasks(float timeoutInSeconds) throws NotImplemented,
            IncorrectState, DoesNotExist, Timeout, NoSuccess {
        return waitFor(timeoutInSeconds, WaitMode.ALL);       
    }
    
    private Task getFinishedTask(WaitMode mode) throws DoesNotExist, Timeout,
            NotImplemented, NoSuccess, IncorrectState {
        int running = 0;
        Task[] list = getTasks();
        if (logger.isDebugEnabled()) {
            logger.debug("getFinishedTask, #tasks = " + list.length);
        }
        if (list.length == 0) {
            throw new DoesNotExist("waitTask called on empty TaskContainer");
        }
        for (Task t : list) {
            State s = t.getState();
            switch(s) {
            case RUNNING:
                running++;
                continue;
            case CANCELED:
            case FAILED:
            case DONE:
                if (mode == WaitMode.ANY) {
                    return remove(reverseMap.get(t));
                }
                break;
            case NEW:
                throw new IncorrectState("Wait called on task in NEW state");
            }
        }
        
        if (running == 0) {
            return remove(reverseMap.get(list[0]));
        }
        
        return null;     
    }
    
    public synchronized Task waitFor(float timeoutInSeconds, WaitMode mode)
            throws NotImplemented, IncorrectState, DoesNotExist, Timeout,
            NoSuccess {
        Task t = getFinishedTask(mode);
        if (t != null) {
            return t;
        }
      
        // The taskcontainer gets notified each time a task changes state.
        if (timeoutInSeconds < 0) {
            while (t == null) {
                try {
                    wait();
                } catch(Exception e) {
                    // ignored
                }
                t = getFinishedTask(mode);
            }
        } else {
            long interval = (long) (timeoutInSeconds * 1000.0);
            long currentTime = System.currentTimeMillis();
            long endTime = currentTime + interval;
            while (t == null && currentTime < endTime) {
                interval = endTime - currentTime;
                try {
                    wait(interval);
                } catch(Exception e) {
                    // ignored
                }
                t = getFinishedTask(mode);
                currentTime = System.currentTimeMillis();
            }
        }
        return t;
    }

    public int addCallback(String name, Callback cb) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess, IncorrectState {
        if (TASKCONTAINER_STATE.equals(name)) {
            return taskContainerMetric.addCallback(cb);
        }
        throw new DoesNotExist("metric " + name + " does not exist");
    }

    public Metric getMetric(String name) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        if (TASKCONTAINER_STATE.equals(name)) {
            return taskContainerMetric;
        } else {
            throw new DoesNotExist("metric " + name + " does not exist");
        }
    }

    public String[] listMetrics() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return new String[] {TASKCONTAINER_STATE};
    }

    public void removeCallback(String name, int cookie) throws NotImplemented,
            DoesNotExist, BadParameter, Timeout, NoSuccess,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied {
        if (TASKCONTAINER_STATE.equals(name)) {
            taskContainerMetric.removeCallback(cookie);
        } else {
            throw new DoesNotExist("metric " + name + " does not exist");
        }
    }
}
