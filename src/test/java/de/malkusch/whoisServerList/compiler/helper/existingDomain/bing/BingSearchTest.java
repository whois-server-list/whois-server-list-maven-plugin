package de.malkusch.whoisServerList.compiler.helper.existingDomain.bing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

public class BingSearchTest {

    private BingSearch service;

    @Before
    public void setupGoogleSearch() {
        String apiKey = System.getenv("BING_API_KEY");
        assumeNotNull(apiKey);

        service = new BingSearch(apiKey);
    }

    @Test
    public void shouldNotFindAnyDomain() {
        Domain domain = new Domain();
        domain.setName("invalidTgWoouBe");

        assertFalse(service.findExistingDomainName(domain).isPresent());
    }

    @Test
    public void shouldFindAnyDomain() {
        Domain domain = new Domain();
        domain.setName("dev");

        String existing = service.findExistingDomainName(domain).get();
        assertTrue(StringUtils.endsWithIgnoreCase(existing, ".dev"));
    }

}
