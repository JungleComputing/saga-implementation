package org.ogf.saga.spi.rpc;

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
import org.ogf.saga.permissions.Permissions;
import org.ogf.saga.rpc.Parameter;
import org.ogf.saga.rpc.RPC;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public interface RPCSPI extends Permissions<RPC> {

    /**
     * Calls the remote procedure.
     * 
     * @param parameters
     *            arguments and results for the call.
     * @exception IncorrectURLException
     *                may be thrown here because the RPC server that was
     *                specified to the factory may not have been contacted
     *                before invoking the call.
     * @exception NoSuccessException
     *                is thrown for arbitrary backend failures, with a
     *                descriptive error message.
     */
    public void call(Parameter... parameters) throws NotImplementedException,
            IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException,
            DoesNotExistException, TimeoutException, NoSuccessException;

    /**
     * Closes the RPC handle instance. Note for Java implementations: A
     * finalizer could be used in case the application forgets to close.
     * 
     * @param timeoutInSeconds
     *            seconds to wait.
     */
    public void close(float timeoutInSeconds) throws NotImplementedException,
             NoSuccessException;

    //
    // Task versions ...
    //

    /**
     * Creates a task for calling the remote procedure.
     * 
     * @param mode
     *            the task mode.
     * @param parameters
     *            arguments and results for the call.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<RPC, Void> call(TaskMode mode, Parameter... parameters)
            throws NotImplementedException;

    /**
     * Creates a task for closing the RPC handle instance.
     * 
     * @param mode
     *            the task mode.
     * @param timeoutInSeconds
     *            seconds to wait.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<RPC, Void> close(TaskMode mode, float timeoutInSeconds)
            throws NotImplementedException;
}
