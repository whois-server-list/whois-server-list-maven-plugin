package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class URLMergerTest {

    @Parameter(0)
    public String expected;

    @Parameter(1)
    public String left;

    @Parameter(2)
    public String right;

    @Parameters
    public static Collection<String[]> getCases() {
        return Arrays.asList(new String[][] {
            {null, "http://invalid.example.org", "http://invalid.example.com"},

            {"http://example.com",
                "http://invalid.example.org", "http://example.com"},

            {"http://example.com",
                    "http://example.com", "http://invalid.example.org"},

            {"http://example.com",
                "http://example.com", "http://example.org"},

            {"http://example.com",
                "http://example.org/invalid", "http://example.com"},

            {"https://ssl.malkusch.de/webmail/",
                "http://webmail.malkusch.de/", "http://example.com"},

            {"https://www.registry.net.za/",
                    "http://www.registry.net.za", "http://example.com"},
            
            {"http://cp.gmx.tango.knipp.de",
                    "http://cp.gmx.tango.knipp.de", "http://example.com"},

        });
    }

    @Test
    public void testMerge() throws MalformedURLException, InterruptedException {
        URLMerger merger = new URLMerger(10);

        URL expectedURL = expected == null ? null : new URL(expected);

        URL merged = merger.merge(new URL(left), new URL(right));

        assertEquals(expectedURL, merged);
    }

}
