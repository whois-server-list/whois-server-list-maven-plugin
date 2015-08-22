package de.malkusch.whoisServerList.compiler.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.malkusch.whoisServerList.publicSuffixList.util.PunycodeAutoDecoder;

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
    private static final Set<String> COUNTRIES;

    /**
     * Initializes the country codes.
     */
    static {
        COUNTRIES = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
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
    public static String normalize(final String name) {
        PunycodeAutoDecoder decoder = new PunycodeAutoDecoder();
        return StringUtils.lowerCase(decoder.decode(name));
    }

    /**
     * Whether a code is a country code.
     *
     * @param code the case insensitive country code, null returns false
     * @return {@code} true if code is a country code
     */
    public static boolean isCountryCode(final String code) {
        if (code == null) {
            return false;

        }
        return COUNTRIES.contains(code.toUpperCase());
    }

}
