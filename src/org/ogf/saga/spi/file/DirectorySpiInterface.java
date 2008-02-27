package org.ogf.saga.spi.file;

import org.ogf.saga.URL;
import org.ogf.saga.error.AlreadyExistsException;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.DoesNotExistException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.IncorrectURLException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.file.Directory;
import org.ogf.saga.file.File;
import org.ogf.saga.file.FileInputStream;
import org.ogf.saga.file.FileOutputStream;
import org.ogf.saga.namespace.NSDirectory;
import org.ogf.saga.spi.namespace.NSDirectorySpiInterface;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public interface DirectorySpiInterface extends NSDirectorySpiInterface {
    // Inspection methods

    /**
     * Returns the number of bytes in the specified file.
     * @param name name of file to inspect.
     * @param flags mode for operation.
     * @return the size.
     */
    long getSize(URL name, int flags)
        throws NotImplementedException, IncorrectURLException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, DoesNotExistException, TimeoutException, NoSuccessException;
  
    /**
     * Tests the name for being a directory entry.
     * Is an alias for {@link NSDirectory#isEntry}.
     * @param name to be tested.
     * @return <code>true</code> if the name represents a non-directory entry.
     */
    boolean isFile(URL name)
        throws NotImplementedException, IncorrectURLException, DoesNotExistException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException;

    // openDirectory and openFile: names changed with respect
    // to specs  because of Java restriction: cannot redefine methods with
    // just a different return type.
    // Thus, they don't hide the methods in NamespaceDirectory, but then,
    // the ones in the SAGA spec don't either, because they have different
    // out parameters.

    /**
     * Creates a new <code>Directory</code> instance.
     * @param name directory to open.
     * @param flags defining the operation modus.
     * @return the opened directory instance.
     */
    Directory openDirectory(URL name, int flags)
        throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException,
            TimeoutException, NoSuccessException;

    /**
     * Creates a new <code>File</code> instance.
     * @param name file to open.
     * @param flags defining the operation modus.
     * @return the opened file instance.
     */
    File openFile(URL name, int flags)
        throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException,
            TimeoutException, NoSuccessException;

    /**
     * Creates a new <code>FileInputStream</code> instance.
     * @param name file to open.
     * @return the input stream.
     */
    FileInputStream openFileInputStream(URL name)
        throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException,
            TimeoutException, NoSuccessException;
 
    /**
     * Creates a new <code>FileOutputStream</code> instance.
     * @param name file to open.
     * @param append when set, the stream appends to the file.
     * @return the output stream.
     */
    FileOutputStream openFileOutputStream(URL name, boolean append)
        throws NotImplementedException, IncorrectURLException,
            AuthenticationFailedException, AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, AlreadyExistsException, DoesNotExistException,
            TimeoutException, NoSuccessException;
    
    //
    // Task versions
    //

    /**
     * Creates a task that retrieves the number of bytes in the specified file.
     * @param mode the task mode.
     * @param name name of file to inspect.
     * @param flags mode for operation.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    Task<Long> getSize(TaskMode mode, URL name, int flags)
        throws NotImplementedException;
       
    /**
     * Creates a task that tests the name for being a directory entry.
     * Is an alias for {@link NSDirectory#isEntry}.
     * @param mode the task mode.
     * @param name to be tested.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    Task<Boolean> isFile(TaskMode mode, URL name)
        throws NotImplementedException;

    /**
     * Creates a task that creates a new <code>Directory</code> instance.
     * @param mode the task mode.
     * @param name directory to open.
     * @param flags defining the operation modus.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    Task<Directory> openDirectory(TaskMode mode, URL name,
            int flags)
        throws NotImplementedException;
    
    /**
     * Creates a task that creates a new <code>File</code> instance.
     * @param mode the task mode.
     * @param name file to open.
     * @param flags defining the operation modus.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    Task<File> openFile(TaskMode mode, URL name, int flags)
        throws NotImplementedException;
       
    /**
     * Creates a task that creates a new <code>FileInputStream</code> instance.
     * @param mode the task mode.
     * @param name file to open.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    Task<FileInputStream> openFileInputStream(TaskMode mode, URL name)
        throws NotImplementedException;
    
    /**
     * Creates a task that creates a new <code>FileOutputStream</code> instance.
     * @param mode the task mode.
     * @param name file to open.
     * @param append when set, the file is opened for appending.
     * @return the task.
     * @exception NotImplementedException is thrown when the task version of this
     *     method is not implemented.
     */
    Task<FileOutputStream> openFileOutputStream(TaskMode mode, URL name, boolean append)
        throws NotImplementedException;    
}
