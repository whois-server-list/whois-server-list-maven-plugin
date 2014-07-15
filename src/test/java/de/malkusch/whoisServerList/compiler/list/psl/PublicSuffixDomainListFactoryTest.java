package de.malkusch.whoisServerList.compiler.list.psl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.psl.PublicSuffixDomainListFactory;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class PublicSuffixDomainListFactoryTest {
    
    @Test
    public void testBuildList() throws BuildListException {
        PublicSuffixDomainListFactory factory
            = new PublicSuffixDomainListFactory();
        
        Map<String, TopLevelDomain> topLevelDomains = new HashMap<>();
        for (TopLevelDomain tld : factory.buildList()) {
            assertFalse(topLevelDomains.containsKey(tld.getName()));
            topLevelDomains.put(tld.getName(), tld);
            
        }
        
        CountryCodeTopLevelDomain de
            = (CountryCodeTopLevelDomain) topLevelDomains.get("de");
        assertEquals("de", de.getName());
        assertEquals("DE", de.getCountryCode());
        
        TopLevelDomain uk = topLevelDomains.get("uk");
        assertFalse(uk.getDomains().isEmpty());
        assertTrue(uk.getDomains().contains(TestUtil.buildSimpleDomain("co.uk")));
    }

}
