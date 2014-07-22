package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.DomainListCompiler;

@RunWith(Parameterized.class)
public class MergerTest {

    @Parameter(0)
    public Merger<Object> merger;

    @Parameter(1)
    public Object object;

    @Parameters
    public static Collection<Object[]> getParameters()
            throws MalformedURLException {

        Properties properties = DomainListCompiler.getDefaultProperties();

        return Arrays.asList(new Object[][] {
                { new NotNullMerger<String>(), "test" },
                { new DateMerger(), new Date() },
                { new StringMerger(), "test" },
                { new WhoisServerMerger(), new WhoisServer() },
                { new DomainMerger<Domain>(), new Domain() },
                { new TopLevelDomainMerger(properties), new TopLevelDomain() },
                { new NewestMerger<>(
                        new Date(), new Date(), new StringMerger()), "test" },
                { new ListMerger<>(null, null), Collections.EMPTY_LIST },
                { new DomainListMerger(properties), new DomainList() },
                { new URLMerger(10), new URL("http://www.example.org") },
        });
    }

    @Test
    public void testMergeNull() throws InterruptedException {
        assertNull(merger.merge(null, null));
        assertEquals(object, merger.merge(object, null));
        assertEquals(object, merger.merge(null, object));
    }

    @Test
    public void testMergeIdentity() throws InterruptedException {
        assertEquals(object, merger.merge(object, object));
    }

}
