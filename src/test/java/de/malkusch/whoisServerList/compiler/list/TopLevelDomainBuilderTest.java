package de.malkusch.whoisServerList.compiler.list;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.TopLevelDomainBuilder;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class TopLevelDomainBuilderTest {

    @Test
    public void testBuild()
            throws WhoisServerListException, InterruptedException {

        TopLevelDomainBuilder builder = new TopLevelDomainBuilder(Source.XML);

        builder.setName("net");
        assertEquals(TestUtil.buildSimpleTld("net"), builder.build());

        builder.setName("Net");
        assertEquals(TestUtil.buildSimpleTld("net"), builder.build());

        builder.setName("de");
        assertEquals(TestUtil.buildSimpleCcTld("de", "DE"), builder.build());
    }

    @Test(expected=IllegalStateException.class)
    public void buildNoDomainName()
            throws WhoisServerListException, InterruptedException {

        TopLevelDomainBuilder builder = new TopLevelDomainBuilder(Source.XML);

        builder.setName(null);
        builder.build();
    }

}
