package de.malkusch.whoisServerList.compiler.filter;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;

/**
 * Filter which doesn't accept unreachable whois server.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class WhoisServerFilter implements Filter<WhoisServer> {

    /**
     * The TCP filter.
     */
    private final TCPServiceFilter tcpServiceFilter;

    /**
     * The logger.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(WhoisServerFilter.class);

    /**
     * Sets the timeout.
     *
     * @param timeout  the timeout in seconds
     */
    WhoisServerFilter(final int timeout) {
        this.tcpServiceFilter
                = new TCPServiceFilter(WhoisServer.DEFAULT_PORT, timeout);
    }

    @Override
    @Nullable public WhoisServer filter(@Nullable final WhoisServer server) {
        if (server == null) {
            return null;

        }
        String host = tcpServiceFilter.filter(server.getHost());
        if (host == null) {
            LOGGER.warn("removing whois server {}.", server.getHost());
            return null;

        }

        return server;
    }

}
