package org.ogf.saga.impl.task;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;
import org.ogf.saga.impl.monitoring.Metric;

final class TaskContainerMetric extends Metric {

    public TaskContainerMetric(Session session, Monitorable monitorable)
            throws NotImplemented, BadParameter {
    
        super(monitorable, session, org.ogf.saga.task.TaskContainer.TASKCONTAINER_STATE,
                "fires on state changes of any task in the container, and has the value of that task's cookie",
                "ReadOnly", "1", "Int", "");
    }
    
    // Make setMode available for classes in this package.
    protected void setMode(String value) throws NotImplemented, BadParameter, DoesNotExist, IncorrectState {
        super.setMode(value);
    }
    
    // Make setValue available for classes in this package.
    protected void setValue(String value) throws NotImplemented, BadParameter, IncorrectState, DoesNotExist {
        super.setValue(value);
    }
    
    // Make internalFire available for classes in this package.
    protected void internalFire() {
        super.internalFire();
    }
}
