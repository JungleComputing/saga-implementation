package org.ogf.saga.spi.stream;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.monitoring.AsyncMonitorable;
import org.ogf.saga.permissions.Permissions;
import org.ogf.saga.stream.Stream;
import org.ogf.saga.stream.StreamService;
import org.ogf.saga.task.Async;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;

public interface StreamServiceSPI extends AsyncMonitorable<StreamService>,
        Permissions<StreamService>, Async {

    /**
     * Obtains the URL to be used to connect to this server.
     * 
     * @return the URL.
     */
    public URL getUrl() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException;

    /**
     * Waits for incoming client connections (like an accept of a serversocket).
     * The returned stream is in OPEN state.
     * 
     * @param timeoutInSeconds
     *            the timeout in seconds.
     * @return the client connection, or <code>null</code> if the timeout
     *         expires before a client connects.
     */
    public Stream serve(float timeoutInSeconds) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException;

    /**
     * Closes a stream service.
     * 
     * @param timeoutInSeconds
     *            the timeout in seconds.
     */
    public void close(float timeoutInSeconds) throws NotImplementedException,
            IncorrectStateException, NoSuccessException;

    //
    // Task versions ...
    //

    /**
     * Obtains a task to obtain the URL to be used to connect to this server.
     * 
     * @param mode
     *            the task mode.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<StreamService, URL> getUrl(TaskMode mode)
            throws NotImplementedException;

    /**
     * Obtains a task that waits for incoming client connections (like an accept
     * of a serversocket). The returned stream is in OPEN state.
     * 
     * @param mode
     *            the task mode.
     * @param timeoutInSeconds
     *            the timeout in seconds.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<StreamService, Stream> serve(TaskMode mode,
            float timeoutInSeconds) throws NotImplementedException;

    /**
     * Obtains a task that closes a stream service.
     * 
     * @param mode
     *            the task mode.
     * @param timeoutInSeconds
     *            the timeout in seconds.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<StreamService, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException;
}
