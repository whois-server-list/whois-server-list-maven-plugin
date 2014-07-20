package de.malkusch.whoisServerList.compiler.list.iana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

public class IanaDomainListFactoryTest {

    @Test
    public void testbuildList() throws IOException, BuildListException, InterruptedException {
        IanaDomainListFactory listFactory
                = new IanaDomainListFactory(DomainListCompiler.getDefaultProperties());
        DomainList list = listFactory.buildList();

        assertTrue(list.getDomains().size() > 500);

        for (TopLevelDomain domain : list.getDomains()) {
            assertFalse(domain.getName().isEmpty());

            assertNotNull(domain.getState());

            if (! domain.getWhoisServers().isEmpty()) {
                assertEquals(1, domain.getWhoisServers().size());
                WhoisServer server = domain.getWhoisServers().get(0);
                assertFalse(server.getHost().isEmpty());

            }

            if (DomainUtil.isCountryCode(domain.getName())) {
                assertEquals(domain.getName().toUpperCase(), domain.getCountryCode());

            }
        }
    }

}
