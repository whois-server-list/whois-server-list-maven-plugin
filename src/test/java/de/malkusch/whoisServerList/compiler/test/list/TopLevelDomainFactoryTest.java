package de.malkusch.whoisServerList.compiler.test.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.TopLevelDomainFactory;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class TopLevelDomainFactoryTest {

    @Test
    public void testBuild() throws WhoisServerListException {
        TopLevelDomainFactory factory = new TopLevelDomainFactory();
        
        assertNull(factory.build(null));
        
        assertEquals(TestUtil.buildSimpleTld("net"), factory.build("net"));
        assertEquals(TestUtil.buildSimpleTld("net"), factory.build("Net"));
        
        assertEquals(TestUtil.buildSimpleCcTld("de", "DE"), factory.build("de"));
    }
    
}
