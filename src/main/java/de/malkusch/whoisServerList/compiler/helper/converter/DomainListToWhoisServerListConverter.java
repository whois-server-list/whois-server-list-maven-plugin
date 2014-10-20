package de.malkusch.whoisServerList.compiler.helper.converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

/**
 * Converts a list of domains into a list of whois servers.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class DomainListToWhoisServerListConverter
        implements Converter<List<? extends Domain>, List<WhoisServer>> {

    @Override
    public List<WhoisServer> convert(final List<? extends Domain> domains) {
        if (domains == null) {
            return null;

        }
        List<WhoisServer> servers = new ArrayList<>();
        for (Domain domain : domains) {
            List<WhoisServer> domainServers = domain.getWhoisServers();
            if (domainServers == null) {
                continue;

            }
            servers.addAll(domainServers);

        }
        return servers;
    }

}
