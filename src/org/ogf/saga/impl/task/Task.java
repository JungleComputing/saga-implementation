package org.ogf.saga.impl.task;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ogf.saga.ObjectType;
import org.ogf.saga.error.AlreadyExists;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.IncorrectURL;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.task.State;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.session.Session;

// The SAGA specifications warn against using threads.
// However, there is nothing against a default implementation that uses them.

public class Task<E> extends org.ogf.saga.impl.SagaObjectBase
        implements org.ogf.saga.task.Task<E>, Callable<E> {

    private static Logger logger = Logger.getLogger(Task.class);
    
    private State state = State.NEW;
    private final Object object;
    private Throwable exception = null;
    private Metric metric;
    private E result = null;
    private Method method = null;
    private Object[] parameters = null;
    private Future<E> future = null;
    
    private static ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            10L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    
    public Task(Object object, Session session, TaskMode mode, String methodName,
            Class[] parameterTypes, Object... parameters) {
        
        super(session);
                
        // lookup method of the task.
        try {
            method = object.getClass().getMethod(methodName, parameterTypes);
        } catch(Throwable e) {
            logger.error("Could not find method " + methodName, e);
            throw new SagaError("Internal error", e);
        }
                     
        // Create the task metric.
        try {
            metric = new Metric(this, session, TASK_STATE,
                    "fires on task change, and has the literal value of the task state enum",
                    "ReadOnly", "1", "Enum", "New");
        } catch(Throwable e) {
            // Should not happen.
            logger.error("Could not create metric", e);
            throw new SagaError("Unexpected exception", e);
        }
        this.object = object;
        this.parameters = parameters;
        
        if (logger.isDebugEnabled()) {
            logger.debug("Created task for method " + methodName);
        }
        switch(mode) {
        case ASYNC:
            internalRun();
            break;
        case SYNC:
            internalRun();
            internalWaitTask(-1.0F);
            break;
        case TASK:
            break;
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        Task clone = (Task) super.clone();
        return clone;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#cancel()
     */
    public synchronized void cancel() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        if (state == State.NEW) {
            throw new IncorrectState("cancel() called on task in state New");
        }
        cancel(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#cancel(float)
     */
    public void cancel(float timeoutInSeconds) throws NotImplemented,
            IncorrectState, Timeout, NoSuccess {
        cancel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#getObject()
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject() throws NotImplemented, Timeout,
            NoSuccess {
        return (T) object;
    }

    /*
     * (non-Javadoc)
     * 
     * see org.ogf.saga.task.Task#getResult()
     */
    public E getResult() throws NotImplemented, IncorrectState, Timeout,
           NoSuccess {
        if (state == State.NEW || state == State.CANCELED || state == State.FAILED) {
            throw new IncorrectState("getResult called in state " + state);
        }
        waitTask();
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#getState()
     */
    public State getState() throws NotImplemented, Timeout, NoSuccess {
        return state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#rethrow()
     */
    public synchronized void rethrow() throws NotImplemented, IncorrectURL,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            BadParameter, IncorrectState, AlreadyExists, DoesNotExist, Timeout,
            NoSuccess {
        if (state == State.FAILED) {
            // otherwise we should do nothing

            if (exception instanceof NotImplemented) {
                throw (NotImplemented) exception;
            }
            if (exception instanceof IncorrectURL) {
                throw (IncorrectURL) exception;
            }
            if (exception instanceof AuthenticationFailed) {
                throw (AuthenticationFailed) exception;
            }
            if (exception instanceof AuthorizationFailed) {
                throw (AuthorizationFailed) exception;
            }
            if (exception instanceof PermissionDenied) {
                throw (PermissionDenied) exception;
            }
            if (exception instanceof BadParameter) {
                throw (BadParameter) exception;
            }
            if (exception instanceof IncorrectState) {
                throw (IncorrectState) exception;
            }
            if (exception instanceof AlreadyExists) {
                throw (AlreadyExists) exception;
            }
            if (exception instanceof DoesNotExist) {
                throw (DoesNotExist) exception;
            }
            if (exception instanceof Timeout) {
                throw (Timeout) exception;
            }
            if (exception instanceof NoSuccess) {
                throw (NoSuccess) exception;
            }
            logger.error("Got unexpected exception in task", exception);
            //We should not get here.
            throw new SagaError("Got unknown exception", exception);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#run()
     */
    public void run() throws NotImplemented, IncorrectState, Timeout, NoSuccess {
        if (state != State.NEW) {
            throw new IncorrectState("run() called in wrong state");
        }
        internalRun();
    }

    /**
     * Internal version, no exceptions from this one, we know that the state is
     * correct.
     */
    private void internalRun() {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting task for method " + method.getName());
        }
        setState(State.RUNNING);
        synchronized(executor) {
            future = executor.submit(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#waitTask()
     */
    public void waitTask() throws NotImplemented, IncorrectState, Timeout,
            NoSuccess {
        waitTask(-1.0F);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#waitTask(float)
     */
    public synchronized boolean waitTask(float timeoutInSeconds) throws NotImplemented,
            IncorrectState, Timeout, NoSuccess {
        if (state == State.NEW) {
            throw new IncorrectState("waitTask called on new task");
        }
        return internalWaitTask(timeoutInSeconds);
    }
    
    /**
     * Internal version, we know that the state is correct.
     * @param timeoutInSeconds the timeout.
     * @return <code>true</code> if the task is finished.
     */
    private synchronized boolean internalWaitTask(float timeoutInSeconds) {
        
        if (logger.isDebugEnabled()) {
            logger.debug("Waiting for task " + method.getName());
        }
        
        switch(state) {
        case NEW:
            throw new Error("Internal error");
        case DONE:
        case CANCELED:
        case FAILED:
            if (logger.isDebugEnabled()) {
                logger.debug("task already finished");
            }
            return true;
        case SUSPENDED:
        case RUNNING:
            if (timeoutInSeconds < 0) {
                while (state == State.SUSPENDED || state == State.RUNNING) {
                    try {
                        wait();
                    } catch(Exception e) {
                        // ignored
                    }
                }
            } else {
                long interval = (long) (timeoutInSeconds * 1000.0);
                long currentTime = System.currentTimeMillis();
                long endTime = currentTime + interval;
                while ((state == State.SUSPENDED || state == State.RUNNING)
                        && currentTime < endTime) {
                    interval = endTime - currentTime;
                    try {
                        wait(interval);
                    } catch(Exception e) {
                        // ignored
                    }
                    currentTime = System.currentTimeMillis();
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("After waiting: state = " + state);
            }
        }
 
        return state != State.SUSPENDED && state != State.RUNNING;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.SagaObject#getType()
     */
    public ObjectType getType() {
        return ObjectType.TASK;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.monitoring.Monitorable#addCallback(java.lang.String,
     *      org.ogf.saga.monitoring.Callback)
     */
    public int addCallback(String name, Callback cb) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess, IncorrectState {
        if (TASK_STATE.equals(name)) {
            return metric.addCallback(cb);
        }
        
        throw new DoesNotExist("metric " + name + " does not exist");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ogf.saga.monitoring.Monitorable#getMetric(java.lang.String)
     */
    public Metric getMetric(String name) throws NotImplemented,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied,
            DoesNotExist, Timeout, NoSuccess {
        if (TASK_STATE.equals(name)) {
            return metric;
        }
        throw new DoesNotExist("metric " + name + " does not exist");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.monitoring.Monitorable#listMetrics()
     */
    public String[] listMetrics() throws NotImplemented, AuthenticationFailed,
            AuthorizationFailed, PermissionDenied, Timeout, NoSuccess {
        return new String[] {TASK_STATE} ;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.monitoring.Monitorable#removeCallback(java.lang.String,
     *      int)
     */
    public void removeCallback(String name, int cookie) throws NotImplemented,
            DoesNotExist, BadParameter, Timeout, NoSuccess,
            AuthenticationFailed, AuthorizationFailed, PermissionDenied {
        if (TASK_STATE.equals(name)) {
            metric.removeCallback(cookie);
        } else {
            throw new DoesNotExist("metric " + name + " does not exist");
        }
    }

    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (logger.isDebugEnabled()) {
            logger.debug("Cancelling task " + method.getName());
        }
        return future.cancel(mayInterruptIfRunning);
    }

    public E get() throws InterruptedException, ExecutionException {
        return future.get();
    }
    
    public E get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public boolean isCancelled() {
        if (future == null) {
            return false;
        }
        return future.isCancelled();
    }

    public boolean isDone() {
        if (future == null) {
            return false;
        }
        return future.isDone();
    }

    private synchronized void setState(State value) {
        state = value;
        notifyAll();
        try {
            if (value == State.DONE || value == State.FAILED
                    || value == State.CANCELED) {
                metric.setMode("Final");
            }
            metric.setValue(value.toString());
            metric.internalFire();
        } catch(Throwable e) {
            throw new SagaError("Internal error", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public E call() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking method " + method.getName());
        }
        try {
            result = (E) method.invoke(object, parameters);
        } catch(InvocationTargetException e1) {
            exception = e1.getCause();
            if (logger.isDebugEnabled()) {
                logger.debug("Invocation gave exception: ", exception);
            }            
            setState(State.FAILED);
            return null;
        } catch(Throwable e2) {
            logger.warn("Invoke failed:", e2);
            setState(State.FAILED);
            exception = e2;
            return null;
        }
        if (future.isCancelled()) {
            setState(State.CANCELED);
        } else {
            setState(State.DONE);
        }
        return result;
    }
}
