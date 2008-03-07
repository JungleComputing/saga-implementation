package org.ogf.saga.spi.stream;

import org.ogf.saga.URL;
import org.ogf.saga.attributes.AsyncAttributes;
import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.context.Context;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.monitoring.AsyncMonitorable;
import org.ogf.saga.permissions.Permissions;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamInputStream;
import org.ogf.saga.stream.StreamOutputStream;
import org.ogf.saga.stream.StreamService;
import org.ogf.saga.task.Async;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public interface StreamSpiInterface extends Async, AsyncMonitorable<Stream>,
        AsyncAttributes<Stream>, Permissions<Stream> {
    
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
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
               PermissionDeniedException, IncorrectStateException, TimeoutException, NoSuccessException;

    /**
     * Returns the remote authorization info.
     * The returned context is deep-copied.
     * @return the remote context.
     */
    public Context getContext()
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
               PermissionDeniedException, IncorrectStateException, TimeoutException, NoSuccessException;

    // management methods

    /**
     * Establishes a connection to the target defined during the construction
     * of the stream.
     */
    public void connect()
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
               PermissionDeniedException, IncorrectStateException, TimeoutException, NoSuccessException;

    /**
     * Checks if the stream is ready for I/O, or if it has entered the
     * ERROR state. It will only check for the specified activities.
     * If the timeout expires, an empty list is returned.
     * @param what the activities to wait for.
     * @param timeoutInSeconds the timeout in seconds.
     * @return the activities that apply.
     */
    public int waitFor(int what, float timeoutInSeconds)
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
               PermissionDeniedException, IncorrectStateException, NoSuccessException;

    /**
     * Closes an active connection.
     * I/O is no longer possible. The stream is put in state CLOSED.
     * @param timeoutInSeconds the timeout in seconds.
     */
    public void close(float timeoutInSeconds)
        throws NotImplementedException, IncorrectStateException, NoSuccessException;

    // I/O methods

    /**
     * Obtains an InputStream from the stream.
     * @return the inputstream.
     */
    public StreamInputStream getInputStream() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException, SagaIOException;

    /**
     * Obtains an OutputStream from the stream.
     * @return the outputstream.
     */
    public StreamOutputStream getOutputStream() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException, SagaIOException;
    
    /**
     * Reads a raw buffer from the stream.
     * @param len the maximum number of bytes to be read.
     * @param buffer the buffer to store into.
     * @return the number of bytes read.
     * @exception SagaIOException deviation from the SAGA specs: thrown in case
     *     of an error, where the SAGA specs describe a return of a negative
     *     value, corresponding to negatives of the respective ERRNO error
     *     code.
     */
    public int read(Buffer buffer, int len)
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
               PermissionDeniedException, BadParameterException, IncorrectStateException, TimeoutException,
               NoSuccessException, SagaIOException;

    /**
     * Writes a raw buffer to the stream.
     * Note: if the buffer contains less data than the specified len, only
     * the data in the buffer are written.
     * @param len the number of bytes of data in the buffer.
     * @param buffer the data to be sent.
     * @return the number of bytes written.
     * @exception SagaIOException deviation from the SAGA specs: thrown in case
     *     of an error, where the SAGA specs describe a return of a negative
     *     value, corresponding to negatives of the respective ERRNO error
     *     code.
     */
    public int write(Buffer buffer, int len)
        throws NotImplementedException, AuthenticationFailedException, AuthorizationFailedException,
               PermissionDeniedException, BadParameterException, IncorrectStateException, TimeoutException,
               NoSuccessException, SagaIOException;

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
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream, URL> getUrl(TaskMode mode)
        throws NotImplementedException;

    /**
     * Creates a task that obtains the remote authorization info.
     * The returned context is deep-copied.
     * @param mode the task mode.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream, Context> getContext(TaskMode mode)
        throws NotImplementedException;

    // management methods

    /**
     * Returns a task that
     * establishes a connection to the target defined during the construction
     * of the stream.
     * @param mode the task mode.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream, Void> connect(TaskMode mode)
        throws NotImplementedException;

    /**
     * Returns a task that
     * checks if the stream is ready for I/O, or if it has entered the
     * ERROR state. It will only check for the specified activities.
     * If the timeout expires, the task will return an empty list.
     * @param mode the task mode.
     * @param what the activities to wait for.
     * @param timeoutInSeconds the timout in seconds.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream, Integer> waitFor(TaskMode mode, int what,
            float timeoutInSeconds)
        throws NotImplementedException;

    /**
     * Returns a task that closes an active connection.
     * @param mode the task mode.
     * @param timeoutInSeconds the timeout in seconds.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream, Void> close(TaskMode mode, float timeoutInSeconds)
        throws NotImplementedException;

    // I/O methods

    /**
     * Creates a task that obtains an OutputStream from the stream.
     * @return the task.
     */
    public Task<Stream, StreamInputStream> getInputStream(TaskMode mode)
            throws NotImplementedException;
    
    /**
     * Creates a task that obtains an OutputStream from the stream.
     * @return the task.
     */
    public Task<Stream, StreamOutputStream> getOutputStream(TaskMode mode)
            throws NotImplementedException;
    
    /**
     * Creates a task that reads a raw buffer from the stream.
     * @param mode the task mode.
     * @param len the maximum number of bytes to be read.
     * @param buffer the buffer to store into.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream, Integer> read(TaskMode mode, Buffer buffer, int len)
        throws NotImplementedException;

    /**
     * Creates a task that writes a raw buffer to the stream.
     * Note: if the buffer contains less data than the specified len, only
     * the data in the buffer are written.
     * @param mode the task mode.
     * @param len the number of bytes of data in the buffer.
     * @param buffer the data to be sent.
     * @return the number of bytes written.
     * @exception SagaIOException deviation from the SAGA specs: thrown in case
     *     of an error.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream, Integer> write(TaskMode mode, Buffer buffer, int len)
        throws NotImplementedException;
}
