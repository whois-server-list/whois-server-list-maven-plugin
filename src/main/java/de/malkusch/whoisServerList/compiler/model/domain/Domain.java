package de.malkusch.whoisServerList.compiler.model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;

/**
 * Domain.
 * 
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class Domain {
	
    /**
     * The domain name.
     */
	private String name;
	
	/**
	 * The created date, may be null.
	 */
	private Date created;
	
	/**
	 * The changed date, may be null.
	 */
	private Date changed;
	
	/**
	 * The domain state, may be null.
	 */
	private State state;
	
	/**
	 * Domain state.
	 * 
	 * @author markus@malkusch.de
	 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
	 */
	public static enum State {
		ACTIVE, INACTIVE, NEW;
	}
	
	/**
	 * The whois servers for this domain, may be empty.
	 */
	private List<WhoisServer> whoisServers = new ArrayList<WhoisServer>();

	/**
	 * Returns the domain name.
	 * 
	 * @return the domain name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the domain name.
	 * 
	 * @param name  the domain name
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the whois servers.
	 * 
	 * @return the whois servers
	 */
	public List<WhoisServer> getWhoisServers() {
		return whoisServers;
	}

	/**
	 * Sets the whois servers.
	 * 
	 * @param  the whois servers
	 */
	public void setWhoisServers(final List<WhoisServer> whoisServers) {
		this.whoisServers = whoisServers;
	}

	/**
	 * Returns the created date.
	 * 
	 * @return the created date, or null
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * Sets the created date.
	 * 
	 * @param created the created date, may be null
	 */
	public void setCreated(final Date created) {
		this.created = created;
	}

	/**
	 * Returns the changed date.
	 * 
	 * @return the changed date, or null
	 */
	public Date getChanged() {
		return changed;
	}

	/**
	 * Sets the changed date.
	 * 
	 * @param changed  the changed date, or null
	 */
	public void setChanged(final Date changed) {
		this.changed = changed;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * Gets the domain state.
	 * 
	 * @return the domain state, or null
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the domain state.
	 * 
	 * @param state  the domain state, or null
	 */
	public void setState(State state) {
		this.state = state;
	}
	
}
