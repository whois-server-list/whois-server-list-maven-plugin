package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class DomainMergerTest {

    @Test
    public void testMerge() throws InterruptedException {
        DomainMerger<Domain> merger = new DomainMerger<>();

        Domain left = new Domain();
        left.setName("example.net");
        left.setChanged(TestUtil.getYesterday());
        left.setCreated(TestUtil.getYesterday());

        WhoisServer onlyLeft = new WhoisServer();
        onlyLeft.setHost("onlyLeft.example.net");
        left.getWhoisServers().add(onlyLeft);

        Domain right = new Domain();
        right.setName("example.net");
        right.setChanged(new Date());
        right.setCreated(new Date());

        WhoisServer onlyRight = new WhoisServer();
        onlyRight.setHost("onlyRight.example.net");
        right.getWhoisServers().add(onlyRight);

        Domain expected = new Domain();
        expected.setName(right.getName());
        expected.setChanged(right.getChanged());
        expected.setCreated(right.getCreated());
        expected.getWhoisServers().addAll(left.getWhoisServers());
        expected.getWhoisServers().addAll(right.getWhoisServers());

        assertEquals(expected, merger.merge(left, right));
    }

}
