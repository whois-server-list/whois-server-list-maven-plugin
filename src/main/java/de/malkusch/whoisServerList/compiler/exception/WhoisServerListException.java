package de.malkusch.whoisServerList.compiler.exception;

public class WhoisServerListException extends Exception {

	private static final long serialVersionUID = -5796617137181502400L;
	
	public WhoisServerListException(String message) {
		super(message);
	}
	
	public WhoisServerListException(Throwable cause) {
        super(cause);
    }

}
