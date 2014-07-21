package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.*;

import org.junit.Test;

public class NotNullMergerTest {

    @Test
    public void testMerge() throws InterruptedException {
        Merger<String> merger = new NotNullMerger<String>();

        assertEquals("l", merger.merge("l", "r"));
    }

}
