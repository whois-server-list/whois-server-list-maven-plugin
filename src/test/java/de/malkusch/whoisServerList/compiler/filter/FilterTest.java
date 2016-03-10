package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

@RunWith(Parameterized.class)
public class FilterTest {

    @Parameter
    public Filter<?> filter;

    @Parameters
    public static Collection<Filter<?>[]> getFilters() {
        return Arrays.asList(new Filter<?>[][] {
                { new TCPServiceFilter(1, 1) },
                { new ListFilter<>(null) },
                { new ConcurrentListFilter<>(null) },
                { new StringFilter() },
                { new DomainFilter<Domain>(new ArrayList<Pattern>(), null) },
                { new FilterChain<String>(new ArrayList<Filter<String>>()) },
                { new DomainListFilter(null) },
        });
    }

    @Test
    public void testFilterNull() throws InterruptedException {
        assertNull(filter.filter(null));
    }

}
