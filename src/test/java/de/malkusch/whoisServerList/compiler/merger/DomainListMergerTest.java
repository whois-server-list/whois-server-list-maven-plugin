package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class DomainListMergerTest {

    @Test
    public void testMerge() {
        DomainListMerger merger = new DomainListMerger();

        DomainList left = new DomainList();
        left.setDate(TestUtil.getYesterday());
        TopLevelDomain de = new TopLevelDomain();
        de.setName("de");
        left.getDomains().add(de);

        DomainList right = new DomainList();
        right.setDate(new Date());
        right.setDescription("right");
        right.setVersion("1.0.0");
        TopLevelDomain net = new TopLevelDomain();
        net.setName("net");
        right.getDomains().add(net);

        DomainList expected = new DomainList();
        expected.setDate(right.getDate());
        expected.setDescription("right");
        expected.setVersion("1.0.0");
        expected.getDomains().add(de);
        expected.getDomains().add(net);

        assertEquals(expected, merger.merge(left, right));
    }

}
