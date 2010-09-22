package org.ogf.saga.impl.task;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaObjectBase;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.monitoring.MetricImpl;
import org.ogf.saga.job.Job;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.State;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.WaitMode;

public class TaskContainerImpl extends SagaObjectBase implements
        org.ogf.saga.task.TaskContainer {
    static Logger logger = LoggerFactory.getLogger(TaskContainerImpl.class);
    private HashMap<String, Task<?, ?>> tasks = new HashMap<String, Task<?, ?>>();
    private HashMap<String, Integer> callbackCookies = new HashMap<String, Integer>();

    private MetricImpl taskContainerMetric;

    private class ContainerCallback implements Callback {

        final TaskContainerImpl taskContainerImpl;

        ContainerCallback(TaskContainerImpl taskContainerImpl) {
            this.taskContainerImpl = taskContainerImpl;
        }

        // callback from each task in the container.
        // Notifies any state change, and notifies the container itself as well.
        public synchronized boolean cb(Monitorable mt,
                org.ogf.saga.monitoring.Metric metric, Context ctx)
                throws NotImplementedException, AuthorizationFailedException {
            Task<?, ?> t = (Task<?, ?>) mt;
            String id = t.getId();
            if (logger.isDebugEnabled()) {
                logger.debug("TaskContainer callback called, id = "
                        + id);
            }
            if (id != null) {
                try {
                    taskContainerMetric.setValue(id);
                } catch (Throwable e) {
                    // ignored
                }
                taskContainerMetric.internalFire();
            }
            synchronized (taskContainerImpl) {
                taskContainerImpl.notifyAll();
            }
            return true;
        }
    }

    ContainerCallback cb;

    TaskContainerImpl() {
        super((Session) null);
        cb = new ContainerCallback(this);
        try {
            taskContainerMetric = new MetricImpl(
                    this,
                    null,
                    TASKCONTAINER_STATE,
                    "fires on state changes of any task in the container, and has the value of that task's cookie",
                    "ReadOnly", "1", "Int", "");
        } catch (Throwable e) {
            // Should not happen.
            throw new SagaRuntimeException("Unexpected exception", e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        TaskContainerImpl clone = (TaskContainerImpl) super.clone();
        synchronized (clone) {
            clone.tasks = new HashMap<String, Task<?, ?>>(tasks);
        }
        return clone;
    }

    public synchronized void add(Task<?, ?> task)
            throws NotImplementedException, TimeoutException,
            AlreadyExistsException, NoSuccessException {
        String id = task.getId();
        if (tasks.containsKey(id)) {
            throw new AlreadyExistsException("Task " + id + " is already present");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Add task " + id);
        }

        tasks.put(id, task);

        try {
            if (task instanceof Job) {
                callbackCookies.put(id, task.addCallback(Job.JOB_STATE, cb));
            } else {
                callbackCookies.put(id, task.addCallback(Task.TASK_STATE, cb));
            }
        } catch (IncorrectStateException e) {
            // Ignore, the task is already in a final state.
        } catch (Throwable e) {
            // Should not happen.
            throw new SagaRuntimeException("Unexpected error", e);
        }
    }

    public void cancel() throws NotImplementedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        Task<?, ?>[] t = getTasks();
        if (t.length == 0) {
            throw new DoesNotExistException(
                    "cancel() called on empty task container");
        }
        for (Task<?, ?> task : t) {
            task.cancel();
        }
    }

    public void cancel(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        cancel();
    }

    public synchronized State[] getStates() throws NotImplementedException,
            TimeoutException, NoSuccessException {
        State[] retval = new State[tasks.size()];
        int k = 0;
        for (Task<?, ?> t : tasks.values()) {
            retval[k++] = t.getState();
        }
        return retval;
    }

    public synchronized Task<?, ?> getTask(String id)
            throws NotImplementedException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        Task<?, ?> task = tasks.get(id);
        if (task == null) {
            throw new DoesNotExistException("There is no task for id "
                    + id);
        }
        return task;
    }

    public synchronized Task<?, ?>[] getTasks() throws NotImplementedException,
            TimeoutException, NoSuccessException {
        return tasks.values().toArray(new Task[tasks.size()]);
    }

    public synchronized Task<?,?>[] listTasks() throws NotImplementedException,
            TimeoutException, NoSuccessException {
        return tasks.values().toArray(new Task[tasks.size()]);
    }

    public synchronized void remove(Task<?,?> task)
            throws NotImplementedException, DoesNotExistException,
            TimeoutException, NoSuccessException {
        String id = task.getId();
        if (logger.isDebugEnabled()) {
            logger.debug("TaskContainer.remove " + id);
        }
        task = tasks.remove(id);
        if (task == null) {
            throw new DoesNotExistException("Task " + id  + " is not in this TaskContainer");
        }
        Integer c = callbackCookies.remove(id);
        if (c != null) {
            try {
                task.removeCallback(Task.TASK_STATE, c);
            } catch (Throwable e) {
                // ignored
            }
        }
    }

    public void run() throws NotImplementedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        Task<?, ?>[] t = getTasks();
        if (t.length == 0) {
            throw new DoesNotExistException(
                    "run() called on empty task container");
        }
        for (Task<?, ?> task : t) {
            task.run();
        }
    }

    public synchronized int size() throws NotImplementedException,
            TimeoutException, NoSuccessException {
        return tasks.size();
    }

    public Task<?, ?> waitFor(WaitMode mode) throws NotImplementedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return waitFor(-1.0F, mode);
    }
    
    public Task<?, ?> waitFor(float timeout) throws NotImplementedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return waitFor(timeout, WaitMode.ALL);
    }
    
    public Task<?, ?> waitFor() throws NotImplementedException,
            IncorrectStateException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        return waitFor(WaitMode.ALL);
    }

    private Task<?, ?> getFinishedTask(WaitMode mode)
            throws DoesNotExistException, TimeoutException,
            NotImplementedException, NoSuccessException,
            IncorrectStateException {
        int running = 0;
        Task<?, ?>[] list = getTasks();
        if (logger.isDebugEnabled()) {
            logger.debug("getFinishedTask, #tasks = " + list.length);
        }
        if (list.length == 0) {
            throw new DoesNotExistException(
                    "waitTask called on empty TaskContainer");
        }
        for (Task<?, ?> t : list) {
            State s = t.getState();
            switch (s) {
            case RUNNING:
            case SUSPENDED:
                running++;
                continue;
            case CANCELED:
            case FAILED:
            case DONE:
                if (mode == WaitMode.ANY) {
                    remove(t);
                    return t;
                }
                break;
            case NEW:
                throw new IncorrectStateException(
                        "Wait called on task in NEW state");
            }
        }

        if (running == 0) {
            remove(list[0]);
            return list[0];
        }

        return null;
    }

    public synchronized Task<?, ?> waitFor(float timeoutInSeconds, WaitMode mode)
            throws NotImplementedException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        Task<?, ?> t = getFinishedTask(mode);
        if (t != null) {
            return t;
        }

        // The taskcontainer gets notified each time a task changes state.
        if (timeoutInSeconds < 0) {
            while (t == null) {
                try {
                    wait();
                } catch (Exception e) {
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
                } catch (Exception e) {
                    // ignored
                }
                t = getFinishedTask(mode);
                currentTime = System.currentTimeMillis();
            }
        }
        return t;
    }

    public int addCallback(String name, Callback cb)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException,
            IncorrectStateException {
        if (TASKCONTAINER_STATE.equals(name)) {
            return taskContainerMetric.addCallback(cb);
        }
        throw new DoesNotExistException("metric " + name + " does not exist");
    }

    public MetricImpl getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        if (TASKCONTAINER_STATE.equals(name)) {
            return taskContainerMetric;
        }
        throw new DoesNotExistException("metric " + name
                + " does not exist");
    }

    public String[] listMetrics() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException {
        return new String[] { TASKCONTAINER_STATE };
    }

    public void removeCallback(String name, int cookie)
            throws NotImplementedException, DoesNotExistException,
            BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException {
        if (TASKCONTAINER_STATE.equals(name)) {
            taskContainerMetric.removeCallback(cookie);
        } else {
            throw new DoesNotExistException("metric " + name
                    + " does not exist");
        }
    }
}
