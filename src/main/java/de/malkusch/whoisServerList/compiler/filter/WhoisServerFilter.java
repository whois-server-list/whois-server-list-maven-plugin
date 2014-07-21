package de.malkusch.whoisServerList.compiler.filter;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
     * Sets the timeout.
     *
     * @param timeout  the timeout in seconds
     */
    public WhoisServerFilter(final int timeout) {
        this.tcpServiceFilter
                = new TCPServiceFilter(WhoisServer.DEFAULT_PORT, timeout);
    }

    @Override
    public boolean isValid(@Nullable WhoisServer server) {
        if (server == null) {
            return false;

        }
        return tcpServiceFilter.isValid(server.getHost());
    }

}
