package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.test.WhoisApiRule;

public class DomainListFilterTest {

    @Rule
    public WhoisApiRule whoisApiRule = new WhoisApiRule();

    @Test
    public void testFilter() throws InterruptedException {
        DomainList list = new DomainList();

        TopLevelDomain com = new TopLevelDomain();
        com.setName("com");
        WhoisServer whoisCom = new WhoisServer();
        whoisCom.setHost("whois.verisign-grs.com");
        whoisCom.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));
        com.getWhoisServers().add(whoisCom);
        list.getDomains().add(com);

        TopLevelDomain net = new TopLevelDomain();
        net.setName("net");
        WhoisServer whoisNet = new WhoisServer();
        whoisNet.setHost("whois.verisign-grs.com");
        whoisNet.setAvailablePattern(Pattern.compile(
                "invalid", Pattern.CASE_INSENSITIVE));
        net.getWhoisServers().add(whoisNet);
        list.getDomains().add(net);

        TopLevelDomain org = new TopLevelDomain();
        org.setName("org");
        WhoisServer whoisOrg = new WhoisServer();
        whoisOrg.setHost("invalid.example.org");
        org.getWhoisServers().add(whoisOrg);
        list.getDomains().add(org);


        DomainListFilter filter = new DomainListFilter(whoisApiRule.whoisApi());

        DomainList filtered = filter.filter(list);

        assertEquals(com, filtered.getDomains().get(0));
        assertEquals("no match for", filtered.getDomains().get(1)
                .getWhoisServers().get(0).getAvailablePattern().toString());
        assertTrue(filtered.getDomains().get(2).getWhoisServers().isEmpty());
    }

}
