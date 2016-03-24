package de.malkusch.whoisServerList.compiler.helper.existingDomain;

import java.util.List;
import java.util.Optional;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

final class AggregatedFinder implements FindExistingDomainService {

    private final List<FindExistingDomainService> services;

    AggregatedFinder(List<FindExistingDomainService> services) {
        this.services = services;
    }

    @Override
    public Optional<String> findExistingDomainName(Domain domain) {
        for (FindExistingDomainService service : services) {
            Optional<String> existing = service.findExistingDomainName(domain);
            if (existing.isPresent()) {
                return existing;
            }
        }
        return Optional.empty();
    }

}
