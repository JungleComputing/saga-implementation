package org.ogf.saga.apps.shell.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.ogf.saga.apps.shell.Environment;
import org.ogf.saga.apps.shell.StreamPrinter;
import org.ogf.saga.apps.shell.Util;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.SagaException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.namespace.Flags;
import org.ogf.saga.task.TaskContainer;
import org.ogf.saga.task.TaskMode;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunJob extends EnvironmentCommand {

    private Logger logger = LoggerFactory.getLogger(RunJob.class);
    private ExecutorService executor;

    public RunJob(Environment env) {
        super(env);

        ThreadFactory f = new StreamPrinterThreadFactory();
        executor = Executors.newCachedThreadPool(f);
    }

    public String getHelpArguments() {
        return "[-out file] [-err file] <executable> [arg]* [&]";
    }

    public String getHelpExplanation() {
        return "run an executable using the current resource manager";
    }

    public void execute(String[] args) {
        String exec = null;
        String[] arguments = null;
        String output = null;
        String error = null;
        TaskMode mode = TaskMode.SYNC;

        try {
            for (int i = 1; i < args.length; i++) {
                if ("-out".equals(args[i])) {
                    output = args[++i];
                } else if ("-err".equals(args[i])) {
                    error = args[++i];
                } else {
                    exec = args[i++];

                    int end = args.length;
                    if (i < end) {
                        if ("&".equals(args[args.length - 1])) {
                            mode = TaskMode.ASYNC;
                            end--;
                        }

                        if (i < end) {
                            arguments = Arrays.copyOfRange(args, i, end);
                        }
                    }
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // wrong number of arguments
        }

        logger.debug("executable=" + exec + ", args="
                + Arrays.toString(arguments) + ", output=" + output
                + ", error=" + error + ", mode=" + mode);

        if (exec == null) {
            System.err.println("usage: " + args[0] + " " + getHelpArguments());
            return;
        }

        boolean interactive = (output == null && error == null);

        try {
            JobDescription desc = JobFactory.createJobDescription();
            desc.setAttribute(JobDescription.EXECUTABLE, exec);
            if (arguments != null) {
                desc.setVectorAttribute(JobDescription.ARGUMENTS, arguments);
            }
            if (interactive) {
                desc.setAttribute(JobDescription.INTERACTIVE, "True");
            } else {
                List<String> fileTransfers = new LinkedList<String>();

                if (output != null) {
                    String localOut = createLocalFile(output, "output");
                    String remoteOut = createRemoteFile(output, "output");
                    desc.setAttribute(JobDescription.OUTPUT, remoteOut);
                    fileTransfers.add(localOut + " < " + remoteOut);
                }

                if (error != null) {
                    String localErr = createLocalFile(error, "error");
                    String remoteErr = createRemoteFile(error, "error");
                    desc.setAttribute(JobDescription.ERROR, remoteErr);
                    fileTransfers.add(localErr + " < " + remoteErr);
                }

                String[] s = fileTransfers.toArray(new String[0]);
                desc.setVectorAttribute(JobDescription.FILETRANSFER, s);
            }

            JobService js = env.getJobService();

            logger.debug("Creating job");
            Job job = js.createJob(desc);

            StreamPrinter stdoutPrinter = null;
            StreamPrinter stderrPrinter = null;
            if (interactive) {
                stdoutPrinter = new StreamPrinter();
                stderrPrinter = new StreamPrinter();
                executor.submit(stdoutPrinter);
                executor.submit(stdoutPrinter);
            }

            logger.debug("Running job");
            job.run();

            if (interactive) {
                stdoutPrinter.setStream(job.getStdout());
                stderrPrinter.setStream(job.getStderr());
            }

            if (mode.equals(TaskMode.SYNC)) {
                logger.debug("Waiting for job");
                job.waitFor();
                logger.debug("Finished job");
            } else {
                logger.debug("Adding job to background tasks");
                TaskContainer bg = env.getBackgroundJobs();
                bg.add(job);
            }
        } catch (SagaException e) {
            Util.printSagaException(e);
        }
    }

    private String createLocalFile(String fileUrl, String outName)
            throws SagaException {
        // resolve the local file name againt the cwd of the shell
        URL localUrl = URLFactory.createURL(fileUrl);
        Directory cwd = env.getCwd();
        File localFile = cwd.openFile(localUrl, Flags.CREATE.getValue());
        String localName = localFile.getURL().toString();

        if (logger.isDebugEnabled()) {
            logger.debug("Using local " + outName + " '" + localName + "'");
        }

        return localName;
    }

    private String createRemoteFile(String fileUrl, String outName)
            throws SagaException {
        // only use the base name of the file URL for the remote file
        // (no adaptor can handle full URLs anyway)
        URL remoteUrl = URLFactory.createURL(fileUrl);
        String remotePath = remoteUrl.getPath();

        if (remotePath == null || remotePath.isEmpty()) {
            throw new BadParameterException("Illegal file name: '" + fileUrl
                    + "'");
        }

        java.io.File remoteFile = new java.io.File(remotePath);
        String remoteName = remoteFile.getName();

        if (logger.isDebugEnabled()) {
            logger.debug("Using remote " + outName + " '" + remoteName + "'");
        }

        return remoteName;
    }

    private class StreamPrinterThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setPriority(Thread.MAX_PRIORITY);
            t.setName("StreamPrinter");
            return t;
        }

    }

}
