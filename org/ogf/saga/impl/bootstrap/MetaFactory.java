package org.ogf.saga.impl.bootstrap;

import org.ogf.saga.bootstrap.SagaFactory;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.NotImplemented;
import org.ogf.saga.error.SagaError;
import org.ogf.saga.file.FileFactory;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.logicalfile.LogicalFileFactory;
import org.ogf.saga.monitoring.MonitoringFactory;
import org.ogf.saga.namespace.NSFactory;
import org.ogf.saga.rpc.RPCFactory;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.stream.StreamFactory;
import org.ogf.saga.task.TaskFactory;

public class MetaFactory implements SagaFactory {

    public BufferFactory createBufferFactory() throws NotImplemented {
        return new org.ogf.saga.impl.buffer.BufferFactory();
    }

    public ContextFactory createContextFactory() {
        return new org.ogf.saga.impl.context.ContextFactory();
    }

    public FileFactory createFileFactory() {
        return new org.ogf.saga.proxies.file.FileWrapperFactory();
    }

    public JobFactory createJobFactory() throws NotImplemented {
        // TODO Auto-generated method stub
        throw new NotImplemented("Job factory not implemented yet");
    }

    public LogicalFileFactory createLogicalFileFactory() throws NotImplemented {
        return new org.ogf.saga.proxies.logicalfile.LogicalFileWrapperFactory();
    }

    public MonitoringFactory createMonitoringFactory() throws NotImplemented {
        return new org.ogf.saga.impl.monitoring.MonitoringFactory();
    }

    public NSFactory createNamespaceFactory() throws NotImplemented {
        return new org.ogf.saga.proxies.namespace.NSWrapperFactory();
    }

    public RPCFactory createRPCFactory() {
        // TODO Auto-generated method stub
        throw new SagaError("Task factory not implemented");
    }

    public SessionFactory createSessionFactory() {
        return new org.ogf.saga.impl.session.SessionFactory();
    }

    public StreamFactory createStreamFactory() throws NotImplemented {
        return new org.ogf.saga.proxies.stream.StreamWrapperFactory();
    }

    public TaskFactory createTaskFactory() throws NotImplemented {
        return new org.ogf.saga.impl.task.TaskFactory();
    }
}
