package de.malkusch.whoisServerList.compiler.filter;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

/**
 * Filter a WhoisServer based on the response for an unavailable query.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@ThreadSafe
interface WhoisServerResponseFilter {

    /**
     * Returns the filtered whois server.
     *
     * @param server    the whois server, may be null
     * @param availableResponse  the response for an available query
     * @param unavailableResponse  the response for an unavailable query
     *
     * @return the filtered whois server, may be null
     */
    @Nullable WhoisServer filter(
            @Nullable WhoisServer server, String availableResponse, Optional<String> unavailableResponse);

}
