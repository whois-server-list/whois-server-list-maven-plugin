package de.malkusch.whoisServerList.compiler.helper;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

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
     * All country codes - keys are in lower case, values are in upper case.
     */
    private static final Map<String, String> COUNTRIES;
    
    /**
     * Initializes the country codes.
     */
    static {
        COUNTRIES = Arrays.stream(Locale.getISOCountries()).collect(
                Collectors.toMap(
                    k -> k.toLowerCase(),
                    k -> k
                )
        );
        
        // exceptions
        COUNTRIES.put("uk", "GB");
        COUNTRIES.put("su", "RU");
        COUNTRIES.put("ac", "SH");
        COUNTRIES.put("eu", "EU");
        COUNTRIES.put("tp", "TL");
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
     * Maps a domain name to a country code.
     * 
     * @param domain The domain
     * @return The upper case ISO 3166 two-letter country code or null.
     */
    public static @Nullable String getCountryCode(String domain) {
        return COUNTRIES.get(domain.toLowerCase());
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
        return COUNTRIES.containsKey(code.toLowerCase());
    }

}
