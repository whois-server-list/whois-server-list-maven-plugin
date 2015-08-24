package de.malkusch.whoisServerList.compiler.list.iana;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.commons.net.whois.WhoisClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain.State;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(WhoisClient.class)
public class IANATopLevelDomainBuilderTest {

    private IANATopLevelDomainBuilder builder;
    
    private static final String CHARSET = "UTF-8";
    private static final String HOST = "whois.example.net";
    
    @Mock
    public WhoisClient client;
    
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Before
    public void setTopLevelDomainFactory() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/compiler.properties"));

        builder = new IANATopLevelDomainBuilder(client, HOST, CHARSET);
    }
    
    @Test
    public void testBuild() throws ParseException, WhoisServerListException, InterruptedException, IOException {
        when(client.getInputStream(false, "de", CHARSET))
                .thenReturn(getClass().getResourceAsStream("/iana/de.txt"));
        
        builder.setName("de");
        TopLevelDomain domain = builder.build();
        
        TopLevelDomain expected = new TopLevelDomain();
        expected.setCountryCode("DE");
        expected.setName("de");
        expected.setState(State.ACTIVE);
        expected.setSource(Source.IANA);
        expected.setCreated(dateFormat.parse("1986-11-05"));
        expected.setChanged(dateFormat.parse("2012-04-19"));
        expected.setRegistrationService(new URL("http://www.denic.de/"));
        WhoisServer deServer = new WhoisServer();
        deServer.setHost("whois.denic.de");
        deServer.setSource(Source.IANA);
        expected.getWhoisServers().add(deServer);
        assertEquals(expected, domain);
    }
    
    @Test
    public void testIdn() throws ParseException, WhoisServerListException, InterruptedException, IOException {
        when(client.getInputStream(false, "网络", CHARSET))
                .thenReturn(getClass().getResourceAsStream("/iana/cn.txt"));
        
        builder.setName("网络");
        TopLevelDomain domain = builder.build();
        
        assertEquals("网络", domain.getName());
    }
    
    @Test
    public void testInactive() throws ParseException, WhoisServerListException, InterruptedException, IOException {
        when(client.getInputStream(false, "テスト", CHARSET))
                .thenReturn(getClass().getResourceAsStream("/iana/inactive.txt"));
        
        builder.setName("テスト");
        TopLevelDomain domain = builder.build();
        
        assertEquals(State.INACTIVE, domain.getState());
    }
    
    @Test
    public void testCountryCodeMapping() throws ParseException, WhoisServerListException, InterruptedException, IOException {
        when(client.getInputStream(false, "uk", CHARSET))
                .thenReturn(getClass().getResourceAsStream("/iana/uk.txt"));

        builder.setName("uk");
        TopLevelDomain domain = builder.build();

        assertEquals("GB", domain.getCountryCode());
    }
    
    @Test
    public void testPercentsInValue() throws ParseException, WhoisServerListException, InterruptedException, IOException {
        when(client.getInputStream(false, "工行", CHARSET))
        .thenReturn(getClass().getResourceAsStream("/iana/test.txt"));
        
        builder.setName("工行");
        builder.build();
    }

}
