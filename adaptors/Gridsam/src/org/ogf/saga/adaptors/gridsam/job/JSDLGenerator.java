package org.ogf.saga.adaptors.gridsam.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.xmlbeans.XmlCursor;
import org.icenigrid.schema.jsdl.posix.y2005.m11.ArgumentType;
import org.icenigrid.schema.jsdl.posix.y2005.m11.EnvironmentType;
import org.icenigrid.schema.jsdl.posix.y2005.m11.FileNameType;
import org.icenigrid.schema.jsdl.posix.y2005.m11.POSIXApplicationDocument;
import org.icenigrid.schema.jsdl.posix.y2005.m11.POSIXApplicationType;
import org.icenigrid.schema.jsdl.y2005.m11.ApplicationType;
import org.icenigrid.schema.jsdl.y2005.m11.BoundaryType;
import org.icenigrid.schema.jsdl.y2005.m11.CPUArchitectureType;
import org.icenigrid.schema.jsdl.y2005.m11.CandidateHostsType;
import org.icenigrid.schema.jsdl.y2005.m11.CreationFlagEnumeration;
import org.icenigrid.schema.jsdl.y2005.m11.DataStagingType;
import org.icenigrid.schema.jsdl.y2005.m11.JobDefinitionDocument;
import org.icenigrid.schema.jsdl.y2005.m11.JobDefinitionType;
import org.icenigrid.schema.jsdl.y2005.m11.JobDescriptionType;
import org.icenigrid.schema.jsdl.y2005.m11.JobIdentificationType;
import org.icenigrid.schema.jsdl.y2005.m11.OperatingSystemType;
import org.icenigrid.schema.jsdl.y2005.m11.OperatingSystemTypeEnumeration;
import org.icenigrid.schema.jsdl.y2005.m11.OperatingSystemTypeType;
import org.icenigrid.schema.jsdl.y2005.m11.ProcessorArchitectureEnumeration;
import org.icenigrid.schema.jsdl.y2005.m11.RangeValueType;
import org.icenigrid.schema.jsdl.y2005.m11.ResourcesType;
import org.icenigrid.schema.jsdl.y2005.m11.SourceTargetType;
import org.ogf.saga.SagaObject;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.job.JobDescription;

class JSDLGenerator {

    private static final Logger logger = LoggerFactory.getLogger(JSDLGenerator.class);

    private final JobDescription jobDescription;

    private final JobDefinitionDocument jobDefinitionDocument;

    JSDLGenerator(JobDescription jd, SagaObject o) throws BadParameterException,
            NotImplementedException, NoSuccessException {
        this.jobDescription = jd;

        jobDefinitionDocument = JobDefinitionDocument.Factory.newInstance();

        createJSDLDescription(o);
    }

    JobDefinitionDocument getJSDL() {
        return jobDefinitionDocument;
    }

    private void createJSDLDescription(SagaObject o)
            throws BadParameterException, NotImplementedException,
                            NoSuccessException {

        JobDefinitionType jobDef = jobDefinitionDocument.addNewJobDefinition();
        JobDescriptionType jobDescr = jobDef.addNewJobDescription();
        JobIdentificationType jobid = jobDescr.addNewJobIdentification();
        jobid.addJobProject("gridsam");

        addApplication(jobDescr, o);
        addResource(jobDescr);
        addDataStaging(jobDescr, o);
    }

    private String getV(String s) {
        try {
            s = jobDescription.getAttribute(s);
            if ("".equals(s)) {
                throw new Error("Not initialized");
            }
        } catch (Throwable e) {
            throw new Error("Not present");
        }
        return s;
    }

    private String[] getVec(String s) {
        String[] result;
        try {
            result = jobDescription.getVectorAttribute(s);
            if (result == null || result.length == 0) {
                throw new Error("Not initialized");
            }

        } catch (Throwable e) {
            throw new Error("Not present");
        }
        return result;
    }

    private void addResource(JobDescriptionType jobDescr) {
        String[] hosts;

        try {
            hosts = getVec(JobDescription.CANDIDATEHOSTS);
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("did not find any properties to use."
                        + " Not adding <Resources> tag");
            }
            return;
        }

        ResourcesType resources = jobDescr.addNewResources();

        CandidateHostsType candidates = resources.addNewCandidateHosts();

        for (String host : hosts) {
            candidates.addHostName(host);
        }

        try {
            String s = getV(JobDescription.TOTALPHYSICALMEMORY);
            RangeValueType r = resources.addNewTotalPhysicalMemory();
            BoundaryType rr = r.addNewUpperBoundedRange();
            rr.setStringValue(s);
        } catch (Throwable e) {
            // ignored
        }

