package org.ogf.saga.impl.job;

import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.impl.attributes.AsyncAttributesImpl;
import org.ogf.saga.impl.attributes.AttributeType;
import org.ogf.saga.job.Job;
import org.ogf.saga.session.Session;

public class JobAttributes extends AsyncAttributesImpl<org.ogf.saga.job.Job> {

    public JobAttributes(org.ogf.saga.job.Job object, Session session) {
        super(object, session, false);
        addAttribute(Job.JOBID, AttributeType.STRING, false, true, false, false);
        addAttribute(Job.SERVICEURL, AttributeType.STRING, false, true, false, false);
        addAttribute(Job.EXECUTIONHOSTS, AttributeType.STRING, true, true, false, false);
        addAttribute(Job.WORKINGDIRECTORY, AttributeType.STRING, false, true, false, false);
        addAttribute(Job.CREATED, AttributeType.TIME, false, true, false, false);
        addAttribute(Job.STARTED, AttributeType.TIME, false, true, false, false);
        addAttribute(Job.FINISHED, AttributeType.TIME, false, true, false, false);
        addAttribute(Job.EXITCODE, AttributeType.INT, false, true, false, false);
        addAttribute(Job.TERMSIG, AttributeType.INT, false, true, false, false);
    }

    JobAttributes(JobAttributes orig) {
        super(orig);
    }

    protected synchronized void setValue(String key, String value)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        super.setValue(key, value);
    }

    protected synchronized void setVectorValue(String key, String[] values)
            throws DoesNotExistException, NotImplementedException,
            IncorrectStateException, BadParameterException {
        super.setVectorValue(key, values);
    }
}
