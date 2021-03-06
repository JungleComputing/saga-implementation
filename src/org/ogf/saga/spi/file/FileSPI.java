package org.ogf.saga.spi.file;

import java.util.List;

import org.ogf.saga.buffer.Buffer;
import org.ogf.saga.error.AuthenticationFailedException;
import org.ogf.saga.error.AuthorizationFailedException;
import org.ogf.saga.error.BadParameterException;
import org.ogf.saga.error.IncorrectStateException;
import org.ogf.saga.error.NoSuccessException;
import org.ogf.saga.error.NotImplementedException;
import org.ogf.saga.error.PermissionDeniedException;
import org.ogf.saga.error.SagaIOException;
import org.ogf.saga.error.TimeoutException;
import org.ogf.saga.file.File;
import org.ogf.saga.file.IOVec;
import org.ogf.saga.file.SeekMode;
import org.ogf.saga.spi.namespace.NSEntrySPI;
import org.ogf.saga.task.Task;
import org.ogf.saga.task.TaskMode;

public interface FileSPI extends NSEntrySPI {

    /**
     * Returns the number of bytes in the file.
     * 
     * @return the size.
     */
    long getSize() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException;

    // POSIX-like I/O

    /**
     * Reads up to <code>len</code> bytes from the file into the buffer.
     * Returns the number of bytes read, or 0 at end-of-file. Note: this call is
     * blocking. The async version can be used to implement non-blocking reads.
     * 
     * @param len
     *            the number of bytes to be read.
     * @param offset
     *            the buffer offset.
     * @param buffer
     *            the buffer to read data into.
     * @return the number of bytes read.
     */
    int read(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException;

    /**
     * Writes up to <code>len</code> bytes from the buffer to the file at the
     * current file position. Returns the number of bytes written.
     * 
     * @param len
     *            the number of bytes to be written.
     * @param offset
     *            the buffer offset.
     * @param buffer
     *            the buffer to write data from.
     * @return the number of bytes written.
     */
    int write(Buffer buffer, int offset, int len)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException;

    /**
     * Repositions the current file position as requested.
     * 
     * @param offset
     *            offset in bytes to move pointer.
     * @param whence
     *            determines from where the offset is relative.
     * @return the position after the seek.
     */
    long seek(long offset, SeekMode whence) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException, SagaIOException;

    // Scattered I/O

    /**
     * Gather/scatter read.
     * 
     * @param iovecs
     *            array of IOVecs determining how much to read and where to
     *            store it.
     */
    void readV(IOVec[] iovecs) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException;

    /**
     * Gather/scatter write.
     * 
     * @param iovecs
     *            array of IOVecs determining how much to write and where to
     *            obtain the data from.
     */
    void writeV(IOVec[] iovecs) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException;

    // Pattern-based I/O

    /**
     * Determines the storage size required for a pattern I/O operation.
     * 
     * @param pattern
     *            to determine size for.
     * @return the size.
     */
    int sizeP(String pattern) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException;

    /**
     * Pattern-based read.
     * 
     * @param pattern
     *            specification for the read operation.
     * @param buffer
     *            to store data into.
     * @return number of succesfully read bytes.
     */
    int readP(String pattern, Buffer buffer) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException;

    /**
     * Pattern-based write.
     * 
     * @param pattern
     *            specification for the write operation.
     * @param buffer
     *            to be written.
     * @return number of succesfully written bytes.
     */
    int writeP(String pattern, Buffer buffer) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, BadParameterException,
            IncorrectStateException, TimeoutException, NoSuccessException,
            SagaIOException;

    // extended I/O

    /**
     * Lists the extended modes available in this implementation and/or on the
     * server side.
     * 
     * @return list of available modes.
     */
    List<String> modesE() throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            PermissionDeniedException, IncorrectStateException,
            TimeoutException, NoSuccessException;

    /**
     * Determines the storage size required for an extended I/O operation.
     * 
     * @param emode
     *            extended mode to use.
     * @param spec
     *            to determine size for.
     * @return the size.
     */
    int sizeE(String emode, String spec) throws NotImplementedException,
            AuthenticationFailedException, AuthorizationFailedException,
            IncorrectStateException, PermissionDeniedException,
            BadParameterException, TimeoutException, NoSuccessException;

    /**
     * Extended read.
     * 
     * @param emode
     *            extended mode to use.
     * @param spec
     *            specification of read operation.
     * @param buffer
     *            to store the data read.
     * @return the number of successfully read bytes.
     */
    int readE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException;

    /**
     * Extended write.
     * 
     * @param emode
     *            extended mode to use.
     * @param spec
     *            specification of write operation.
     * @param buffer
     *            data to write.
     * @return the number of successfully written bytes.
     */
    int writeE(String emode, String spec, Buffer buffer)
            throws NotImplementedException, AuthenticationFailedException,
            AuthorizationFailedException, PermissionDeniedException,
            BadParameterException, IncorrectStateException, TimeoutException,
            NoSuccessException, SagaIOException;

    //
    // Task versions ..
    //

    // Inspection

    /**
     * Creates a task that obtains the number of bytes in the file. This is the
     * task version.
     * 
     * @param mode
     *            the task mode.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Long> getSize(TaskMode mode) throws NotImplementedException;

    // POSIX-like I/O

    /**
     * Creates a task that reads up to <code>len</code> bytes from the file
     * into the buffer. The number returned by the task is the number of bytes
     * read, or 0 at end-of-file.
     * 
     * @param mode
     *            the task mode.
     * @param offset
     *            the buffer offset.
     * @param len
     *            the number of bytes to be read.
     * @param buffer
     *            the buffer to read data into.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Integer> read(TaskMode mode, Buffer buffer, int offset, int len)
            throws NotImplementedException;

    /**
     * Creates a task that writes up to <code>len</code> bytes from the buffer
     * to the file at the current file position. The number returned by the task
     * is the number of bytes written.
     * 
     * @param mode
     *            the task mode.
     * @param offset
     *            the buffer offset.
     * @param len
     *            the number of bytes to be written.
     * @param buffer
     *            the buffer to write data from.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Integer> write(TaskMode mode, Buffer buffer, int offset, int len)
            throws NotImplementedException;

    /**
     * Creates a task that repositions the current file position as requested.
     * The number returned by the task is the new file position.
     * 
     * @param mode
     *            the task mode.
     * @param offset
     *            offset in bytes to move pointer.
     * @param whence
     *            determines from where the offset is relative.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Long> seek(TaskMode mode, long offset, SeekMode whence)
            throws NotImplementedException;

    // Scattered I/O

    /**
     * Creates a task that does a gather/scatter read.
     * 
     * @param mode
     *            the task mode.
     * @param iovecs
     *            array of IOVecs determining how much to read and where to
     *            store it.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Void> readV(TaskMode mode, IOVec[] iovecs)
            throws NotImplementedException;

    /**
     * Creates a task that does a gather/scatter write.
     * 
     * @param mode
     *            the task mode.
     * @param iovecs
     *            array of IOVecs determining how much to write and where to
     *            obtain the data from.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Void> writeV(TaskMode mode, IOVec[] iovecs)
            throws NotImplementedException;

    // Pattern-based I/O

    /**
     * Creates a task that determines the storage size required for a pattern
     * I/O operation.
     * 
     * @param mode
     *            the task mode.
     * @param pattern
     *            to determine size for.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Integer> sizeP(TaskMode mode, String pattern)
            throws NotImplementedException;

    /**
     * Creates a task that does a pattern-based read.
     * 
     * @param mode
     *            the task mode.
     * @param pattern
     *            specification for the read operation.
     * @param buffer
     *            to store data into.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    public Task<File, Integer> readP(TaskMode mode, String pattern,
            Buffer buffer) throws NotImplementedException;

    /**
     * Creates a task that does a pattern-based write.
     * 
     * @param mode
     *            the task mode.
     * @param pattern
     *            specification for the write operation.
     * @param buffer
     *            to be written.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Integer> writeP(TaskMode mode, String pattern, Buffer buffer)
            throws NotImplementedException;

    // extended I/O

    /**
     * Creates a task that lists the extended modes available in this
     * implementation and/or on the server side.
     * 
     * @param mode
     *            the task mode.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, List<String>> modesE(TaskMode mode)
            throws NotImplementedException;

    /**
     * Creates a task that determines the storage size required for an extended
     * I/O operation.
     * 
     * @param mode
     *            the task mode.
     * @param emode
     *            extended mode to use.
     * @param spec
     *            to determine size for.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Integer> sizeE(TaskMode mode, String emode, String spec)
            throws NotImplementedException;

    /**
     * Creates a task for an extended read.
     * 
     * @param mode
     *            the task mode.
     * @param emode
     *            extended mode to use.
     * @param spec
     *            specification of read operation.
     * @param buffer
     *            to store the data read.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Integer> readE(TaskMode mode, String emode, String spec,
            Buffer buffer) throws NotImplementedException;

    /**
     * Creates a task for an extended write.
     * 
     * @param mode
     *            the task mode.
     * @param emode
     *            extended mode to use.
     * @param spec
     *            specification of write operation.
     * @param buffer
     *            data to write.
     * @return the task.
     * @exception NotImplementedException
     *                is thrown when the task version of this method is not
     *                implemented.
     */
    Task<File, Integer> writeE(TaskMode mode, String emode, String spec,
            Buffer buffer) throws NotImplementedException;
}
