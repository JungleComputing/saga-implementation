package demo.job;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.ogf.saga.context.Context;
import org.ogf.saga.context.ContextFactory;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.job.Job;
import org.ogf.saga.job.JobDescription;
import org.ogf.saga.job.JobFactory;
import org.ogf.saga.job.JobService;
import org.ogf.saga.monitoring.Callback;
import org.ogf.saga.monitoring.Metric;
import org.ogf.saga.monitoring.Monitorable;
import org.ogf.saga.session.Session;
import org.ogf.saga.session.SessionFactory;
import org.ogf.saga.url.URL;
import org.ogf.saga.url.URLFactory;

public class TestJobGLite implements Callback {

    /**
     * @param args
     */
    public static void main(String[] args) {

        String server = "glite://wms3.grid.sara.nl:7443/glite_wms_wmproxy_server";

        if (args.length > 1) {
            System.err.println("Usage: java demo.job.TestJobGLite [<serverURL>]");
            System.exit(1);
        } else if (args.length == 1) {
            server = args[0];
        }
        
        try {
            Session session = SessionFactory.createSession(true);
            
            URL serverURL = URLFactory.createURL(server);

            Context context = ContextFactory.createContext("glite");
            // These settings are specific for me, or anyone in the pvier virtual organization.
            // Your mileage may vary ...
            context.setAttribute(Context.USERVO, "pvier");
            context.setAttribute(Context.SERVER, "voms://voms.grid.sara.nl:30000/O=dutchgrid/O=hosts/OU=sara.nl/CN=voms.grid.sara.nl");
            context.setAttribute(Context.USERPASS, getPassphrase());    // Well, I'm not going to tell you ...
            session.addContext(context);
                        
            // Create the JobService.
            JobService js = JobFactory.createJobService(serverURL);

            // Create a job: /bin/hostname executed on 1 node.
            JobDescription jd = JobFactory.createJobDescription();
            jd.setAttribute(JobDescription.EXECUTABLE, "/bin/hostname");
            jd.setAttribute(JobDescription.NUMBEROFPROCESSES, "1");
            jd.setAttribute(JobDescription.OUTPUT, "hostname.out");
            jd.setVectorAttribute(JobDescription.FILETRANSFER,
                    new String[] { "hostname.out < hostname.out" });
            
            // Create the job, run it, and wait for it.
            Job job = js.createJob(jd);
            job.addCallback(Job.JOB_STATE, new TestJobGLite());
            job.addCallback(Job.JOB_STATEDETAIL, new TestJobGLite());
            job.run();
            job.waitFor();
        } catch (Throwable e) {
            System.out.println("Got exception " + e);
            e.printStackTrace();
            e.getCause().printStackTrace();
        }

    }

    @Override
    public boolean cb(Monitorable mt, Metric metric, Context ctx)
            throws NotImplementedException, AuthorizationFailedException {
        try {
            String value = metric.getAttribute(Metric.VALUE);
            String name = metric.getAttribute(Metric.NAME);
            System.out.println("Callback called for metric "
                    + name + ", value = " + value);
        } catch (Throwable e) {
            System.err.println("error" + e);
            e.printStackTrace(System.err);
        }
        // Keep the callback.
        return true;
    }
    
    private static String getPassphrase() {
        JPasswordField pwd = new JPasswordField();
        Object[] message = { "grid-proxy-init\nPlease enter your passphrase.",
                pwd };
        JOptionPane.showMessageDialog(null, message, "Grid-Proxy-Init",
                JOptionPane.QUESTION_MESSAGE);
        return new String(pwd.getPassword());
    }

}
