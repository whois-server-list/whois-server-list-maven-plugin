package de.malkusch.whoisServerList.compiler.list.mdwhois;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;

@RunWith(MockitoJUnitRunner.class)
public class MDWhoisDomainListFactoryTest {

    @Mock
    private HttpEntity entity;
    
    /**
     * The SUT.
     */
    private MDWhoisDomainListFactory factory;
    
    @Before
    public void buildFactory() throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = mock(HttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        
        HttpClient client = mock(HttpClient.class);
        when(client.execute(any())).thenReturn(response);
        
        this.factory = new MDWhoisDomainListFactory(client, new URI("http://example.org/"));
    }

    @Before
    public void setTestStream() throws UnsupportedOperationException, IOException {
        when(entity.getContent()).thenReturn(getClass().getResourceAsStream("/tld_serv_list"));
    }

    private TopLevelDomain find(final DomainList list, final String name) {
        return list.getDomains().stream().filter(
                    domain -> domain.getName().equals(name)
                ).findFirst().get();
    }
    
    /**
     * Tests a TLD which has a comment.
     */
    @Test
    public void testTLDWithComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test");
        
        assertEquals("whois.test", domain.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a SLD which has a comment.
     */
    @Test
    public void testSLDWithComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test").getDomains().get(0);

        assertEquals("sld.test", domain.getName());
        assertEquals("whois.sld.test", domain.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a TLD which has no comment.
     */
    @Test
    public void testTLDWithNoComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test2");
        
        assertEquals("whois.test2", domain.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a SLD which has no comment.
     */
    @Test
    public void testSLDWithNoComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test2").getDomains().get(0);
        
        assertEquals("sld.test2", domain.getName());
        assertEquals("whois.sld.test2", domain.getWhoisServers().get(0).getHost());
    }

    /**
     * Tests a TLD with Flag
     */
    @Test
    public void testTLDWithFlag() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test3");
        
        assertEquals("whois.test3", domain.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a SLD with Flag
     */
    @Test
    public void testSLDWithFlag() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test3").getDomains().get(0);
        
        assertEquals("sld.test3", domain.getName());
        assertEquals("whois.sld.test3", domain.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a TLD with Flag and Comment
     */
    @Test
    public void testTLDWithFlagAndComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test4");
        
        assertEquals("whois.test4", domain.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a SLD with Flag and Comment
     */
    @Test
    public void testSLDWithFlagAndComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test4").getDomains().get(0);
        
        assertEquals("sld.test4", domain.getName());
        assertEquals("whois.sld.test4", domain.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a TLD with Flag and no Host
     */
    @Test
    public void testTLDWithFlagAndNoHost() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test5");
        
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests a SLD with Flag and no Host
     */
    @Test
    public void testSLDWithFlagAndNoHost() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test5").getDomains().get(0);
        
        assertEquals("sld.test5", domain.getName());
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests a TLD with Flag and no Host and comment
     */
    @Test
    public void testTLDWithFlagAndNoHostAndComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test6");
        
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests a SLD with Flag and no Host and comment
     */
    @Test
    public void testSLDWithFlagAndNoHostAndComment() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test6").getDomains().get(0);
        
        assertEquals("sld.test6", domain.getName());
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests a TLD with web interface.
     */
    @Test
    public void testTLDWithWeb() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test7");
        
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests a SLD with web interface.
     */
    @Test
    public void testSLDWithWeb() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "test7").getDomains().get(0);
        
        assertEquals("sld.test7", domain.getName());
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    

    /**
     * Tests building a list converts automatically a IDN.
     */
    @Test
    public void testBuildListForIDN() throws BuildListException, InterruptedException {
        Domain domain = find(factory.buildList(), "السعودة").getDomains().get(0);
        
        assertEquals("schön.السعودة", domain.getName());
    }
    
    /**
     * Tests setting the correct source.
     */
    @Test
    public void testBuildListSetsSource() throws BuildListException, InterruptedException {
        TopLevelDomain domain = find(factory.buildList(), "test");

        assertEquals(Source.MD_WHOIS, domain.getSource());
        assertEquals(Source.MD_WHOIS, domain.getWhoisServers().get(0).getSource());
        assertEquals(Source.MD_WHOIS, domain.getDomains().get(0).getSource());
        assertEquals(Source.MD_WHOIS, domain.getDomains().get(0).getWhoisServers().get(0).getSource());
    }
    
    /**
     * Tests a list with SLD after TLD.
     */
    @Test
    public void testBuildListForSLDAfterTLD() throws BuildListException, InterruptedException {
        TopLevelDomain tld = find(factory.buildList(), "test8");
        Domain         sld = tld.getDomains().get(0);
        
        assertEquals("whois.test8", tld.getWhoisServers().get(0).getHost());
        assertEquals("sld.test8", sld.getName());
        assertEquals("whois.sld.test8", sld.getWhoisServers().get(0).getHost());
    }

    /**
     * Tests a list with SLD before TLD.
     */
    @Test
    public void testBuildListForSLDBeforeTLD() throws BuildListException, InterruptedException {
        TopLevelDomain tld = find(factory.buildList(), "test9");
        Domain         sld = tld.getDomains().get(0);
        
        assertEquals("whois.test9", tld.getWhoisServers().get(0).getHost());
        assertEquals("sld.test9", sld.getName());
        assertEquals("whois.sld.test9", sld.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a list with SLD and no TLD.
     */
    @Test
    @Ignore("Incomplete")
    public void testBuildListForSLDwithoutTLD() throws BuildListException, InterruptedException {
        TopLevelDomain tld = find(factory.buildList(), "test10");
        Domain         sld = tld.getDomains().get(0);
        
        assertTrue(tld.getWhoisServers().isEmpty());
        assertEquals("sld.test10", sld.getName());
        assertEquals("whois.sld.test10", sld.getWhoisServers().get(0).getHost());
    }
}
