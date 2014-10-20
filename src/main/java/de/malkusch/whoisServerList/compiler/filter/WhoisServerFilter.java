package de.malkusch.whoisServerList.compiler.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
     * Removes invalid patterns
     */
    private final WhoisServerResponseFindPatternFilter findPatternFilter;
    
    /**
     * Removes invalid patterns
     */
    private final WhoisServerResponseInvalidPatternFilter invalidPatternFilter;

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
     */
    WhoisServerFilter(
            @Nonnull final String unavailableQuery, final int timeout,
            @Nonnull final List<Pattern> patterns) {

        this.unavailableQuery = unavailableQuery;
        this.timeout = timeout;
        
        this.invalidPatternFilter
                = new WhoisServerResponseInvalidPatternFilter();
        
        this.findPatternFilter
                = new WhoisServerResponseFindPatternFilter(patterns);
    }

    @Override
    @Nullable
    public WhoisServer filter(@Nullable final WhoisServer server) {
        if (server == null) {
            return null;

        }

        WhoisClient whoisClient = new WhoisClient();
        whoisClient.setDefaultTimeout(timeout * SECOND);
        whoisClient.setConnectTimeout(timeout * SECOND);
        try {
            whoisClient.connect(server.getHost());
            
        } catch (IOException e) {
            LOGGER.warn(
                "removing inaccessible whois server '{}'",server.getHost());
            return null;
        }

        try (InputStream stream
                = whoisClient.getInputStream(unavailableQuery)) {

            whoisClient.setSoTimeout(timeout * SECOND);

            WhoisServer filtered = server.clone();
            String response = IOUtils.toString(stream);

            // Might use a filter chain
            filtered = invalidPatternFilter.filter(filtered, response);
            filtered = findPatternFilter.filter(filtered, response);
            
            return filtered;

        } catch (IOException e) {
            LOGGER.warn(
                "Removing inaccessible whois server '{}'", server.getHost());
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
