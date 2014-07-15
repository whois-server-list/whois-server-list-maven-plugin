package de.malkusch.whoisServerList.compiler.model.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Country code top level domain.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class CountryCodeTopLevelDomain extends TopLevelDomain {

    /**
     * The country code.
     */
	private String countryCode;

	/**
	 * Returns the ISO 3166-1 tld country code.
	 *
	 * @return the ISO 3166-1 tld country code, not null
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Sets the ISO 3166-1 country code.
	 *
	 * @param countryCode  the ISO 3166-1 tld country code, not null
	 */
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
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
