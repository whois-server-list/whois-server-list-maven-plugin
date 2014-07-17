package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

@RunWith(Parameterized.class)
public class MergerTest {

    @Parameter(0)
    public Merger<Object> merger;

    @Parameter(1)
    public Object object;

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
                { new NotNullMerger<String>(), "test" },
                { new DateMerger(), new Date() },
                { new StringMerger(), "test" },
                { new WhoisServerMerger(), new WhoisServer() },
                { new DomainMerger<Domain>(), new Domain() },
                { new TopLevelDomainMerger<TopLevelDomain>(),
                        new TopLevelDomain() },
                { new CountryCodeTopLevelDomainMerger(),
                        new CountryCodeTopLevelDomain() },
                { new NewestMerger<>(
                        new Date(), new Date(), new StringMerger()), "test" },
                { new ListMerger<>(null, null), Collections.EMPTY_LIST },
        });
    }

    @Test
    public void testMergeNull() {
        assertNull(merger.merge(null, null));
        assertEquals(object, merger.merge(object, null));
        assertEquals(object, merger.merge(null, object));
    }

    @Test
    public void testMergeIdentity() {
        assertEquals(object, merger.merge(object, object));
    }

}
