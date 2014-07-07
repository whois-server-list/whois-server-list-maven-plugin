package de.malkusch.whoisServerList.compiler.model.domain;

/**
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class CountryCodeTopLevelDomain extends TopLevelDomain {

	private String countryCode;

	/**
	 * @return ISO 3166-1 tld country code
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Sets the country code
	 * 
	 * @param countryCode ISO 3166-1 tld country code
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
