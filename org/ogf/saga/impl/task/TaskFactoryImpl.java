package org.ogf.saga.impl.task;

import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.task.TaskContainer;

public class TaskFactoryImpl extends org.ogf.saga.task.TaskFactory {

    protected TaskContainer doCreateTaskContainer() throws NotImplementedException,
            TimeoutException, NoSuccessException {
        return new org.ogf.saga.impl.task.TaskContainerImpl();
    }
}
