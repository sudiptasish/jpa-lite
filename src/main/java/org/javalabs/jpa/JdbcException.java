package org.javalabs.jpa;

/**
 * The JPA-LiTE framework will throw JdbcException if the query execution fails.
 *
 * <p>{@code JdbcException} and its subclasses are <em>unchecked
 * exceptions</em>.  Unchecked exceptions do <em>not</em> need to be
 * declared in a method or constructor's {@code throws} clause if they
 * can be thrown by the execution of the method or constructor and
 * propagate outside the method or constructor boundary.
 * 
 * @author Sudiptasish Chanda
 */
public class JdbcException extends RuntimeException {
    
    /** 
     * Constructs a new jdbc exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public JdbcException() {
        super();
    }

    /** 
     * Constructs a new jdbc exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public JdbcException(String message) {
        super(message);
    }

    /**
     * Constructs a new jdbc exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this jdbc exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public JdbcException(String message, Throwable cause) {
        super(message, cause);
    }

    /** 
     * Constructs a new jdbc exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for jdbc exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public JdbcException(Throwable cause) {
        super(cause);
    }
}
