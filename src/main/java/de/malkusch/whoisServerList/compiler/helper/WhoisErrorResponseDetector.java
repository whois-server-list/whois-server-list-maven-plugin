package de.malkusch.whoisServerList.compiler.helper;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.malkusch.whoisServerList.api.v1.model.DomainList;

public final class WhoisErrorResponseDetector {

    private final Collection<Pattern> errorPatterns;

    public WhoisErrorResponseDetector(final DomainList list) {
        this(Stream
                .concat(list.getDomains().stream().flatMap(domain -> domain.getWhoisServers().stream()),
                        list.getDomains().stream()
                                .flatMap(tld -> tld.getDomains().stream()
                                        .flatMap(domain -> domain.getWhoisServers().stream())))
                .flatMap(server -> server.getErrorPatterns().stream()).distinct().collect(Collectors.toList()));
    }

    public WhoisErrorResponseDetector(final Collection<Pattern> errorPatterns) {
        this.errorPatterns = errorPatterns;
    }

    public boolean isError(final String response) {
        return errorPatterns.stream().anyMatch(pattern -> pattern.matcher(response).find());
    }

}
