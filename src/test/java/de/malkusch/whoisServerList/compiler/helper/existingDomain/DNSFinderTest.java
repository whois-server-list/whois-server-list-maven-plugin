package de.malkusch.whoisServerList.compiler.helper.existingDomain;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

public class DNSFinderTest {

    @Test
    public void shouldFindAnExistingDomain() {
        Domain domain = new Domain();
        domain.setName("org");
        FindExistingDomainService service = new DNSFinder("nonexisting-TgWoouBe", "nic");
        
        Optional<String> existing = service.findExistingDomainName(domain);

        assertEquals(Optional.of("nic.org"), existing);
    }
    
    @Test
    public void shouldNotFindAnyDomain() {
        Domain domain = new Domain();
        domain.setName("org");
        FindExistingDomainService service = new DNSFinder("nonexisting-TgWoouBe", "nonexisting2-TgWoouBe");
        
        assertFalse(service.findExistingDomainName(domain).isPresent());
    }

}
