package de.malkusch.whoisServerList.compiler.merger;

import static java.util.Arrays.*;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.helper.converter.WhoisServerToHostConverter;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;

public class ListMergerTest {

    @Test
    public void testMerge() throws InterruptedException {
        ListMerger<WhoisServer> merger = new ListMerger<>(
                new WhoisServerToHostConverter(), new WhoisServerMerger());

        WhoisServer onlyLeft = new WhoisServer();
        onlyLeft.setHost("onlyLeft.example.net");

        WhoisServer onlyRight = new WhoisServer();
        onlyRight.setHost("onlyRight.example.net");

        WhoisServer mergeLeft = new WhoisServer();
        mergeLeft.setHost("merge.example.net");

        WhoisServer mergeRight = new WhoisServer();
        mergeRight.setHost("merge.example.net");
        mergeRight.setAvailablePattern(Pattern.compile("test"));

        WhoisServer merged = new WhoisServer();
        merged.setHost("merge.example.net");
        merged.setAvailablePattern(mergeRight.getAvailablePattern());

        WhoisServer[] leftList = new WhoisServer[] {
                onlyLeft,
                mergeLeft
        };

        WhoisServer[] rightList = new WhoisServer[] {
                onlyRight,
                mergeRight
        };

        WhoisServer[] mergedList = new WhoisServer[] {
                onlyLeft,
                merged,
                onlyRight,
        };

        assertArrayEquals(
                mergedList,
                merger.merge(asList(leftList), asList(rightList)).toArray());
    }

}
