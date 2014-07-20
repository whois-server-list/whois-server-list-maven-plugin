package de.malkusch.whoisServerList.compiler;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;
import de.malkusch.whoisServerList.compiler.helper.VersionUtil;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.xml.XMLDomainListFactory;
import de.malkusch.whoisServerList.compiler.model.DomainList;

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
