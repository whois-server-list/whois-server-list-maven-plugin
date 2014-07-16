package de.malkusch.whoisServerList.compiler.test;

import java.util.Collection;
import java.util.List;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

public final class TestUtil {

    private TestUtil() {
    }

    public static Domain buildSimpleDomain(final String name) {
        Domain domain = new Domain();
        domain.setName(name);
        return domain;
    }

    public static WhoisServer find(List<WhoisServer> servers, String host) {
        for (WhoisServer server : servers) {
            if (server.getHost().equalsIgnoreCase(host)) {
                return server;

            }
        }
        return null;
    }

    public static <T extends Domain> T find(
            Collection<T> domains, String name) {

        for (T domain : domains) {
            if (domain.getName().equalsIgnoreCase(name)) {
                return domain;

            }
        }
        return null;
    }

    public static TopLevelDomain buildSimpleTld(final String name) {
        TopLevelDomain domain = new TopLevelDomain();
        domain.setName(name);
        return domain;
    }

    public static CountryCodeTopLevelDomain buildSimpleCcTld(
            final String name, final String countryCode) {

        CountryCodeTopLevelDomain domain = new CountryCodeTopLevelDomain();
        domain.setName(name);
        domain.setCountryCode(countryCode);
        return domain;
    }

}
