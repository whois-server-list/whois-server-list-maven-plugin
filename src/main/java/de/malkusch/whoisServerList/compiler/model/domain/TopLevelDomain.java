package de.malkusch.whoisServerList.compiler.model.domain;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Top level domain.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class TopLevelDomain extends Domain {
	
    /**
     * The registration service.
     */
	private URL registrationService;

	/**
	 * The sub domains.
	 */
	private List<Domain> domains = new ArrayList<>();

	/**
	 * Returns the sub domains.
	 *
	 * @return the sub domains, not null
	 */
	public List<Domain> getDomains() {
		return domains;
	}

	/**
	 * Sets the sub domains.
	 *
	 * @param domains  the sub domains, not null
	 */
	public void setDomains(final List<Domain> domains) {
		this.domains = domains;
	}

	/**
	 * Returns the URL of the registration service.
	 *
	 * @return the registration service, maybe null
	 */
	public URL getRegistratonService() {
		return registrationService;
	}

	/**
	 * Sets the URL of the registration service.
	 *
	 * @param registrationService the registration service, may be null
	 */
	public void setRegistratonService(URL registrationService) {
		this.registrationService = registrationService;
	}

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
	
}
