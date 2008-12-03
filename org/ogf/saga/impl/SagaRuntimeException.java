package org.ogf.saga.impl;

public class SagaRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new SAGA error.
     */
    public SagaRuntimeException() {
    }

    /**
     * Constructs a new SAGA error with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public SagaRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new SAGA error with the specified cause.
     * 
     * @param cause
     *            the cause.
     */
    public SagaRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new SAGA error with the specified detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public SagaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the message as specified by the SAGA API, i.e., <exception name>:
     * <message>.
     * 
     * @return the message.
     */
    public String getMessage() {
        return this.getClass().getSimpleName() + ": " + super.getMessage();
    }
}
