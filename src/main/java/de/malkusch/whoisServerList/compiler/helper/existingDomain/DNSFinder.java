package de.malkusch.whoisServerList.compiler.helper.existingDomain;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

final class DNSFinder implements FindExistingDomainService {

    private final String[] knownPrefixes;

    DNSFinder(String... knownPrefixes) {
        this.knownPrefixes = knownPrefixes;
    }

    @Override
    public Optional<String> findExistingDomainName(Domain domain) {
        return Arrays.stream(knownPrefixes).parallel().map(prefix -> String.format("%s.%s", prefix, domain.getName()))
                .filter(candidate -> {
                    try {
                        InetAddress.getByName(candidate);
                        return true;

                    } catch (UnknownHostException e) {
                        return false;
                    }
                }).findAny();
    }

}
