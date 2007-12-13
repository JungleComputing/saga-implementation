package org.ogf.saga.impl.job;

import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.impl.attributes.Attributes;
import org.ogf.saga.job.JobDescription;

public class JobDescriptionAttributes extends Attributes {

    public JobDescriptionAttributes() {
        addAttribute(JobDescription.EXECUTABLE, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.ARGUMENTS, AttributeType.STRING,
                true, false, false, false);
        addAttribute(JobDescription.SPMDVARIATION, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.TOTALCPUCOUNT, AttributeType.INT,
                false, false, false, false);
        addAttribute(JobDescription.NUMBEROFPROCESSES, AttributeType.INT,
                false, false, false, false);
        addAttribute(JobDescription.PROCESSESPERHOST, AttributeType.INT,
                false, false, false, false);
        addAttribute(JobDescription.THREADSPERPROCESS, AttributeType.INT,
                false, false, false, false);
        addAttribute(JobDescription.ENVIRONMENT, AttributeType.STRING,
                true, false, false, false);
        addAttribute(JobDescription.WORKINGDIRECTORY, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.INTERACTIVE, AttributeType.BOOL,
                false, false, false, false);
        addAttribute(JobDescription.INPUT, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.OUTPUT, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.ERROR, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.FILETRANSFER, AttributeType.STRING,
                true, false, false, false);
        addAttribute(JobDescription.CLEANUP, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.JOBSTARTTIME, AttributeType.INT,
                false, false, false, false);
        addAttribute(JobDescription.TOTALCPUTIME, AttributeType.INT,
                false, false, false, false);
        addAttribute(JobDescription.TOTALPHYSICALMEMORY, AttributeType.FLOAT,
                false, false, false, false);
        addAttribute(JobDescription.CPUARCHITECTURE, AttributeType.STRING,
                true, false, false, false);
        addAttribute(JobDescription.OPERATINGSYSTEMTYPE, AttributeType.STRING,
                true, false, false, false);
        addAttribute(JobDescription.CANDIDATEHOSTS, AttributeType.STRING,
                true, false, false, false);
        addAttribute(JobDescription.QUEUE, AttributeType.STRING,
                false, false, false, false);
        addAttribute(JobDescription.JOBCONTACT, AttributeType.STRING,
                true, false, false, false);
    }
}