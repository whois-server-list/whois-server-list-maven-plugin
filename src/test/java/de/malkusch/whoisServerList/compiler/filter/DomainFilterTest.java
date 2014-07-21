package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.regex.Pattern;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;

public class DomainFilterTest {

    @Test
    public void testFilter() throws InterruptedException {
        DomainFilter<Domain> filter = new DomainFilter<>(5);

        Domain com = new Domain();
        WhoisServer whoisCom = new WhoisServer();
        whoisCom.setHost("whois.verisign-grs.com");
        whoisCom.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));
        com.getWhoisServers().add(whoisCom);

        assertEquals(com, filter.filter(com));


        Domain de = new Domain();
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
    }

}
