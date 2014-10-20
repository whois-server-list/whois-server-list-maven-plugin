package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

public class DomainFilterTest {

    @Test
    public void testFilter() throws InterruptedException {
        Pattern[] patterns = new Pattern[] {
                Pattern.compile("not found", Pattern.CASE_INSENSITIVE)
        };
        DomainFilter<Domain> filter
                = new DomainFilter<>(5, Arrays.asList(patterns));

        Domain com = new Domain();
        WhoisServer whoisCom = new WhoisServer();
        whoisCom.setHost("whois.verisign-grs.com");
        whoisCom.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));
        com.getWhoisServers().add(whoisCom);

        assertEquals(com, filter.filter(com));


        Domain de = new Domain();
        de.setName("de");
        WhoisServer whoisDe = new WhoisServer();
        whoisDe.setHost("whois.nic.de");
        whoisDe.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));
        de.getWhoisServers().add(whoisDe);

        assertNull(
            filter.filter(de).getWhoisServers().get(0).getAvailablePattern());


        Domain net = new Domain();
        net.setName(" net ");
        assertEquals("net", filter.filter(net).getName());
        
        
        Domain org = new Domain();
        org.setName("org");
        WhoisServer whoisOrg = new WhoisServer();
        whoisOrg.setHost("whois.pir.org");
        org.getWhoisServers().add(whoisOrg);

        assertEquals(
            "not found",
            filter.filter(org).getWhoisServers().get(0).getAvailablePattern().toString());
    }

}
