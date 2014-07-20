package de.malkusch.whoisServerList.compiler.list.xml;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class XMLDomainListFactoryTest {

    @Test
    public void testBuildList() throws BuildListException, MalformedURLException {
        XMLDomainListFactory factory = new XMLDomainListFactory();
        Collection<TopLevelDomain> toplevelDomains
                = factory.buildList().getDomains();

        TopLevelDomain de = TestUtil.find(toplevelDomains, "de");

        assertTrue(de instanceof CountryCodeTopLevelDomain);
        assertEquals(
                new URL("http://www.denic.de/"), de.getRegistratonService());
        assertEquals(2, de.getWhoisServers().size());
        assertNotNull(TestUtil.find(de.getWhoisServers(), "whois.denic.de"));
        WhoisServer denic = TestUtil.find(de.getWhoisServers(), "whois.nic.de");
        assertNotNull(denic);
        assertEquals(0, de.getDomains().size());

        TopLevelDomain uk = TestUtil.find(toplevelDomains, "uk");
        assertEquals(6, uk.getDomains().size());
        assertNotNull(TestUtil.find(uk.getDomains(), "co.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "org.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "me.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "ac.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "ltd.uk"));
        assertNotNull(TestUtil.find(uk.getDomains(), "plc.uk"));

        TopLevelDomain com = TestUtil.find(toplevelDomains, "com");
        assertNotNull(com);
        assertFalse(com instanceof CountryCodeTopLevelDomain);

        TopLevelDomain net = TestUtil.find(toplevelDomains, "net");
        assertNotNull(net);

        WhoisServer comWhois1
            = TestUtil.find(com.getWhoisServers(), "whois.crsnic.net");
        assertSame(comWhois1,
                TestUtil.find(net.getWhoisServers(), comWhois1.getHost()));

        String exptectedPatter
            = Pattern.quote("no match for");
        assertEquals(exptectedPatter, comWhois1.getAvailablePattern().toString());
    }

}
