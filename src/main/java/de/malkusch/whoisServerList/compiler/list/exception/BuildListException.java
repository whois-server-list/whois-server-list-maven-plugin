package de.malkusch.whoisServerList.compiler.list.exception;

import java.io.Serializable;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

/**
 * Exception during list building.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class BuildListException extends WhoisServerListException {

    /**
     * Version number.
     *
     * @see Serializable
     */
	private static final long serialVersionUID = -5395605433272577428L;
	
	/**
     * Constructs a new exception with a cause.
     *
     * @param cause  the cause
     */
	public BuildListException(final Throwable cause) {
		super(cause);
	}

	/**
     * Constructs a new exception with the specified detail message.
     *
     * @param message  the message
     */
    public BuildListException(final String message) {
        super(message);
    }

}
