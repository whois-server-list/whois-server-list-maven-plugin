package de.malkusch.whoisServerList.compiler.merger;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.helper.converter.WhoisServerToHostConverter;

@RunWith(Parameterized.class)
public class ListMergerTest {

    @Parameter
    public AbstractListMerger<WhoisServer> merger;

    @Parameters
    public static Collection<AbstractListMerger<?>[]> getCases() {
        return Arrays.asList(new AbstractListMerger<?>[][] {
                {new ListMerger<>(
                    new WhoisServerToHostConverter(), new WhoisServerMerger())},

                {new ConcurrentListMerger<>(
                    new WhoisServerToHostConverter(), new WhoisServerMerger())},
        });
    }

    @Test
    public void testMerge() throws InterruptedException {

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
