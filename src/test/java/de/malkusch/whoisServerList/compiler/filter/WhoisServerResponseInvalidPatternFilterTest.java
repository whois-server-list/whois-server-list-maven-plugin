package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

@RunWith(Parameterized.class)
public class WhoisServerResponseInvalidPatternFilterTest {

    private WhoisServerResponseInvalidPatternFilter filter;

    private WhoisServer server;

    @Parameter(0)
    public String availableResponse;
    
    @Parameter(1)
    public Optional<String> unavailableResponse;

    @Parameter(2)
    public String pattern;

    @Parameter(3)
    public String expected;

    @Parameters
    public static Collection<Object[]> getCases() {
        return Arrays.asList(new Object[][] {
            { "no match for", Optional.empty(), "no match for", "no match for" },
            { "No Match For", Optional.empty(), "no match for", "no match for" },
            { "xyz", Optional.empty(), "anypattern", null },
            { "xyz", Optional.empty(), null, null },
            { "xyz \n xyz invalid  xyz \n xyz", Optional.empty(), "invalid", "invalid" },
        });
    }

    @Before
    public void setup() {
        filter = new WhoisServerResponseInvalidPatternFilter();
        
        server = new WhoisServer();
        if (pattern != null) {
            server.setAvailablePattern(
                    Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));

        }
    }

    @Test
    public void testFilter() {
        WhoisServer filtered = filter.filter(server, availableResponse, unavailableResponse);

        if (expected == null) {
            assertNull(filtered.getAvailablePattern());

        } else {
            assertEquals(expected, filtered.getAvailablePattern().toString());

        }
    }

}
