package org.ogf.saga.apps.shell;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.task.TaskContainer;
import org.ogf.saga.task.TaskFactory;
import org.ogf.saga.task.WaitMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

    private Logger logger = LoggerFactory.getLogger(Environment.class);
    
    protected Directory cwd;
    protected URL resourceManager;
    protected JobService jobService;
    protected TaskContainer backgroundJobs;
    protected boolean terminated;
    
    public Environment() throws SagaException, IOException {
        // initialize the current working directory to the local home directory
        logger.info("Initializing current working directory");
        File userDir = new File(System.getProperty("user.dir"));
        URI userDirUri = userDir.toURI();
        URL cwdUrl = URLFactory.createURL("file://localhost");
        cwdUrl.setPath(userDirUri.getPath());
        cwd = FileFactory.createDirectory(cwdUrl);
        
        // initialize the manager for running jobs and discovering resources
        resourceManager = URLFactory.createURL("local://localhost");
        setResourceManager(resourceManager);
        
        // create a task container for background jobs
        backgroundJobs = TaskFactory.createTaskContainer();
        
        terminated = false;
    }
    
    /**
     * Returns the current working directory
     * 
     * @return the current working directory
     */
    public Directory getCwd() {
        return cwd;
    }
    
    /**
     * Sets a new current working directory.
     * 
     * @param newCwd the new current working directory.
     */
    public void setCwd(Directory newCwd) {
        cwd = newCwd;
    }

    /**
     * Returns the current job service.
     * 
     * @return the current job service.
     */
    public JobService getJobService() {
        return jobService;
    }
    
    /**
     * Returns the URL of the current resource manager.
     * 
     * @return the URL of the current resource manager.
     */
    public URL getResourceManager() {
        return resourceManager;
    }

    /**
     * Changes the current resource manager. The URL is used to create a new 
     * job service. 
     * 
     * @param rm the new resource manager
     * 
     * @throws SagaException if no job service could be created with the given
     *     resource manager.
     */
    public void setResourceManager(URL rm) throws SagaException {
        logger.info("Creating job service '" + rm + "'");
    	jobService = JobFactory.createJobService(rm);
        resourceManager = rm;
    }
    
    /**
     * Returns a task container that is supposed to hold all background jobs
     * started in the SAGA shell.
     * 
     * @return a task container for background jobs
     */
    public TaskContainer getBackgroundJobs() {
        return backgroundJobs;
    }
    
    /**
     * Returns whether this shell is terminated (i.e. terminate() has been 
     * called)
     *  
     * @return true is terminate() has been called, false otherwise.
     */
    public boolean isTerminated() {
        return terminated;
    }
    
    /**
     * Terminates the shell. First all running background jobs are cancelled and
     * waited for. Second, the default SAGA session is closed (which may
     * trigger cleanup in some of the adaptors).
     */
    public void terminate() {
    	if (!terminated) {
    		terminated = true;
        
            try {
                int jobCount = backgroundJobs.size();
                if (jobCount > 0) {
                    System.out.println("Killing " + jobCount + " background jobs...");
                    backgroundJobs.cancel();
                    backgroundJobs.waitFor(WaitMode.ALL);
                }
            } catch (SagaException e) {
                Util.printSagaException(e);
            }
            
            try {
                logger.debug("Closing default session...");
                Session defaultSession = SessionFactory.createSession(true);
                defaultSession.close();
            } catch (SagaException e) {
            	Util.printSagaException(e);
            }
    	}
    }
    
}
