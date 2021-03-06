package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

@RunWith(Parameterized.class)
@Deprecated
public class WhoisServerPatternFilterTest {

    private WhoisServerPatternFilter filter;

    private WhoisServer server;

    @Parameter(0)
    public String host;

    @Parameter(1)
    public String pattern;

    @Parameter(2)
    public String query;

    @Parameter(3)
    public String expected;

    @Parameters
    public static Collection<String[]> getCases() {
        return Arrays.asList(new String[][] {
            { "whois.verisign-grs.com", "no match for", "T4vcMRpp.com",
                    "no match for" },
            { "invalid.example.org", "anypattern", "anyquery", "anypattern" },
            { "whois.verisign-grs.com", "no match for", "example.com", null },
            { "whois.verisign-grs.com", "invalid", "T4vcMRpp.com", null },
            { "whois.verisign-grs.com", null, "T4vcMRpp.com", null },
        });
    }

    @Before
    public void setup() {
        filter = new WhoisServerPatternFilter(query, 5);

        server = new WhoisServer();
        server.setHost(host);
        if (pattern != null) {
            server.setAvailablePattern(
                    Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));

        }
    }

    @Test
    public void testFilter() {
        WhoisServer filtered = filter.filter(server);

        if (expected == null) {
            assertNull(filtered.getAvailablePattern());

        } else {
            assertEquals(expected, filtered.getAvailablePattern().toString());

        }
    }

}
