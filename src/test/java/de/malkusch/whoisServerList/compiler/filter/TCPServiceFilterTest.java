package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;

@RunWith(Parameterized.class)
public class TCPServiceFilterTest {

    private TCPServiceFilter filter;

    @Parameter(0)
    public String host;

    @Parameter(1)
    public int port;

    @Parameter(2)
    public String expected;

    @Parameters
    public static Collection<Object[]> getFilters() {
        Properties properties = DomainListCompiler.getDefaultProperties();
        String ianaWhois
            = properties.getProperty(IanaDomainListFactory.PROPERTY_WHOIS_HOST);
        return Arrays.asList(new Object[][] {
                { "www.example.org", 80, "www.example.org" },
                { ianaWhois, WhoisServer.DEFAULT_PORT, ianaWhois },
                { "www.example.org", 123, null },
                { "invalid.example.org", 80, null },
        });
    }

    @Before
    public void setup() {
        filter = new TCPServiceFilter(port, 5);
    }

    @Test
    public void testFilter() {
        assertEquals(expected, filter.filter(host));
    }

}
