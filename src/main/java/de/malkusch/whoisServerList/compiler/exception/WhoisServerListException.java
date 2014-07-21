package de.malkusch.whoisServerList.compiler.exception;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

/**
 * Exception.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public class WhoisServerListException extends Exception {

    /**
     * Version number.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = -5796617137181502400L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message  the message
     */
    public WhoisServerListException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a cause.
     *
     * @param cause  the cause
     */
    public WhoisServerListException(final Throwable cause) {
        super(cause);
    }

}
