package de.malkusch.whoisServerList.compiler.helper.existingDomain;

import java.util.ArrayList;
import java.util.List;

import de.malkusch.whoisServerList.compiler.helper.existingDomain.bing.BingSearch;
import de.malkusch.whoisServerList.compiler.helper.existingDomain.google.GoogleSearch;

public final class FindExistingDomainServiceFactory {

    public FindExistingDomainService build() {
        List<FindExistingDomainService> services = new ArrayList<>();

        services.add(new DNSFinder("nic", "example", "test", "sex", "www", "google", "facebook", "porn", "porno"));
        services.add(new BingSearch(System.getProperty("bing.apiKey")));
        services.add(new GoogleSearch(System.getProperty("google.apiKey"), System.getProperty("google.engine")));

        return new AggregatedFinder(services);
    }
}
