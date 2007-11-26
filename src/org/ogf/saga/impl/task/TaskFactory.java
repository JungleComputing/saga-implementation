package org.ogf.saga.impl.task;

import org.ogf.saga.error.NoSuccess;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.Timeout;
import org.ogf.saga.task.TaskContainer;

public class TaskFactory extends org.ogf.saga.task.TaskFactory {

    protected TaskContainer doCreateTaskContainer() throws NotImplemented,
            Timeout, NoSuccess {
        return new org.ogf.saga.impl.task.TaskContainer();
    }
}
