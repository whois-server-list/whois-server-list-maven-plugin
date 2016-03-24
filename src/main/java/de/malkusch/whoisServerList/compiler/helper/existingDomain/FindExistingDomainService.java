package de.malkusch.whoisServerList.compiler.helper.existingDomain;

import java.util.Optional;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

public interface FindExistingDomainService {

    public Optional<String> findExistingDomainName(Domain domain);
    
}
