package de.malkusch.whoisServerList.compiler.list.iana;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;

public class IanaDomainListFactoryTest {

    @Test
    public void testbuildList() throws IOException, BuildListException, InterruptedException {
        IanaDomainListFactory listFactory
                = new IanaDomainListFactory(DomainListCompiler.getDefaultProperties());
        DomainList list = listFactory.buildList();

        assertTrue(list.getDomains().size() > 500);

        for (TopLevelDomain domain : list.getDomains()) {
            assertFalse(domain.getName().isEmpty());

            assertNotEquals('.', domain.getName().charAt(0));

            assertNotNull(domain.getState());

            if (! domain.getWhoisServers().isEmpty()) {
                assertEquals(1, domain.getWhoisServers().size());
                WhoisServer server = domain.getWhoisServers().get(0);
                assertFalse(server.getHost().isEmpty());

            }

            assertEquals(DomainUtil.getCountryCode(domain.getName()), domain.getCountryCode());
        }
    }

}
