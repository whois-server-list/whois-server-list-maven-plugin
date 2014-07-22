package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

public class WhoisServerMergerTest {

    @Test
    public void testMergeHostName() {
        WhoisServerMerger merger = new WhoisServerMerger();

        WhoisServer left = new WhoisServer();
        left.setHost("www.example.net");

        WhoisServer right = new WhoisServer();
        right.setHost("www.example.org");
        right.setAvailablePattern(Pattern.compile("test"));

        WhoisServer expected = new WhoisServer();
        expected.setHost("www.example.net");
        expected.setAvailablePattern(right.getAvailablePattern());

        assertEquals(expected, merger.merge(left, right));
    }

}
