package de.malkusch.whoisServerList.compiler.merger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;

public class CountryCodeTopLevelDomainMergerTest {

    @Test
    public void testMerge() {
        CountryCodeTopLevelDomainMerger merger
                = new CountryCodeTopLevelDomainMerger();

        CountryCodeTopLevelDomain left = new CountryCodeTopLevelDomain();
        left.setCountryCode(null);

        CountryCodeTopLevelDomain right = new CountryCodeTopLevelDomain();
        right.setCountryCode("de");

        CountryCodeTopLevelDomain expected = new CountryCodeTopLevelDomain();
        expected.setCountryCode("de");

        assertEquals(expected, merger.merge(left, right));
    }

}
