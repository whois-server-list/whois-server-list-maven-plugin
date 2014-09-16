package de.malkusch.whoisServerList.compiler.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

/**
 * Removes invalid available patterns.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
@Deprecated
final class WhoisServerPatternFilter implements Filter<WhoisServer> {

    /**
     * A whois query for an unavailable object.
     */
    private final String unavailableQuery;

    /**
     * The timeout in seconds.
     */
    private final int timeout;

    /**
     * One second in milliseconds.
     */
    private static final int SECOND = 1000;

    /**
     * The logger.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(WhoisServerPatternFilter.class);

    /**
     * Sets the unavailable query and the timeout.
     *
     * @param unavailableQuery  the unavailable query, not null
     * @param timeout           the timeout in seconds
     */
    WhoisServerPatternFilter(
            @Nonnull final String unavailableQuery, final int timeout) {

        this.unavailableQuery = unavailableQuery;
        this.timeout = timeout;
    }

    @Override
    @Nullable
    public WhoisServer filter(@Nullable final WhoisServer server) {
        if (server == null) {
            return null;

        }
        if (server.getAvailablePattern() == null) {
            return server;

        }

        WhoisClient whoisClient = new WhoisClient();
        whoisClient.setDefaultTimeout(timeout * SECOND);
        try {
            whoisClient.connect(server.getHost());
        } catch (IOException e) {
            LOGGER.warn("Couldn't query whois server '{}'", server.getHost());
            return server;
        }

        try (InputStream stream
                = whoisClient.getInputStream(unavailableQuery)) {

            whoisClient.setSoTimeout(timeout * SECOND);

            WhoisServer filtered = server.clone();
            String response = IOUtils.toString(stream);

            Matcher matcher = server.getAvailablePattern().matcher(response);
            if (!matcher.find()) {
                LOGGER.warn(
                    "removing available pattern from '{}'", server.getHost());
                filtered.setAvailablePattern(null);

            }

            return filtered;

        } catch (IOException e) {
            LOGGER.warn("Couldn't query whois server '{}'", server.getHost());
            return server;
        }

    }

}
