package de.malkusch.whoisServerList.compiler.list.xml;

import java.util.regex.Pattern;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.api.v0.model.Server;
import de.malkusch.whoisServerList.compiler.helper.converter.Converter;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;

/**
 * Converts a XML Server.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class XMLServerToServerConverter
        implements Converter<Server, WhoisServer> {

    @Override
    public WhoisServer convert(final Server xmlServer) {
        WhoisServer server = new WhoisServer();

        server.setHost(xmlServer.getHost());

        String xmlPattern = xmlServer.getAvailstring();
        if (xmlPattern != null) {
            Pattern pattern
                    = Pattern.compile(Pattern.quote(xmlPattern));
            server.setAvailablePattern(pattern);
        }

        return server;
    }

}
