package de.malkusch.whoisServerList.compiler.list.phpwhois;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;

@RunWith(MockitoJUnitRunner.class)
public class PhpWhoisDomainListFactoryTest {

    @Mock
    private HttpEntity entity;
    
    /**
     * The SUT.
     */
    private PhpWhoisDomainListFactory factory;
    
    @Before
    public void buildFactory() throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = mock(HttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        
        HttpClient client = mock(HttpClient.class);
        when(client.execute(any())).thenReturn(response);
        
        this.factory = new PhpWhoisDomainListFactory(client, new URI("http://example.org/"), 0);
    }

    @Before
    public void setTestStream() throws UnsupportedOperationException, IOException {
        when(entity.getContent()).thenReturn(getClass().getResourceAsStream("/whois.servers.php"));
    }
    
    private TopLevelDomain find(final DomainList list, final String name) {
        return list.getDomains().stream().filter(
                    domain -> domain.getName().equals(name)
                ).findFirst().get();
    }
    
    /**
     * Tests building a list converts automatically a IDN.
     */
    @Test
    public void testBuildListForIDN() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "السعودة");
        
        assertEquals("schön.السعودة", domain.getDomains().get(0).getName());
    }
    
    /**
     * Tests a list with more than one item.
     */
    @Test
    public void testBuildListForMoreItems() throws BuildListException, InterruptedException {
        DomainList list = factory.buildList();
        
        TopLevelDomain domain = find(list, "tlda");
        assertEquals("whois.tlda", domain.getWhoisServers().get(0).getHost());
        
        TopLevelDomain domain2 = find(list, "tldb");
        assertEquals("whois.tldb", domain2.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a list item which doesn't have a host.
     */
    @Test
    public void testBuildListWithNoHost() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "nohost");
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests a list item which has a HTTP URI as host.
     */
    @Test
    public void testBuildListWithHttpHost() throws BuildListException, InterruptedException {
        DomainList list = factory.buildList();
        
        TopLevelDomain domain = find(list, "httphost");
        assertTrue(domain.getWhoisServers().isEmpty());
        
        TopLevelDomain domain2 = find(list, "httpshost");
        assertTrue(domain2.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests setting the correct source.
     */
    @Test
    public void testBuildListSetsSource() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "checksource");
        assertEquals(Source.PHP_WHOIS, domain.getSource());
        assertEquals(Source.PHP_WHOIS, domain.getWhoisServers().get(0).getSource());
        assertEquals(Source.PHP_WHOIS, domain.getDomains().get(0).getSource());
        assertEquals(Source.PHP_WHOIS, domain.getDomains().get(0).getWhoisServers().get(0).getSource());
    }
    
    /**
     * Tests extracting the whois server host from a list item.
     */
    @Test
    public void testBuildListWithHost() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "whoishost");
        assertEquals("whois.nic.tld", domain.getWhoisServers().get(0).getHost());
    }
    

    /**
     * Tests a list with SLD before TLD.
     */
    @Test
    public void testBuildListForSLDBeforeTLD() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "sldfirst");
        
        assertEquals("tld.whois", domain.getWhoisServers().get(0).getHost());
        assertEquals("sld.sldfirst", domain.getDomains().get(0).getName());
        assertEquals("sld.whois", domain.getDomains().get(0).getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a list with SLD after TLD.
     */
    @Test
    public void testBuildListForSLDAfterTLD() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "tldfirst");
        
        assertEquals("tld.whois", domain.getWhoisServers().get(0).getHost());
        assertEquals("sld.tldfirst", domain.getDomains().get(0).getName());
        assertEquals("sld.whois", domain.getDomains().get(0).getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a list with SLD and no TLD.
     */
    @Test
    public void testBuildListForSLDwithoutTLD() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "notld");
        
        assertTrue(domain.getWhoisServers().isEmpty());
        assertEquals("sld.notld", domain.getDomains().get(0).getName());
        assertEquals("sld.whois", domain.getDomains().get(0).getWhoisServers().get(0).getHost());
    }
    
}
