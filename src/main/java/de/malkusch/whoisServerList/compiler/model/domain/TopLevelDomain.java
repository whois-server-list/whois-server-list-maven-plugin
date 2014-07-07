package de.malkusch.whoisServerList.compiler.model.domain;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class TopLevelDomain extends Domain {
	
	private URL registrationService;

	private List<Domain> domains = new ArrayList<>();

	/**
	 * Returns the second level domains.
	 * @return
	 */
	public List<Domain> getDomains() {
		return domains;
	}

	/**
	 * Sets the second level domains.
	 */
	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	/**
	 * Returns the URL of the registration service.
	 */
	public URL getRegistratonService() {
		return registrationService;
	}

	/**
	 * Sets the URL of the registration service.
	 */
	public void setRegistratonService(URL registrationService) {
		this.registrationService = registrationService;
	}
	
}
