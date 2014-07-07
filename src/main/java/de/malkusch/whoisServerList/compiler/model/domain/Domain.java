package de.malkusch.whoisServerList.compiler.model.domain;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;

/**
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class Domain {
	
	private String name;
	
	private WhoisServer whoisServer;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the whois server.
	 */
	public WhoisServer getWhoisServer() {
		return whoisServer;
	}

	/**
	 * Sets the whois server.
	 */
	public void setWhoisServer(WhoisServer whoisServer) {
		this.whoisServer = whoisServer;
	}

}
