package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.test.CacheRule;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

@RunWith(Parameterized.class)
public class DomainFilterTest {

    @Rule
    public CacheRule cacheRule = new CacheRule();

    @Parameter(0)
    public Domain domain;
    
    @Parameter(1)
    public Domain expected;
    
    @Parameter(2)
    public Pattern[] patterns;
    
    @Parameters
    public static Collection<Object[]> getFilters() {
        Collection<Object[]> cases = new ArrayList<>();
        
        Pattern[] patterns = new Pattern[] {
            Pattern.compile(
                    Pattern.quote("not found"), Pattern.CASE_INSENSITIVE)
        };
        
        {
            Domain domain = TestUtil.buildDomain(
                    "com", "whois.verisign-grs.com", "no match for");
            Domain expected = domain.clone();
            cases.add(new Object[] { domain, expected, patterns });
        }
        
        {
            Domain domain = TestUtil.buildDomain(
                    "de", "whois.nic.de", "no match for");
            Domain expected = TestUtil.buildDomain(
                    "de", "whois.nic.de", null);
            cases.add(new Object[] { domain, expected, patterns });
        }
        
        {
            Domain domain = TestUtil.buildSimpleDomain(" net ");
            Domain expected = TestUtil.buildSimpleDomain("net");
            cases.add(new Object[] { domain, expected, patterns });
        }
        
        {
            Domain domain = TestUtil.buildDomain(
                    "org", "whois.pir.org", null);
            Domain expected = TestUtil.buildDomain(
                    "org", "whois.pir.org", "not found");
            cases.add(new Object[] { domain, expected, patterns });
        }
        
        {
            Domain domain = TestUtil.buildDomain(
                    "as", "whois.nic.as", "No Object Found");
            Domain expected = domain.clone();
            cases.add(new Object[] { domain, expected, patterns });
        }
        {
            Domain domain = TestUtil.buildDomain(
                    "de", "whois.denic.de", "Status: free");
            Domain expected = domain.clone();
            cases.add(new Object[] { domain, expected, patterns });
        }
        {
            Domain domain = TestUtil.buildDomain(
                    "de", "whois.nic.de", "Status: free");
            Domain expected = domain.clone();
            cases.add(new Object[] { domain, expected, patterns });
        }
        {
            Domain domain = TestUtil.buildDomain(
                    "org", "whois.pir.org", "not found");
            Domain expected = domain.clone();
            cases.add(new Object[] { domain, expected, patterns });
        }
        {
            Domain domain = TestUtil.buildDomain(
                    "org", "whois.publicinterestregistry.net", "not found");
            Domain expected = domain.clone();
            cases.add(new Object[] { domain, expected, patterns });
        }
        
        return cases;
    }

    @Test
    public void testFilter() throws InterruptedException {
        DomainFilter<Domain> filter = new DomainFilter<>(
                5, Arrays.asList(patterns), cacheRule.getQueryCache());

        assertEquals(expected, filter.filter(domain));
    }

}
