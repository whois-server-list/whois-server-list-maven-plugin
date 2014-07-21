package de.malkusch.whoisServerList.compiler.list.xml;

import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;
import de.malkusch.whoisServerList.api.v0.model.Server;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.converter.Converter;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.WhoisServerBuilder;
import de.malkusch.whoisServerList.compiler.model.Source;
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
        WhoisServerBuilder factory
            = new WhoisServerBuilder(Source.XML);
        factory.setHost(xmlServer.getHost());
        WhoisServer server;
        try {
            server = factory.build();
        } catch (WhoisServerListException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        String xmlPattern = xmlServer.getAvailstring();
        if (xmlPattern != null) {
            Pattern pattern = Pattern.compile(
                    Pattern.quote(xmlPattern), Pattern.CASE_INSENSITIVE);
            server.setAvailablePattern(pattern);
        }

        return server;
    }

}
