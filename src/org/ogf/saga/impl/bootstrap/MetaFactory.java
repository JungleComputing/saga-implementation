package org.ogf.saga.impl.bootstrap;

import org.ogf.saga.bootstrap.SagaFactory;
import org.ogf.saga.buffer.BufferFactory;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.NotImplementedException;
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

    public BufferFactory createBufferFactory() throws NotImplementedException {
        return new org.ogf.saga.impl.buffer.BufferFactory();
    }

    public ContextFactory createContextFactory() {
        return new org.ogf.saga.impl.context.ContextFactory();
    }

    public FileFactory createFileFactory() {
        return new org.ogf.saga.proxies.file.FileWrapperFactory();
    }

    public JobFactory createJobFactory() throws NotImplementedException {
        return new org.ogf.saga.proxies.job.JobWrapperFactory();
    }

    public LogicalFileFactory createLogicalFileFactory() throws NotImplementedException {
        return new org.ogf.saga.proxies.logicalfile.LogicalFileWrapperFactory();
    }

    public MonitoringFactory createMonitoringFactory() throws NotImplementedException {
        return new org.ogf.saga.impl.monitoring.MonitoringFactory();
    }

    public NSFactory createNamespaceFactory() throws NotImplementedException {
        return new org.ogf.saga.proxies.namespace.NSWrapperFactory();
    }

    public RPCFactory createRPCFactory() {
        return new org.ogf.saga.proxies.rpc.RPCWrapperFactory();
    }

    public SessionFactory createSessionFactory() {
        return new org.ogf.saga.impl.session.SessionFactory();
    }

    public StreamFactory createStreamFactory() throws NotImplementedException {
        return new org.ogf.saga.proxies.stream.StreamWrapperFactory();
    }

    public TaskFactory createTaskFactory() throws NotImplementedException {
        return new org.ogf.saga.impl.task.TaskFactory();
    }
}
