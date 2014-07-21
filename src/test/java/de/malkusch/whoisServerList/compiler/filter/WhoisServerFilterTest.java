package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.*;

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
public class WhoisServerFilterTest {

    private WhoisServerFilter filter;

    private WhoisServer server;

    @Parameter(0)
    public String host;

    @Parameter(1)
    public boolean expected;

    @Parameters
    public static Collection<Object[]> getFilters() {
        Properties properties = DomainListCompiler.getDefaultProperties();
        String ianaWhois
            = properties.getProperty(IanaDomainListFactory.PROPERTY_WHOIS_HOST);
        return Arrays.asList(new Object[][] {
                { ianaWhois, true },
                { "example.org", false },
                { null, false },
        });
    }

    @Before
    public void setup() {
        filter = new WhoisServerFilter(5);
        server = new WhoisServer();
        server.setHost(host);
    }

    @Test
    public void testFilter() {
        WhoisServer filtered = filter.filter(server);
        if (expected) {
            assertNotNull(filtered);

        } else {
            assertNull(filtered);

        }
    }

}
