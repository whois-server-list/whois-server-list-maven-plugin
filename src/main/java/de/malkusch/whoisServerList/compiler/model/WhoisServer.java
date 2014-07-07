package de.malkusch.whoisServerList.compiler.model;

import java.util.regex.Pattern;

/**
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class WhoisServer {
	
	public final static int DEFAULT_PORT = 43;
	
	private String host;
	
	private Pattern availablePattern;
	
	/**
	 * Returns the whois server
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the whois server
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns the pattern for checking the server response for an available domain.
	 */
	public Pattern getAvailablePattern() {
		return availablePattern;
	}

	/**
	 * Sets the pattern for checking the server response for an available domain.
	 */
	public void setAvailablePattern(Pattern availablePattern) {
		this.availablePattern = availablePattern;
	}
	
	@Override
	public String toString() {
		return getHost();
	}

}
