package com.sm.engine.exception;

/**
 * This is exception is thrown if there is no document with given criteria.
 */
public class DocumentNotFoundException extends NotFoundException {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 6981076108348619981L;

    /**
     * The message format.
     */
    private static final String messageFormat = "Document with ID = %s does not exist";

    /**
     * This is the constructor of <code>DocumentNotFoundException</code> class with document id argument.
     *
     * @param id the document id
     */
    public DocumentNotFoundException(String id) {
        super(String.format(messageFormat, id));
    }

    /**
     * This is the constructor of <code>DocumentNotFoundException</code> class with document id and cause
     * arguments.
     *
     * @param documentId the document id
     * @param cause      the cause of the exception
     */
    public DocumentNotFoundException(String documentId, Throwable cause) {
        super(String.format(messageFormat, documentId), cause);
    }
}
