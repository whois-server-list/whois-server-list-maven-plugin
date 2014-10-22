package de.malkusch.whoisServerList.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.compiler.helper.VersionUtil;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.xml.XMLDomainListFactory;
import de.malkusch.whoisServerList.compiler.test.CacheRule;

public class DomainListCompilerTest {

    @Rule
    public CacheRule cacheRule = new CacheRule();

    @Test
    public void testCompile() throws BuildListException, InterruptedException {
        DomainListCompiler compiler =
                new DomainListCompiler(cacheRule.getQueryCache());
        DomainList xmlList =  new XMLDomainListFactory().buildList();
        Date now = new Date();

        DomainList list = compiler.compile();

        assertTrue(now.before(list.getDate()));

        assertEquals(VersionUtil.incrementVersion(xmlList.getVersion()),
                list.getVersion());
    }

}