        try {
            String s = getV(JobDescription.TOTALCPUTIME);
            RangeValueType r = resources.addNewTotalCPUTime();
            BoundaryType rr = r.addNewUpperBoundedRange();
            rr.setStringValue(s);
        } catch (Throwable e) {
            // ignored
        }

        try {
            String s = getV(JobDescription.CPUARCHITECTURE);
            CPUArchitectureType c = resources.addNewCPUArchitecture();
            c.setCPUArchitectureName(ProcessorArchitectureEnumeration.Enum
                    .forString(s));
        } catch (Throwable e) {
            // ignored
        }

        try {
            String s = getV(JobDescription.OPERATINGSYSTEMTYPE);
            OperatingSystemType c = resources.addNewOperatingSystem();
            OperatingSystemTypeType os = c.addNewOperatingSystemType();
            os.setOperatingSystemName(OperatingSystemTypeEnumeration.Enum
                    .forString(s));
        } catch (Throwable e) {
            // ignored
        }
    }

    private void addApplication(JobDescriptionType jobDescr, SagaObject o)
            throws BadParameterException {
        String exec;
        try {
            exec = getV(JobDescription.EXECUTABLE);
        } catch (Throwable e) {
            throw new BadParameterException("Could not get Executable for job",
                    e, o);
        }

        ApplicationType appl = jobDescr.addNewApplication();
        XmlCursor cursor = appl.newCursor();
        cursor.toEndToken();

        POSIXApplicationDocument posixDoc = POSIXApplicationDocument.Factory
                .newInstance();
        POSIXApplicationType posixAppl = posixDoc.addNewPOSIXApplication();
        FileNameType f = posixAppl.addNewExecutable();
        f.setStringValue(exec);
        String[] arguments;
        try {
            arguments = getVec(JobDescription.ARGUMENTS);
            for (String argument : arguments) {
                if (logger.isDebugEnabled()) {
                    logger.debug("argument=" + argument);
                }
                ArgumentType arg = posixAppl.addNewArgument();
                arg.setStringValue(argument);
            }
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Got ignored exception for ARGUMENTS", e);
            }
        }

        try {
            String stdin = getV(JobDescription.INPUT);
            f = posixAppl.addNewInput();
            f.setStringValue(stdin);
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Got ignored exception for INPUT", e);
            }
        }

        try {
            String stdout = getV(JobDescription.OUTPUT);
            f = posixAppl.addNewOutput();
            f.setStringValue(stdout);
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Got ignored exception for OUTPUT", e);
            }
        }

        try {
            String stderr = getV(JobDescription.ERROR);
            f = posixAppl.addNewError();
            f.setStringValue(stderr);
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Got ignored exception for ERROR", e);
            }
        }

        try {
            String[] env = getVec(JobDescription.ENVIRONMENT);
            for (String e : env) {
                String key = e;
                String val;
                int index = e.indexOf('=');
                if (index == -1) {
                    val = "";
                } else {
                    key = e.substring(0, index);
                    val = e.substring(index + 1);
                }
                EnvironmentType ev = posixAppl.addNewEnvironment();
                ev.setName(key);
                ev.setStringValue(val);
            }
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Got ignored exception for ENVIRONMENT", e);
            }
        }

        XmlCursor c = posixDoc.newCursor();
        c.toStartDoc();
        c.toNextToken();
        c.moveXml(cursor);
    }

    private void addDataStage(JobDescriptionType jobDescr, String fileName,
            boolean prestage, String url, boolean deleteOnTermination) {
        DataStagingType ds = jobDescr.addNewDataStaging();
        SourceTargetType st = prestage ? ds.addNewSource() : ds.addNewTarget();
        st.setURI(url);
        ds.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
        ds.setDeleteOnTermination(deleteOnTermination);
        ds.setFileName(fileName);
    }

    private void addDataStaging(JobDescriptionType jobDescr, SagaObject o)
            throws BadParameterException, NotImplementedException {

        String[] transfers = null;

        try {
            transfers = getVec(JobDescription.FILETRANSFER);
        } catch (Throwable e) {
            return;
        }

        for (String s : transfers) {
            String[] parts = s.split(" << ");
            if (parts.length == 1) {
                // no match
            } else {
                throw new NotImplementedException(
                        "PostStage append is not supported", o);
            }
            parts = s.split(" >> ");
            if (parts.length == 1) {
                // no match
            } else {
                throw new NotImplementedException(
                        "PreStage append is not supported", o);
            }
            boolean prestage = true;
            parts = s.split(" > ");
            if (parts.length == 1) {
                prestage = false;
                parts = s.split(" < ");
                if (parts.length == 1) {
                    throw new BadParameterException(
                            "Unrecognized FileTransfer part: " + s, o);
                }
            }

            addDataStage(jobDescr, parts[1], prestage, parts[0], true);
        }
    }

}
