package com.sm.engine.exception;

/**
 * This is exception is thrown if there is no entity found.
 */
public class NotFoundException extends RuntimeException {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 6181076108348618881L;

    /**
     * <p>
     * This is the constructor of <code>NotFoundException</code> class with message argument.
     * </p>
     *
     * @param message the error message.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * <p>
     * This is the constructor of <code>NotFoundException</code> class with message and cause arguments.
     * </p>
     *
     * @param message the error message.
     * @param cause   the cause of the exception.
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
