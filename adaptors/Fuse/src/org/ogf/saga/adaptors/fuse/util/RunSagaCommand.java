package org.ogf.saga.adaptors.fuse.util;

import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunSagaCommand {

    private static Logger logger = LoggerFactory.getLogger(RunSagaCommand.class);
    
    public static void execute(String cmd) throws PermissionDeniedException,
            TimeoutException, NoSuccessException {
    	execute(cmd, null);
    }
    
	public static void execute(String cmd, String input)
			throws PermissionDeniedException, TimeoutException,
			NoSuccessException {
		
        String[] cmdParts = cmd.split(" ");
        
        RunProcess proc = new RunProcess(cmdParts);
        proc.setRedirectErrorStream(true);
        proc.setStdin(input);
        
        logger.debug("Executing " + proc.command());
        proc.run();

        String output = new String(proc.getStdout());

        if (logger.isDebugEnabled()) {
            if (output.length() > 0) {
                logger.debug(output);
            }
        }

        // convert POSIX error codes to SAGA exceptions
        int exitStatus = proc.getExitStatus();

        if (exitStatus != 0) {
            logger.info("Execution failed with exit code: " + exitStatus);
            
            if (output.length() > 0) {
                // try to guess the SAGA exception from the output
                guessSagaException(output);
            }
            
            // guessing failed; try examining the exit code
            
            switch (exitStatus) {
            case 0: // EOK, do nothing
                return;
            case -1: // creating process failed
            case 1: // EPERM, Operation not permitted
                if (output.length() > 0) {
                    throw new NoSuccessException(output);
                }
                throw new NoSuccessException("unknown reason");
            case 5: { // EIO, I/O error
                String s = createErrorMessage("I/O error", output);
                throw new NoSuccessException(s);
            }
            case 13: { // EACCES, Permission denied
                String s = createErrorMessage("Permission denied", output);
                throw new PermissionDeniedException(s);
            }
            case 30: { // EROFS, Read-only file system
                String s = createErrorMessage("Read-only file system", output);
                throw new PermissionDeniedException(s);
            }
            case 110: { // ETIMEDOUT, Connection timed out
                String s = createErrorMessage("Connection timed out", output);
                throw new TimeoutException(s);
            }
            case 111: { // ECONNREFUSED, Connection refused
                String s = createErrorMessage("Connection refused", output);
                throw new NoSuccessException(s);
            }
            case 112: { // EHOSTDOWN, Host is down
                String s = createErrorMessage("Host is down", output);
                throw new NoSuccessException(s);
            }
            case 113: { // EHOSTUNREACH, No route to host
                String s = createErrorMessage("No route to host", output);
                throw new NoSuccessException(s);
            }
            case 121: { // EREMOTEIO, Remote I/O error
                String s = createErrorMessage("Remote I/O error", output);
                throw new NoSuccessException(s);
            }
            default:
                String s = createErrorMessage("Command failed with exit code "
                        + exitStatus, output);
                throw new NoSuccessException(s);
            }
        }
    }

	private static void guessSagaException(String output) 
	        throws PermissionDeniedException, NoSuccessException {
	    if (output.contains("permission denied") ||
	        output.contains("Permission denied") ||
	        output.contains("Permission Denied")) {
	        throw new PermissionDeniedException(output);
	    }
	}
	
    private static String createErrorMessage(String msg, String output) {
        if (output != null && output.length() > 0) {
            return msg + ": " + output;
        }
        return msg;
    }

}
