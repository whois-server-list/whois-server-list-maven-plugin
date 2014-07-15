package de.malkusch.whoisServerList.compiler.test;

import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

public final class TestUtil {
    
    private TestUtil() {
    }

    public static Domain buildSimpleDomain(final String name) {
        Domain domain = new Domain();
        domain.setName(name);
        return domain;
    }
    
    public static TopLevelDomain buildSimpleTld(final String name) {
        TopLevelDomain domain = new TopLevelDomain();
        domain.setName(name);
        return domain;
    }
    
    public static CountryCodeTopLevelDomain buildSimpleCcTld(
            final String name, final String countryCode) {

        CountryCodeTopLevelDomain domain = new CountryCodeTopLevelDomain();
        domain.setName(name);
        domain.setCountryCode(countryCode);
        return domain;
    }

}
