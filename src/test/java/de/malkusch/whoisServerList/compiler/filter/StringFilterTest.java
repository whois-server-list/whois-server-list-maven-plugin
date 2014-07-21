package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringFilterTest {

    @Test
    public void testFilter() {
        StringFilter filter = new StringFilter();

        assertEquals(null, filter.filter(""));
        assertEquals(null, filter.filter(" "));
        assertEquals("test", filter.filter(" test "));
        assertEquals("test", filter.filter("test"));
    }

}
