package de.malkusch.whoisServerList.compiler.filter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Filter remote services by establishing a TCP connection.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class TCPServiceFilter implements Filter<String> {

    /**
     * The TCP port.
     */
    private final int port;

    /**
     * The timeout in seconds.
     */
    private final int timeout;

    /**
     * Milliseconds for one second.
     */
    private static final int SECOND = 1000;

    /**
     * Sets the TCP port.
     *
     * @param port     the TCP port
     * @param timeout  the timeout in seconds
     */
    TCPServiceFilter(final int port, final int timeout) {
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public String filter(@Nullable final String host) {
        if (host == null) {
            return null;

        }
        InetSocketAddress address = new InetSocketAddress(host, port);
        if (address.isUnresolved()) {
            return null;

        }
        try (Socket socket = new Socket()) {
            socket.connect(address, timeout * SECOND);
            return host;

        } catch (IOException e) {
            return null;

        }
    }

}
