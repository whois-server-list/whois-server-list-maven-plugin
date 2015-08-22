package de.malkusch.whoisServerList.compiler.list.whoisrb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
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
public class WhoisrbDomainListFactoryTest {

    @Mock
    private HttpEntity entity;
    
    /**
     * The SUT.
     */
    private WhoisrbDomainListFactory factory;
    
    @Before
    public void buildFactory() throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = mock(HttpResponse.class);
        when(response.getEntity()).thenReturn(entity);
        
        HttpClient client = mock(HttpClient.class);
        when(client.execute(any())).thenReturn(response);
        
        this.factory = new WhoisrbDomainListFactory(client, new URI("http://example.org/"));
    }
    
    /**
     * Sets the input JSON stream.
     * 
     * @param json The input stream.
     */
    private void setList(String json) {
        try {
            when(entity.getContent()).thenReturn(new ByteArrayInputStream(json.getBytes()));

        } catch (UnsupportedOperationException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    /**
     * Tests building a list converts automatically a IDN.
     */
    @Test
    public void testBuildListForIDN() throws BuildListException, InterruptedException {
        setList("{\".xn--schn-7qa.xn--mgberp4a5d4a\": {\"host\": null}}");
        
        TopLevelDomain domain = factory.buildList().getDomains().get(0);
        assertEquals("السعودة", domain.getName());
        assertEquals("schön.السعودة", domain.getDomains().get(0).getName());
    }
    
    /**
     * Tests building a list removes the leading dot from a Domain.
     */
    @Test
    public void testBuildListRemovesLeadingDot() throws BuildListException, InterruptedException {
        setList("{\".de\": {\"host\": null}}");
        
        TopLevelDomain domain = factory.buildList().getDomains().get(0);
        assertEquals("de", domain.getName());
    }
    
    /**
     * Tests a list with more than one item.
     */
    @Test
    public void testBuildListForMoreItems() throws BuildListException, InterruptedException {
        setList("{\".de\": {\"host\": \"whois.nic.de\"}, \".at\": {\"host\": \"whois.nic.at\"}}");
        
        DomainList list = factory.buildList();
        assertEquals(2, list.getDomains().size());
        
        TopLevelDomain de = list.getDomains().get(0);
        assertEquals("de", de.getName());
        assertEquals("whois.nic.de", de.getWhoisServers().get(0).getHost());
        
        TopLevelDomain at = list.getDomains().get(1);
        assertEquals("at", at.getName());
        assertEquals("whois.nic.at", at.getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a list item which doesn't have a host.
     */
    @Test
    public void testBuildListWithNoHost() throws BuildListException, InterruptedException {
        setList("{\".de\": {\"host\": null}}");
        
        TopLevelDomain domain = factory.buildList().getDomains().get(0);
        assertTrue(domain.getWhoisServers().isEmpty());
    }
    
    /**
     * Tests setting the correct source.
     */
    @Test
    public void testBuildListSetsSource() throws BuildListException, InterruptedException {
        setList("{\".de\": {\"host\": \"whois.nic.de\"}, \".test.de\": {\"host\": \"whois2.nic.de\"}}");
        
        TopLevelDomain domain = factory.buildList().getDomains().get(0);
        assertEquals(Source.WHOIS_RB, domain.getSource());
        assertEquals(Source.WHOIS_RB, domain.getWhoisServers().get(0).getSource());
        assertEquals(Source.WHOIS_RB, domain.getDomains().get(0).getSource());
        assertEquals(Source.WHOIS_RB, domain.getDomains().get(0).getWhoisServers().get(0).getSource());
    }
    
    /**
     * Tests extracting the whois server host from a list item.
     */
    @Test
    public void testBuildListWithHost() throws BuildListException, InterruptedException {
        setList("{\".de\": {\"host\": \"whois.nic.de\"}}");
        
        TopLevelDomain domain = factory.buildList().getDomains().get(0);
        assertEquals("whois.nic.de", domain.getWhoisServers().get(0).getHost());
    }
    

    /**
     * Tests a list with SLD before TLD.
     */
    @Test
    public void testBuildListForSLDBeforeTLD() throws BuildListException, InterruptedException {
        setList("{\".test.de\": {\"host\": \"whois2.nic.de\"}, \".de\": {\"host\": \"whois.nic.de\"}}");
        
        DomainList list = factory.buildList();
        assertEquals(1, list.getDomains().size());
        
        TopLevelDomain domain = list.getDomains().get(0);
        assertEquals("de", domain.getName());
        assertEquals("whois.nic.de", domain.getWhoisServers().get(0).getHost());
        assertEquals("test.de", domain.getDomains().get(0).getName());
        assertEquals("whois2.nic.de", domain.getDomains().get(0).getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a list with SLD after TLD.
     */
    @Test
    public void testBuildListForSLDAfterTLD() throws BuildListException, InterruptedException {
        setList("{\".de\": {\"host\": \"whois.nic.de\"}, \".test.de\": {\"host\": \"whois2.nic.de\"}}");
        
        DomainList list = factory.buildList();
        assertEquals(1, list.getDomains().size());
        
        TopLevelDomain domain = list.getDomains().get(0);
        assertEquals("de", domain.getName());
        assertEquals("whois.nic.de", domain.getWhoisServers().get(0).getHost());
        assertEquals("test.de", domain.getDomains().get(0).getName());
        assertEquals("whois2.nic.de", domain.getDomains().get(0).getWhoisServers().get(0).getHost());
    }
    
    /**
     * Tests a list with SLD and no TLD.
     */
    @Test
    public void testBuildListForSLDwithoutTLD() throws BuildListException, InterruptedException {
        setList("{\".test.de\": {\"host\": \"whois.nic.de\"}}");
        
        DomainList list = factory.buildList();
        assertEquals(1, list.getDomains().size());
        
        TopLevelDomain domain = list.getDomains().get(0);
        assertEquals("de", domain.getName());
        assertTrue(domain.getWhoisServers().isEmpty());
        assertEquals("test.de", domain.getDomains().get(0).getName());
        assertEquals("whois.nic.de", domain.getDomains().get(0).getWhoisServers().get(0).getHost());
    }
    
}
