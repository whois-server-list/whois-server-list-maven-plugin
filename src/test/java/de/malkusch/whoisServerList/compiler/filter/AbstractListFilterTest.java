package de.malkusch.whoisServerList.compiler.filter;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AbstractListFilterTest {

    @Parameter
    public AbstractListFilter<String> filter;

    @Parameters
    public static Collection<AbstractListFilter<?>[]> getCases() {
        return asList(new AbstractListFilter<?>[][]{
                { new ConcurrentListFilter<>(new StringFilter()) },
                { new ListFilter<>(new StringFilter()) }
        });
    }

    @Test
    public void testFilter() throws InterruptedException {
        List<String> list = asList(new String[] { "a", "", "b" });
        List<String> expected = asList(new String[] { "a", "b" });

        assertEquals(expected, filter.filter(list));
    }

}
