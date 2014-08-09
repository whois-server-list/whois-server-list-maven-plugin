package de.malkusch.whoisServerList.compiler.helper.converter;

import static org.junit.Assert.*;
import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

@RunWith(Parameterized.class)
public class DomainListToWhoisServerListConverterTest {
    
    private DomainListToWhoisServerListConverter converter;
    
    @Parameter(0)
    public List<Domain> domains;
    
    @Parameter(1)
    public List<WhoisServer> servers;
    
    @Parameters
    public static Collection<List<?>[]> getTestCases() {
        Collection<List<?>[]> cases = new ArrayList<>();

        WhoisServer s1 = new WhoisServer();
        WhoisServer s2 = new WhoisServer();
        WhoisServer s3 = new WhoisServer();
        
        Domain d0 = new Domain();
        
        Domain d1 = new Domain();
        d1.getWhoisServers().add(s1);
        
        Domain d2 = new Domain();
        d2.getWhoisServers().add(s2);
        d2.getWhoisServers().add(s3);
        
        // convert NULL
        cases.add(new List[] {null, null});
        
        // domain without server
        {
            List<Domain> domains = asList(new Domain[] {d0});
            List<WhoisServer> servers = new ArrayList<>();
            cases.add(new List[] {domains, servers});
        }
        
        // domain with one server
        {
            List<Domain> domains = asList(new Domain[] {d1});
            List<WhoisServer> servers = asList(new WhoisServer[] {s1});
            cases.add(new List[] {domains, servers});
        }
        
        // domain with more servers
        {
            List<Domain> domains = asList(new Domain[] {d2});
            List<WhoisServer> servers = asList(new WhoisServer[] {s2, s3});
            cases.add(new List[] {domains, servers});
        }
        
        // many domains
        {
            List<Domain> domains = asList(new Domain[] {d0, d1, d2});
            List<WhoisServer> servers = asList(new WhoisServer[] {s1, s2, s3});
            cases.add(new List[] {domains, servers});
        }
        
        return cases;
    }
    
    @Before
    public void setConverter() {
        this.converter = new DomainListToWhoisServerListConverter();
    }

    @Test
    public void testConvert() {
        assertEquals(servers, converter.convert(domains));
    }

}
