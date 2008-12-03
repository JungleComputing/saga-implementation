package org.ogf.saga.spi.job;

import java.util.List;

import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobSelf;
import org.ogf.saga.job.JobService;
import org.ogf.saga.task.Async;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public interface JobServiceSPI extends Async {

    /**
     * Creates a job instance as specified by the job description provided. The
     * job is delivered in 'New' state. The provided job description is copied,
     * so can be modified after this call.
     * 
     * @param jd
     *            the job description.
     * @return the job.
     */
    public Job createJob(JobDescription jd) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException, TimeoutException,
            NoSuccessException;

    /**
     * Runs the specified command on the specified host. Deviation from the SAGA
     * specification: the input, output and error stream OUT parameters are not
     * specified here, since Java has no OUT parameters. Unfortunately, their
     * absence, according to the SAGA specifications, implies a non-interactive
     * job. Since interactive jobs should still be supported, a parameter is
     * added here to specify whether the job is interactive. If interactive, the
     * streams can be obtained from the Job using the {@link Job#getStdin()},
     * {@link Job#getStdout()}, and {@link Job#getStderr()} methods.
     * 
     * @param commandLine
     *            the command to run.
     * @param host
     *            hostname of the host on which the command must be run. If this
     *            is an empty string, the implementation is free to choose a
     *            host.
     * @param interactive
     *            specifies whether the job is interactive.
     * @return the job.
     */
    public Job runJob(String commandLine, String host, boolean interactive)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException;

    /**
     * Obtains the list of jobs that are currently known to the resource
     * manager.
     * 
     * @return a list of job identifications.
     */
    public List<String> list() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException;

    /**
     * Returns the job instance associated with the specified job
     * identification.
     * 
     * @param jobId
     *            the job identification.
     * @return the job instance.
     */
    public Job getJob(String jobId) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            DoesNotExistException, TimeoutException, NoSuccessException;

    /**
     * Returns a job instance representing the calling application.
     * 
     * @return the job instance.
     */
    public JobSelf getSelf() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, TimeoutException, NoSuccessException;

    /**
     * Creates a task that creates a job instance as specified by the job
     * description provided. The job is delivered in 'New' state.
     * 
     * @param mode
     *            the task mode.
     * @param jd
     *            the job description.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<JobService, Job> createJob(TaskMode mode, JobDescription jd)
            throws NotImplementedException;

    /**
     * Creates a task that runs the specified command on the specified host.
     * Deviation from the SAGA specification: the input, output and error stream
     * OUT parameters are not specified here, since Java has no OUT parameters.
     * Unfortunately, their absence, according to the SAGA specifications,
     * implies a non-interactive job. Since interactive jobs should still be
     * supported, a parameter is added here to specify whether the job is
     * interactive. If interactive, the streams can be obtained from the Job
     * using the {@link Job#getStdin()}, {@link Job#getStdout()}, and
     * {@link Job#getStderr()} methods
     * 
     * @param mode
     *            the task mode.
     * @param commandLine
     *            the command to run.
     * @param host
     *            hostname of the host on which the command must be run. If this
     *            is an empty string, the implementation is free to choose a
     *            host.
     * @param interactive
     *            specifies whether the job is interactive.
     * @return the task.
     */
    public Task<JobService, Job> runJob(TaskMode mode, String commandLine,
            String host, boolean interactive) throws NotImplementedException;

    /**
     * Creates a task that obtains the list of jobs that are currently known to
     * the resource manager.
     * 
     * @param mode
     *            the task mode.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<JobService, List<String>> list(TaskMode mode)
            throws NotImplementedException;

    /**
     * Creates a task that obtains the job instance associated with the
     * specified job identification.
     * 
     * @param mode
     *            the task mode.
     * @param jobId
     *            the job identification.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<JobService, Job> getJob(TaskMode mode, String jobId)
            throws NotImplementedException;

    /**
     * Creates a task that obtains a job instance representing the calling
     * application.
     * 
     * @param mode
     *            the task mode.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<JobService, JobSelf> getSelf(TaskMode mode)
            throws NotImplementedException;
}
