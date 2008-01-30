package org.ogf.saga.spi.stream;

import org.ogf.saga.URL;
import org.ogf.saga.error.AuthenticationFailed;
import org.ogf.saga.error.AuthorizationFailed;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.PermissionDenied;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.monitoring.AsyncMonitorable;
import org.ogf.saga.permissions.Permissions;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.task.Async;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public interface StreamServiceSpiInterface extends AsyncMonitorable,
        Permissions, Async {

    /**
     * Obtains the URL to be used to connect to this server.
     * @return the URL.
     */
    public URL getUrl()
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, IncorrectState, Timeout, NoSuccess;

    /**
     * Waits for incoming client connections (like an accept of a
     * serversocket). The returned stream is in OPEN state.
     * @param timeoutInSeconds the timeout in seconds.
     * @return the client connection, or <code>null</code> if the timeout
     * expires before a client connects.
     */
    public Stream serve(float timeoutInSeconds)
        throws NotImplemented, AuthenticationFailed, AuthorizationFailed,
               PermissionDenied, IncorrectState, Timeout, NoSuccess;

    /**
     * Closes a stream service.
     * @param timeoutInSeconds the timeout in seconds.
     */
    public void close(float timeoutInSeconds)
        throws NotImplemented, IncorrectState, NoSuccess;

    //
    // Task versions ...
    //

    /**
     * Obtains a task to obtain the URL to be used to connect to this server.
     * @param mode the task mode.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<URL> getUrl(TaskMode mode)
        throws NotImplemented;

    /**
     * Obtains a task that waits for incoming client connections
     * (like an accept of a serversocket).
     * The returned stream is in OPEN state.
     * @param mode the task mode.
     * @param timeoutInSeconds the timeout in seconds.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task<Stream> serve(TaskMode mode, float timeoutInSeconds)
        throws NotImplemented;

    /**
     * Obtains a task that closes a stream service.
     * @param mode the task mode.
     * @param timeoutInSeconds the timeout in seconds.
     * @return the task.
     * @exception NotImplemented is thrown when the task version of this
     *     method is not implemented.
     */
    public Task close(TaskMode mode, float timeoutInSeconds)
        throws NotImplemented;
}