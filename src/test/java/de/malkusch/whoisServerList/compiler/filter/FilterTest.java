package de.malkusch.whoisServerList.compiler.filter;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FilterTest {

    @Parameter
    public Filter<?> filter;

    @Parameters
    public static Collection<Filter<?>[]> getFilters() {
        return Arrays.asList(new Filter<?>[][] {
                { new TCPServiceFilter(1, 1) },
                { new WhoisServerFilter(1) },
                { new WhoisServerPatternFilter("") },
        });
    }

    @Test
    public void testFilterNull() {
        assertNull(filter.filter(null));
    }

}
