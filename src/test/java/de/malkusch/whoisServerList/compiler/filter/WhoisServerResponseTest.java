package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class WhoisServerResponseTest {

    @Parameter
    public WhoisServerResponseFilter filter;

    @Parameters
    public static Collection<WhoisServerResponseFilter[]> getFilters() {
        return Arrays.asList(new WhoisServerResponseFilter[][] {
                { new WhoisServerResponseInvalidPatternFilter() },
        });
    }

    @Test
    public void testFilterNull() throws InterruptedException {
        assertNull(filter.filter(null, "response"));
    }

}
