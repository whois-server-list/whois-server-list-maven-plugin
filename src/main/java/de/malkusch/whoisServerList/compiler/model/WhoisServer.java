package de.malkusch.whoisServerList.compiler.model;

import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Whois server.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class WhoisServer {

    /**
     * The whois default port.
     */
    public static final int DEFAULT_PORT = 43;

    /**
     * The host.
     */
    private String host;

    /**
     * The pattern for available domains.
     * This may be null.
     */
    private Pattern availablePattern;

    /**
     * Returns the whois server.
     *
     * @return the host, not null
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the whois server.
     *
     * @param host  the server, not null
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * Returns the pattern for checking the server response
     * for an available domain.
     *
     * @return the available patter, may be null
     */
    public Pattern getAvailablePattern() {
        return availablePattern;
    }

    /**
     * Sets the pattern for checking the server response for
     * an available domain.
     *
     * @param availablePattern the available pattern, maybe null
     */
    public void setAvailablePattern(final Pattern availablePattern) {
        this.availablePattern = availablePattern;
    }

    @Override
    public String toString() {
        return getHost();
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
