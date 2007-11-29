package org.ogf.saga.spi.stream;

import java.io.IOException;
import java.util.List;

import org.ogf.saga.URL;
import org.ogf.saga.attributes.AsyncAttributes;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.monitoring.AsyncMonitorable;
import org.ogf.saga.permissions.Permissions;
import org.ogf.saga.stream.Activity;
import org.ogf.saga.task.Async;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public interface StreamSpiInterface extends Async, AsyncMonitorable,
        AsyncAttributes, Permissions {
    
    /**
     * Returns a clone.
     */    
    public Object clone() throws CloneNotSupportedException;

    // inspection methods

    /**
     * Obtains the URL that was used to create the stream.
     * When this stream is the result of a {@link StreamService#serve()}
     * call, <code>null</code> is returned.
     * @return the URL.
     */
    public URL getUrl()
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, IncorrectState, Timeout, NoSuccess;

    /**
     * Returns the remote authorization info.
     * The returned context is deep-copied.
     * @return the remote context.
     */
    public Context getContext()
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, IncorrectState, Timeout, NoSuccess;

    // management methods

    /**
     * Establishes a connection to the target defined during the construction
     * of the stream.
     */
    public void connect()
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, IncorrectState, Timeout, NoSuccess;

    /**
     * Checks if the stream is ready for I/O, or if it has entered the
     * ERROR state. It will only check for the specified activities.
     * If the timeout expires, an empty list is returned.
     * @param what the activities to wait for.
     * @param timeoutInSeconds the timeout in seconds.
     * @return the activities that apply.
     */
    public List<Activity> waitStream(float timeoutInSeconds, int what)
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, IncorrectState, NoSuccess;

    /**
     * Closes an active connection.
     * I/O is no longer possible. The stream is put in state CLOSED.
     * @param timeoutInSeconds the timeout in seconds.
     */
    public void close(float timeoutInSeconds)
        throws NotImplemented, IncorrectState, NoSuccess;

    // I/O methods

    /**
     * Reads a raw buffer from the stream.
     * @param len the maximum number of bytes to be read.
     * @param buffer the buffer to store into.
     * @return the number of bytes read.
     * @exception IOException deviation from the SAGA specs: thrown in case
     *     of an error, where the SAGA specs describe a return of a negative
     *     value, corresponding to negatives of the respective ERRNO error
     *     code.
     */
    public int read(int len, Buffer buffer)
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, BadParameter, IncorrectState, Timeout,
               NoSuccess, IOException;

    /**
     * Writes a raw buffer to the stream.
     * Note: if the buffer contains less data than the specified len, only
     * the data in the buffer are written.
     * @param len the number of bytes of data in the buffer.
     * @param buffer the data to be sent.
     * @return the number of bytes written.
     * @exception IOException deviation from the SAGA specs: thrown in case
     *     of an error, where the SAGA specs describe a return of a negative
     *     value, corresponding to negatives of the respective ERRNO error
     *     code.
     */
    public int write(int len, Buffer buffer)
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, BadParameter, IncorrectState, Timeout,
               NoSuccess, IOException;

    //
    // Task versions ...
    //

    // inspection methods

    /**
     * Creates a task that obtains the URL that was used to create the stream.
     * When this stream is the result of a {@link StreamService#serve()}
     * call, the URL will be <code>null</code>.
     * @param mode the task mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<URL> getUrl(TaskMode mode)
        throws NotImplemented;

    /**
     * Creates a task that obtains the remote authorization info.
     * The returned context is deep-copied.
     * @param mode the task mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Context> getContext(TaskMode mode)
        throws NotImplemented;

    // management methods

    /**
     * Returns a task that
     * establishes a connection to the target defined during the construction
     * of the stream.
     * @param mode the task mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task connect(TaskMode mode)
        throws NotImplemented;

    /**
     * Returns a task that
     * checks if the stream is ready for I/O, or if it has entered the
     * ERROR state. It will only check for the specified activities.
     * If the timeout expires, the task will return an empty list.
     * @param mode the task mode.
     * @param what the activities to wait for.
     * @param timeoutInSeconds the timout in seconds.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<List<Activity>> waitStream(TaskMode mode,
            float timeoutInSeconds, int what)
        throws NotImplemented;

    /**
     * Returns a task that closes an active connection.
     * @param mode the task mode.
     * @param timeoutInSeconds the timeout in seconds.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task close(TaskMode mode, float timeoutInSeconds)
        throws NotImplemented;

    // I/O methods

    /**
     * Creates a task that reads a raw buffer from the stream.
     * @param mode the task mode.
     * @param len the maximum number of bytes to be read.
     * @param buffer the buffer to store into.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Integer> read(TaskMode mode, int len, Buffer buffer)
        throws NotImplemented;

    /**
     * Creates a task that writes a raw buffer to the stream.
     * Note: if the buffer contains less data than the specified len, only
     * the data in the buffer are written.
     * @param mode the task mode.
     * @param len the number of bytes of data in the buffer.
     * @param buffer the data to be sent.
     * @return the number of bytes written.
     * @exception IOException deviation from the SAGA specs: thrown in case
     *     of an error.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Integer> write(TaskMode mode, int len, Buffer buffer)
        throws NotImplemented;
}
