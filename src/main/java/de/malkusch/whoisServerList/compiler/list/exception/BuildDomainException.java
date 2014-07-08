package de.malkusch.whoisServerList.compiler.list.exception;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

public class BuildDomainException extends WhoisServerListException {

	private static final long serialVersionUID = -5395605433272577428L;
	
	public BuildDomainException(String message) {
		super(message);
	}
	
	public BuildDomainException(Throwable cause) {
		super(cause);
	}

}
