package de.malkusch.whoisServerList.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.compiler.helper.VersionUtil;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.xml.XMLDomainListFactory;

public class DomainListCompilerTest {

    @Test
    public void testCompile() throws BuildListException, InterruptedException {
        DomainListCompiler compiler = new DomainListCompiler();
        DomainList xmlList =  new XMLDomainListFactory().buildList();
        Date now = new Date();

        DomainList list = compiler.compile();

        assertTrue(now.before(list.getDate()));

        assertEquals(VersionUtil.incrementVersion(xmlList.getVersion()),
                list.getVersion());
    }

}
