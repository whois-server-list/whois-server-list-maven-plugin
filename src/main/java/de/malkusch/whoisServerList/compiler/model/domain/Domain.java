package de.malkusch.whoisServerList.compiler.model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;

/**
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class Domain {
	
	private String name;
	
	private Date created;
	
	private Date changed;
	
	private List<WhoisServer> whoisServers = new ArrayList<WhoisServer>();

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
	 * Returns the whois servers.
	 */
	public List<WhoisServer> getWhoisServers() {
		return whoisServers;
	}

	/**
	 * Sets the whois servers.
	 */
	public void setWhoisServers(List<WhoisServer> whoisServers) {
		this.whoisServers = whoisServers;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getChanged() {
		return changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
	}

}
