package de.malkusch.whoisServerList.compiler.merger;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class NewestMergerTest {

    @Test
    public void testMergeNullDates() throws InterruptedException {
        {
            NewestMerger<String> merger = new NewestMerger<>(
                    null, null, new StringMerger());

            assertEquals("l", merger.merge("l", "r"));
        }
        {
            NewestMerger<String> merger = new NewestMerger<>(
                    new Date(), null, new StringMerger());

            assertEquals("l", merger.merge("l", "r"));
        }
        {
            NewestMerger<String> merger = new NewestMerger<>(
                    null, new Date(), new StringMerger());

            assertEquals("l", merger.merge("l", "r"));
        }

    }

    @Test
    public void testMerge() throws InterruptedException {
        {
            NewestMerger<String> merger = new NewestMerger<>(
                    TestUtil.getYesterday(), new Date(), new StringMerger());

            assertEquals("r", merger.merge("l", "r"));
        }
        {
            NewestMerger<String> merger = new NewestMerger<>(
                    new Date(), TestUtil.getYesterday(), new StringMerger());

            assertEquals("l", merger.merge("l", "r"));
        }
    }

}
