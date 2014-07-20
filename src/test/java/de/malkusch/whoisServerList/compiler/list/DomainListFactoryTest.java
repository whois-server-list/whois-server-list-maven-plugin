package de.malkusch.whoisServerList.compiler.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.psl.PublicSuffixDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.xml.XMLDomainListFactory;
import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

@RunWith(Parameterized.class)
public class DomainListFactoryTest {

    @Parameter
    public DomainListFactory factory;

    @Parameters
    public static final Collection<DomainListFactory[]> getFactories() throws IOException {
        List<DomainListFactory[]> cases = new ArrayList<>();

        cases.add(new DomainListFactory[] {
                new IanaDomainListFactory(DomainListCompiler.getDefaultProperties())});

        cases.add(new DomainListFactory[] {
                new PublicSuffixDomainListFactory()});

        cases.add(new DomainListFactory[] {
                new XMLDomainListFactory()});

        return cases;
    }

    @Test
    public void testBuild() throws BuildListException, InterruptedException {
        DomainList list = factory.buildList();

        assertTrue(list.getDomains().size() > 50);

        for (TopLevelDomain domain : list.getDomains()) {
            assertDomain(domain);

            for (WhoisServer server : domain.getWhoisServers()) {
                assertEquals(factory.getSource(), server.getSource());

            }

            for (Domain subdomain : domain.getDomains()) {
                assertDomain(subdomain);

            }
        }
    }

    private void assertDomain(final Domain domain) {
        assertFalse(domain.getName().isEmpty());
        assertEquals(factory.getSource(), domain.getSource());
    }

}
