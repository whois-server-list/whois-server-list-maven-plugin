package de.malkusch.whoisServerList.compiler.filter;

import java.io.InputStream;
import java.net.IDN;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisApi.WhoisApi;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

/**
 * Removes unavailable whois server and apply all pattern filters.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class WhoisServerFilter implements Filter<WhoisServer> {

    /**
     * A whois query for an unavailable object.
     */
    private final String unavailableQuery;

    /**
     * Removes invalid patterns.
     */
    private final WhoisServerResponseFindPatternFilter findPatternFilter;

    /**
     * Removes invalid patterns.
     */
    private final WhoisServerResponseInvalidPatternFilter invalidPatternFilter;

    /**
     * The whois API.
     */
    private final WhoisApi whoisApi;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WhoisServerFilter.class);

    /**
     * Sets the unavailable query and the timeout.
     *
     * @param unavailableQuery
     *            the unavailable query, not null
     * @param patterns
     *            the existing unavailable patterns, not null
     * @param whoisApi
     *            Whois API
     */
    WhoisServerFilter(@Nonnull final String unavailableQuery, @Nonnull final List<Pattern> patterns,
            @Nonnull final WhoisApi whoisApi) {

        this.unavailableQuery = unavailableQuery;
        this.invalidPatternFilter = new WhoisServerResponseInvalidPatternFilter();
        this.findPatternFilter = new WhoisServerResponseFindPatternFilter(patterns);
        this.whoisApi = whoisApi;
    }

    @Override
    @Nullable
    public WhoisServer filter(@Nullable final WhoisServer server) {
        if (server == null || server.getHost() == null) {
            return null;

        }

        String response = getResponse(server);
        if (response == null) {
            LOGGER.warn("removing inaccessible whois server '{}'", server.getHost());
            return null;

        }

        WhoisServer filtered = server.clone();

        // Might use a filter chain
        filtered = invalidPatternFilter.filter(filtered, response);
        filtered = findPatternFilter.filter(filtered, response);

        return filtered;
    }

    /**
     * Queries a whois server and returns the response.
     *
     * @param server
     *            the whois server
     * @return the whois response
     */
    private String getResponse(final WhoisServer server) {
        try (InputStream stream = whoisApi.query(server.getHost(), IDN.toASCII(unavailableQuery))) {
            return IOUtils.toString(stream);

        } catch (Exception e) {
            return null;
        }
    }

}
