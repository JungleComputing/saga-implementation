package org.ogf.saga.impl.job;

import org.ogf.saga.impl.attributes.AsyncAttributes;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.job.Job;
import org.ogf.saga.session.Session;

public class JobAttributes extends AsyncAttributes {

    public JobAttributes(Object object, Session session) {
        super(object, session, false);
        addAttribute(Job.JOBID, AttributeType.STRING, false, true, false, false);
        addAttribute(Job.EXECUTIONHOSTS, AttributeType.STRING, true, true, false, false);
        addAttribute(Job.CREATED, AttributeType.TIME, false, true, false, false);
        addAttribute(Job.STARTED, AttributeType.TIME, false, true, false, false);
        addAttribute(Job.FINISHED, AttributeType.TIME, false, true, false, false);
        addAttribute(Job.EXITCODE, AttributeType.INT, false, true, false, false);
        addAttribute(Job.TERMSIG, AttributeType.INT, false, true, false, false);
    }
}
