package de.malkusch.whoisServerList.compiler.helper.existingDomain.google;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

public class GoogleSearchTest {

    private GoogleSearch service;
    
    @Before
    public void setupGoogleSearch() {
        String apiKey = System.getenv("GOOGLE_API_KEY");
        assumeNotNull(apiKey);
        
        String engineId = System.getenv("GOOGLE_ENGINE");
        assumeNotNull(engineId);
        
        service = new GoogleSearch(apiKey, engineId);
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
        domain.setName("adac");
        
        String existing = service.findExistingDomainName(domain).get();
        assertTrue(StringUtils.endsWithIgnoreCase(existing, ".adac"));
    }
    
}
