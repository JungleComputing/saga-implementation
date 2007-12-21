package org.ogf.saga.impl.job;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NotImplemented;
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
       
    JobAttributes(JobAttributes orig) {
        super(orig);
    }
    
    public Object clone() {
        return new JobAttributes(this);
    }
    
    protected void setValue(String key, String value) throws DoesNotExist, NotImplemented,
            IncorrectState, BadParameter {
        super.setValue(key, value);
    }
    
    protected void setVectorValue(String key, String[] values) throws DoesNotExist, NotImplemented,
            IncorrectState, BadParameter {
        super.setVectorValue(key, values);
    }
}
