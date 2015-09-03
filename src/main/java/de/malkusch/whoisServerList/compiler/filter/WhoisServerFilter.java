package de.malkusch.whoisServerList.compiler.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.cache.Cache;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * The timeout in seconds.
     */
    private final int timeout;

    /**
     * Removes invalid patterns.
     */
    private final WhoisServerResponseFindPatternFilter findPatternFilter;

    /**
     * Removes invalid patterns.
     */
    private final WhoisServerResponseInvalidPatternFilter invalidPatternFilter;

    /**
     * The query cache.
     */
    private final Cache<String, String> cache;

    /**
     * One second in milliseconds.
     */
    private static final int SECOND = 1000;

    /**
     * The logger.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(WhoisServerFilter.class);

    /**
     * Sets the unavailable query and the timeout.
     *
     * @param unavailableQuery  the unavailable query, not null
     * @param timeout           the timeout in seconds
     * @param patterns          the existing unavailable patterns, not null
     * @param cache             the query cache, not null
     */
    WhoisServerFilter(
            @Nonnull final String unavailableQuery, final int timeout,
            @Nonnull final List<Pattern> patterns,
            @Nonnull final Cache<String, String> cache) {

        this.unavailableQuery = unavailableQuery;
        this.timeout = timeout;

        this.invalidPatternFilter
                = new WhoisServerResponseInvalidPatternFilter();

        this.findPatternFilter
                = new WhoisServerResponseFindPatternFilter(patterns);

        this.cache = cache;
    }
    
    @Override
    @Nullable
    public WhoisServer filter(@Nullable final WhoisServer server) {
        if (server == null || server.getHost() == null) {
            return null;

        }

        String response = getResponse(server);
        if (response == null) {
            LOGGER.warn(
                "removing inaccessible whois server '{}'", server.getHost());
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
     * The response is cached in {@link WhoisServerFilter#cache}. This cache
     * ignores the query. I.e. different queries on the same server will return
     * the same response as they share the cached response.
     *
     * @param server  the whois server
     * @return the whois response
     */
    private String getResponse(final WhoisServer server) {
        String response = cache.get(server.getHost());
        if (response != null) {
            return response;

        }
        WhoisClient whoisClient = new WhoisClient();
        whoisClient.setDefaultTimeout(timeout * SECOND);
        whoisClient.setConnectTimeout(timeout * SECOND);
        try {
            whoisClient.connect(server.getHost());
            whoisClient.setSoTimeout(timeout * SECOND);

        } catch (IOException e) {
            return null;

        }
        try (InputStream stream =
                    whoisClient.getInputStream(IDN.toASCII(unavailableQuery))) {

            response = IOUtils.toString(stream);
            cache.put(server.getHost(), response);
            return response;

        } catch (IOException e) {
            return null;

        } finally {
            try {
                whoisClient.disconnect();

            } catch (IOException e) {
                LOGGER.warn(
                        "failed disconnecting server '{}': {}",
                        server.getHost(), e);
            }
        }
    }

}
