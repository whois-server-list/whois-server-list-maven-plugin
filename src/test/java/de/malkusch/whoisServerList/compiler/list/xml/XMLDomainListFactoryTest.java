package de.malkusch.whoisServerList.compiler.list.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class XMLDomainListFactoryTest {

    @Test
    public void testBuildList() throws BuildListException, MalformedURLException {
        XMLDomainListFactory factory = new XMLDomainListFactory();
        DomainList list = factory.buildList();
        Collection<TopLevelDomain> toplevelDomains = list.getDomains();

        TopLevelDomain de = TestUtil.find(toplevelDomains, "de");

        assertEquals("DE", de.getCountryCode());
        assertEquals(
                new URL("http://www.denic.de/"), de.getRegistrationService());
        assertEquals(2, de.getWhoisServers().size());
        assertNotNull(TestUtil.find(de.getWhoisServers(), "whois.denic.de"));
        WhoisServer denic = TestUtil.find(de.getWhoisServers(), "whois.nic.de");
        assertNotNull(denic);
        assertEquals(8, de.getDomains().size());

        TopLevelDomain uk = TestUtil.find(toplevelDomains, "uk");
        assertNotNull(TestUtil.find(uk.getDomains(), "co.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "org.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "me.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "ac.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "ltd.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "plc.uk"));

        TopLevelDomain com = TestUtil.find(toplevelDomains, "com");
        assertNotNull(com);
        assertNull(com.getCountryCode());

        TopLevelDomain net = TestUtil.find(toplevelDomains, "net");
        assertNotNull(net);

        WhoisServer comWhois1
            = TestUtil.find(com.getWhoisServers(), "whois.crsnic.net");
        assertEquals(comWhois1,
                TestUtil.find(net.getWhoisServers(), comWhois1.getHost()));

        String exptectedPatter
            = Pattern.quote("no match for");
        assertEquals(exptectedPatter, comWhois1.getAvailablePattern().toString());

        assertEquals("2.0.2", list.getVersion());

        Pattern descriptionPattern = Pattern.compile(
                "^This list .+1335STSwu9hST4vcMRppEPgENMHD2r1REK$",
                Pattern.DOTALL);
        assertTrue(
                String.format("description was '%s'", list.getDescription()),
                descriptionPattern.matcher(list.getDescription()).matches());
    }

}
