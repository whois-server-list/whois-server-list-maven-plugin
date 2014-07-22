package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;

public class DomainListFilterTest {

    @Test
    public void testFilter() throws InterruptedException {
        DomainList list = new DomainList();

        TopLevelDomain com = new TopLevelDomain();
        WhoisServer whoisCom = new WhoisServer();
        whoisCom.setHost("whois.verisign-grs.com");
        whoisCom.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));
        com.getWhoisServers().add(whoisCom);
        list.getDomains().add(com);

        TopLevelDomain net = new TopLevelDomain();
        WhoisServer whoisNet = new WhoisServer();
        whoisNet.setHost("whois.verisign-grs.com");
        whoisNet.setAvailablePattern(Pattern.compile(
                "invalid", Pattern.CASE_INSENSITIVE));
        net.getWhoisServers().add(whoisNet);
        list.getDomains().add(net);

        TopLevelDomain org = new TopLevelDomain();
        WhoisServer whoisOrg = new WhoisServer();
        whoisOrg.setHost("invalid.example.org");
        org.getWhoisServers().add(whoisOrg);
        list.getDomains().add(org);


        DomainListFilter filter = new DomainListFilter(5);
        DomainList filtered = filter.filter(list);

        assertEquals(com, filtered.getDomains().get(0));
        assertNull(filtered.getDomains().get(1)
                .getWhoisServers().get(0).getAvailablePattern());
        assertTrue(filtered.getDomains().get(2).getWhoisServers().isEmpty());
    }

}
