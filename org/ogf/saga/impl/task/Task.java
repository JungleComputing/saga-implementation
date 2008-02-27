package org.ogf.saga.impl.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.impl.SagaRuntimeException;
import org.ogf.saga.impl.monitoring.Metric;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.session.Session;
import org.ogf.saga.task.State;
import org.ogf.saga.task.TaskMode;

// The SAGA specifications warn against using threads.
// However, there is nothing against a default implementation that uses them.

public class Task<E> extends org.ogf.saga.impl.SagaObjectBase
        implements org.ogf.saga.task.Task<E>, Callable<E> {

    private static Logger logger = Logger.getLogger(Task.class);
    
    protected State state = State.NEW;
    private final Object object;
    private Throwable exception = null;
    private Metric metric;
    private E result = null;
    private Method method = null;
    private Object[] parameters = null;
    private Future<E> future = null;
    
    protected HashMap<String, Metric> metrics = new HashMap<String, Metric>();
    
    private static ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            10L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    
    // Constructor used for Job.
    public Task(Session session) throws BadParameterException {
        super(session);
        this.object = this;
    }
    
    public Task(Task<E> orig) {
        super(orig);
        this.object = orig.object;
        this.state = orig.state;
        this.result = orig.result;
        this.exception = null;
        this.method = orig.method;
        if (orig.parameters != null) {
            this.parameters = orig.parameters.clone();
        } else {
            this.parameters = null;
        }
        this.future = orig.future;
        this.metrics = new HashMap<String, Metric>(orig.metrics);
        for (String s : metrics.keySet()) {
            Metric m = new Metric(metrics.get(s));
            m.setMonitorable(this);
            metrics.put(s, m);
        }
    }
    
    public Object clone() {
        return new Task<E>(this);
    }
    
    public Task(Object object, Session session, TaskMode mode, String methodName,
            Class[] parameterTypes, Object... parameters) {
        
        super(session);
                
        // lookup method of the task.
        try {
            method = object.getClass().getMethod(methodName, parameterTypes);
        } catch(Throwable e) {
            logger.error("Could not find method " + methodName, e);
            throw new SagaRuntimeException("Internal error", e);
        }
                     
        // Create the task metric.
        try {
            metric = new Metric(this, session, TASK_STATE,
                    "fires on task change, and has the literal value of the task state enum",
                    "ReadOnly", "1", "Enum", "New");
        } catch(Throwable e) {
            // Should not happen.
            logger.error("Could not create metric", e);
            throw new SagaRuntimeException("Unexpected exception", e);
        }
        metrics.put(TASK_STATE, metric);
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
            internalWaitFor(-1.0F);
            break;
        case TASK:
            break;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#cancel()
     */
    public synchronized void cancel() throws NotImplementedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        cancel(0.0F);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#cancel(float)
     */
    public void cancel(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (state == State.NEW) {
            throw new IncorrectStateException("cancel() called on task in state New");
        }
        cancel(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#getObject()
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject() throws NotImplementedException, TimeoutException,
            NoSuccessException {
        return (T) object;
    }

    /*
     * (non-Javadoc)
     * 
     * see org.ogf.saga.task.Task#getResult()
     */
    public E getResult() throws NotImplementedException, IncorrectStateException, TimeoutException,
           NoSuccessException {
        if (state == State.NEW || state == State.CANCELED || state == State.FAILED) {
            throw new IncorrectStateException("getResult called in state " + state);
        }
        waitFor();
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#getState()
     */
    public State getState() throws NotImplementedException, TimeoutException, NoSuccessException {
        return state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#rethrow()
     */
    public synchronized void rethrow() throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException, TimeoutException,
            NoSuccessException {
        if (state == State.FAILED) {
            // otherwise we should do nothing

            if (exception instanceof NotImplementedException) {
                throw (NotImplementedException) exception;
            }
            if (exception instanceof IncorrectURLException) {
                throw (IncorrectURLException) exception;
            }
            if (exception instanceof AuthenticationFailedException) {
                throw (AuthenticationFailedException) exception;
            }
            if (exception instanceof AuthorizationFailedException) {
                throw (AuthorizationFailedException) exception;
            }
            if (exception instanceof PermissionDeniedException) {
                throw (PermissionDeniedException) exception;
            }
            if (exception instanceof BadParameterException) {
                throw (BadParameterException) exception;
            }
            if (exception instanceof IncorrectStateException) {
                throw (IncorrectStateException) exception;
            }
            if (exception instanceof AlreadyExistsException) {
                throw (AlreadyExistsException) exception;
            }
            if (exception instanceof DoesNotExistException) {
                throw (DoesNotExistException) exception;
            }
            if (exception instanceof TimeoutException) {
                throw (TimeoutException) exception;
            }
            if (exception instanceof NoSuccessException) {
                throw (NoSuccessException) exception;
            }
            logger.error("Got unexpected exception in task", exception);
            //We should not get here.
            throw new SagaRuntimeException("Got unknown exception", exception);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#run()
     */
    public void run() throws NotImplementedException, IncorrectStateException, TimeoutException, NoSuccessException {
        if (state != State.NEW) {
            throw new IncorrectStateException("run() called in wrong state");
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
    public void waitFor() throws NotImplementedException, IncorrectStateException, TimeoutException,
            NoSuccessException {
        waitFor(-1.0F);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.task.Task#waitTask(float)
     */
    public synchronized boolean waitFor(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, TimeoutException, NoSuccessException {
        if (state == State.NEW) {
            throw new IncorrectStateException("waitFor called on new task");
        }
        return internalWaitFor(timeoutInSeconds);
    }
    
    /**
     * Internal version, we know that the state is correct.
     * @param timeoutInSeconds the timeout.
     * @return <code>true</code> if the task is finished.
     */
    private synchronized boolean internalWaitFor(float timeoutInSeconds) {
        
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
     * @see org.ogf.saga.monitoring.Monitorable#addCallback(java.lang.String,
     *      org.ogf.saga.monitoring.Callback)
     */
    public synchronized int addCallback(String name, Callback cb) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException, IncorrectStateException {
        Metric m = metrics.get(name);
        if (m == null) {
            throw new DoesNotExistException("metric " + name + " does not exist");
        }
        return m.addCallback(cb);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ogf.saga.monitoring.Monitorable#getMetric(java.lang.String)
     */
    public org.ogf.saga.monitoring.Metric getMetric(String name) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            DoesNotExistException, TimeoutException, NoSuccessException {
        Metric m = metrics.get(name);
        if (m == null) {
            throw new DoesNotExistException("metric " + name + " does not exist");
        }
        return m;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.monitoring.Monitorable#listMetrics()
     */
    public synchronized String[] listMetrics() throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, TimeoutException, NoSuccessException {
        return metrics.keySet().toArray(new String[metrics.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.saga.monitoring.Monitorable#removeCallback(java.lang.String,
     *      int)
     */
    public synchronized void removeCallback(String name, int cookie) throws NotImplementedException,
            DoesNotExistException, BadParameterException, TimeoutException, NoSuccessException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException {
        Metric m = metrics.get(name);
        if (m == null) {
            throw new DoesNotExistException("metric " + name + " does not exist");
        }
        m.removeCallback(cookie);
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
    
    public E get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, java.util.concurrent.TimeoutException {
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
            throw new SagaRuntimeException("Internal error", e);
        }
    }
    
    protected void setStateValue(State value) {
        state = value;
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
    
    protected void addMetric(String name, Metric metric) {
        metrics.put(name, metric);
    }
    
    protected synchronized void setException(Throwable e) {
        exception = e;
    }
}
