package org.ogf.saga.apps.shell.command;

import java.util.Arrays;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.task.State;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskContainer;

public class ListJobs extends EnvironmentCommand {

	public ListJobs(Environment env) {
		super(env);
	}
	
    public String getHelpArguments() {
        return "";
    }

    public String getHelpExplanation() {
        return "show all background jobs";
    }

    public void execute(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: " + args[0]);
            return;
        }
        
        TaskContainer bg = env.getBackgroundJobs();
        
        try {
            int[] cookies = bg.listTasks();
            Arrays.sort(cookies);
            
            for (int cookie: cookies) {
                Task<?, ?> t = bg.getTask(cookie);
                
                if (t instanceof Job) {
                    Job job = (Job)t;
                
                    State jobState = job.getState();
                    System.out.print("[" + cookie + "] " + jobState);
                    
                    String jobId = job.getAttribute(Job.JOBID);
                    System.out.print("\t" + jobId);
                    
                    JobDescription jd = job.getJobDescription();
    
                    String exec = jd.getAttribute(JobDescription.EXECUTABLE);
                    System.out.print("\t" + exec);
                    
                    for (String arg: jd.getVectorAttribute(JobDescription.ARGUMENTS)) {
                        System.out.print(" " + arg);
                    }
                    
                    System.out.println();
                    
                    // remove all finished jobs after a listing
                    if (jobState.equals(State.DONE) ||
                        jobState.equals(State.FAILED) ||
                        jobState.equals(State.CANCELED)) {
                        bg.remove(cookie);
                    }
                }
            }
            
        } catch (SagaException e) {
            Util.printSagaException(e);
        } 
    }

}
