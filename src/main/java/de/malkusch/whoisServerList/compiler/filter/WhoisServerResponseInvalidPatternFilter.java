package de.malkusch.whoisServerList.compiler.filter;

import java.util.regex.Matcher;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
final class WhoisServerResponseInvalidPatternFilter
        implements WhoisServerResponseFilter {

    /**
     * The logger.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(WhoisServerResponseInvalidPatternFilter.class);

    @Override
    @Nullable
    public WhoisServer filter(
            @Nullable final WhoisServer server, final String response) {
        if (server == null) {
            return null;

        }
        if (server.getAvailablePattern() == null) {
            return server;

        }

        WhoisServer filtered = server.clone();
        
        Matcher matcher = server.getAvailablePattern().matcher(response);
        if (!matcher.find()) {
            LOGGER.warn(
                "removing available pattern from '{}'", server.getHost());
            filtered.setAvailablePattern(null);

        }

        return filtered;
    }

}
