package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class DateMergerTest {

    @Test
    public void testMerge() {
        DateMerger merger = new DateMerger();

        Date yesterday = TestUtil.getYesterday();
        Date now = new Date();

        assertEquals(now, merger.merge(yesterday, now));
        assertEquals(now, merger.merge(now, yesterday));

    }

}
