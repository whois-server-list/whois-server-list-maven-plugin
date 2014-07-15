package de.malkusch.whoisServerList.compiler.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Domain utility class.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class DomainUtil {
	
    /**
     * All country codes.
     */
	private final static Set<String> countries;
	
	/**
	 * Initializes the country codes.
	 */
	static {
		countries = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
	}

	/**
	 * Private utility constructor.
	 */
	private DomainUtil() {
	}

	/**
	 * Return the canonical domain name.
	 *
	 * I.e. return the lower case domain name.
	 *
	 * @param name  the domain name, null returns null
	 * @return the canonical domain name, or null
	 */
	static public String normalize(final String name) {
		return name != null ? name.toLowerCase() : null;
	}
	
	/**
	 * Whether a code is a country code.
	 *
	 * @param name the case insensitive country code, null returns false
	 * @return {@code} true if code is a country code
	 */
	static public boolean isCountryCode(final String code) {
		if (code == null) {
			return false;
			
		}
		return countries.contains(code.toUpperCase());
	}
	
}
