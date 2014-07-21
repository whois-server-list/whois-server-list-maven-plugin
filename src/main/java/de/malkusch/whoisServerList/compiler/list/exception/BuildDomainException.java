package de.malkusch.whoisServerList.compiler.list.exception;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

/**
 * Exception during domain construction.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public class BuildDomainException extends WhoisServerListException {

    /**
     * Version number.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = -5395605433272577428L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message  the message
     */
    public BuildDomainException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a cause.
     *
     * @param cause  the cause
     */
    public BuildDomainException(final Throwable cause) {
        super(cause);
    }

}
