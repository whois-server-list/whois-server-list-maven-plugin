package de.malkusch.whoisServerList.compiler.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisApi.WhoisApi;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.helper.WhoisErrorResponseDetector;

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
     * Error detector.
     */
    private final WhoisErrorResponseDetector errorDetector;

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
     * @param errorDetector
     *            error detector
     * @param whoisApi
     *            Whois API
     */
    WhoisServerFilter(@Nonnull final String unavailableQuery, @Nonnull final List<Pattern> patterns,
            @Nonnull final WhoisErrorResponseDetector errorDetector, @Nonnull final WhoisApi whoisApi) {

        this.unavailableQuery = unavailableQuery;
        this.invalidPatternFilter = new WhoisServerResponseInvalidPatternFilter();
        this.findPatternFilter = new WhoisServerResponseFindPatternFilter(patterns);
        this.errorDetector = errorDetector;
        this.whoisApi = whoisApi;
    }

    @Override
    @Nullable
    public WhoisServer filter(@Nullable final WhoisServer server) {
        if (server == null || server.getHost() == null) {
            return null;

        }

        String response = getResponse(server, 5);
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
     * @param retries
     *            number of retries
     *
     * @return the whois response
     */
    private String getResponse(final WhoisServer server, int retries) {

        if (retries < 0) {
            return getDirectResponse(server);
        }

        try (InputStream stream = whoisApi.query(server.getHost(), IDN.toASCII(unavailableQuery))) {
            String response = IOUtils.toString(stream);

            if (errorDetector.isError(response)) {
                return getResponse(server, retries - 1);
            }

            return response;

        } catch (Exception e) {
            return getResponse(server, retries - 1);
        }
    }

    /**
     * Queries a whois server directly and returns the response.
     *
     * @param server
     *            the whois server
     *
     * @return the whois response
     */
    private String getDirectResponse(final WhoisServer server) {

        WhoisClient whoisClient = new WhoisClient();
        whoisClient.setDefaultTimeout(5000);
        whoisClient.setConnectTimeout(5000);

        try {
            whoisClient.connect(server.getHost());
            whoisClient.setSoTimeout(5000);

        } catch (IOException e) {
            return null;
        }

        try (InputStream stream = whoisClient.getInputStream(IDN.toASCII(unavailableQuery))) {
            String response = IOUtils.toString(stream);
            
            if (errorDetector.isError(response)) {
                LOGGER.warn("Server '{}' is blocking this IP address. Please check the result manually.", server.getHost());
            }
            
            return response;
            
        } catch (IOException e) {
            return null;
        }
    }

}
