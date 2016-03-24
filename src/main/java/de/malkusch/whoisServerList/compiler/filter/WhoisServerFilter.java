package de.malkusch.whoisServerList.compiler.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
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
    private final Optional<String> unavailableQuery;

    /**
     * A whois query for an available object.
     */
    private final String availableQuery;

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
     * Sets the available query and the timeout.
     *
     * @param availableQuery
     *            the available query, not null
     * @param patterns
     *            the existing unavailable patterns, not null
     * @param errorDetector
     *            error detector
     * @param whoisApi
     *            Whois API
     */
    WhoisServerFilter(@Nonnull final String availableQuery, @Nonnull Optional<String> unavailableQuery,
            @Nonnull final List<Pattern> patterns, @Nonnull final WhoisErrorResponseDetector errorDetector,
            @Nonnull final WhoisApi whoisApi) {

        this.invalidPatternFilter = new WhoisServerResponseInvalidPatternFilter();
        this.findPatternFilter = new WhoisServerResponseFindPatternFilter(patterns);
        this.errorDetector = errorDetector;
        this.whoisApi = whoisApi;
        this.availableQuery = availableQuery;

        try {
            InetAddress.getByName(availableQuery);
            LOGGER.warn("{} could be resolved. I didn't expect that.", availableQuery);
            // DNS is probably spoiled:
            unavailableQuery = Optional.empty();

        } catch (UnknownHostException e) {
            // Expected
        }
        this.unavailableQuery = unavailableQuery;
    }

    @Override
    @Nullable
    public WhoisServer filter(@Nullable final WhoisServer server) {
        if (server == null || server.getHost() == null) {
            return null;

        }

        String availableResponse = getResponse(server, availableQuery, 5);
        if (availableResponse == null) {
            LOGGER.warn("removing inaccessible whois server '{}'", server.getHost());
            return null;

        }

        Optional<String> unavailableResponse = unavailableQuery
                .map(query -> getResponse(server, unavailableQuery.get(), 5));

        WhoisServer filtered = server.clone();

        // Might use a filter chain
        filtered = invalidPatternFilter.filter(filtered, availableResponse, unavailableResponse);
        filtered = findPatternFilter.filter(filtered, availableResponse, unavailableResponse);

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
    private String getResponse(final WhoisServer server, final String query, final int retries) {

        if (retries < 0) {
            return getDirectResponse(server, query);
        }

        try (InputStream stream = whoisApi.query(server.getHost(), IDN.toASCII(query))) {
            String response = IOUtils.toString(stream);

            if (errorDetector.isError(response)) {
                return getResponse(server, query, retries - 1);
            }

            return response;

        } catch (Exception e) {
            return getResponse(server, query, retries - 1);
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
    private String getDirectResponse(final WhoisServer server, final String query) {

        WhoisClient whoisClient = new WhoisClient();
        whoisClient.setDefaultTimeout(5000);
        whoisClient.setConnectTimeout(5000);

        try {
            whoisClient.connect(server.getHost());
            whoisClient.setSoTimeout(5000);

        } catch (IOException e) {
            return null;
        }

        try (InputStream stream = whoisClient.getInputStream(IDN.toASCII(query))) {
            String response = IOUtils.toString(stream);

            if (errorDetector.isError(response)) {
                LOGGER.warn("Server '{}' is blocking this IP address. Please check the result manually.",
                        server.getHost());
            }

            return response;

        } catch (IOException e) {
            return null;
        }
    }

}
