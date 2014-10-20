package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

public class FilterChainTest {

    @Test
    public void testFilter() throws InterruptedException {
        List<Filter<WhoisServer>> chain = new ArrayList<>();
        chain.add(new DeprecatedWhoisServerFilter(5));
        chain.add(new WhoisServerPatternFilter("T4vcMRpp.com", 5));
        FilterChain<WhoisServer> filter = new FilterChain<>(chain);


        WhoisServer whoisCom = new WhoisServer();
        whoisCom.setHost("whois.verisign-grs.com");
        whoisCom.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));

        assertEquals(whoisCom ,filter.filter(whoisCom));


        WhoisServer whoisDe = new WhoisServer();
        whoisDe.setHost("whois.nic.de");
        whoisDe.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));

        assertNull(filter.filter(whoisDe).getAvailablePattern());


        WhoisServer whoisInvalid = new WhoisServer();
        whoisInvalid.setHost("invalid.example.org");
        whoisInvalid.setAvailablePattern(Pattern.compile(
                "no match for", Pattern.CASE_INSENSITIVE));

        assertNull(filter.filter(whoisInvalid));
    }

}
