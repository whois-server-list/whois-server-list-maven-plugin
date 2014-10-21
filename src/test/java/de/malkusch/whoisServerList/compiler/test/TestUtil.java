package de.malkusch.whoisServerList.compiler.test;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;

public final class TestUtil {

    private TestUtil() {
    }

    public static Date getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Domain buildDomain(
            final String name, final String host, final String pattern) {
        
        Domain domain = buildSimpleDomain(name);
        
        WhoisServer server = new WhoisServer();
        server.setHost(host);
        if (pattern != null) {
            server.setAvailablePattern(Pattern.compile(
                    Pattern.quote(pattern), Pattern.CASE_INSENSITIVE));
            
        }
        domain.getWhoisServers().add(server);
        
        return domain;
    }
    
    public static Domain buildSimpleDomain(final String name) {
        return buildSimpleDomain(name, Source.IANA);
    }
    
    public static Domain buildSimpleDomain(
            final String name, final Source source) {

        Domain domain = new Domain();
        domain.setName(name);
        domain.setSource(source);
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
            final Collection<T> domains, final String name) {

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
        domain.setSource(Source.XML);
        return domain;
    }

    public static TopLevelDomain buildSimpleCcTld(
            final String name, final String countryCode) {

        TopLevelDomain domain = new TopLevelDomain();
        domain.setName(name);
        domain.setCountryCode(countryCode);
        domain.setSource(Source.XML);
        return domain;
    }

}
