package org.ogf.saga.impl.task;

import org.ogf.saga.error.BadParameter;
import org.ogf.saga.error.DoesNotExist;
import org.ogf.saga.error.IncorrectState;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;
import org.ogf.saga.impl.monitoring.Metric;

final class TaskMetric extends Metric {

    public TaskMetric(Session session, Monitorable monitorable)
            throws NotImplemented, BadParameter {
    
        super(monitorable, session, org.ogf.saga.task.Task.TASK_STATE,
                "fires on task change, and has the literal value of the task state enum",
                "ReadOnly", "1", "Enum", "New");
    }
    
    // Make setMode available for classes in this package.
    protected void setMode(String value) throws NotImplemented, BadParameter, DoesNotExist, IncorrectState {
        super.setMode(value);
    }
    
    // Make setValue available for classes in this package.
    protected void setValue(String value) throws NotImplemented, BadParameter, IncorrectState, DoesNotExist {
        super.setValue(value);
    }
    
    // make internalFire available for classes in this package.
    protected void internalFire() {
        super.internalFire();
    }
}
