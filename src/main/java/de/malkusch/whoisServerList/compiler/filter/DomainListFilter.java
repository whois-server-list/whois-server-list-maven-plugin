package de.malkusch.whoisServerList.compiler.filter;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisApi.WhoisApi;
import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.helper.WhoisErrorResponseDetector;
import de.malkusch.whoisServerList.compiler.helper.comparator.DomainComparator;
import de.malkusch.whoisServerList.compiler.helper.converter.DomainListToWhoisServerListConverter;
import de.malkusch.whoisServerList.compiler.helper.converter.WhoisServerListToOrderedPatternListConverter;

/**
 * Filter domains.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class DomainListFilter implements Filter<DomainList> {

    /**
     * The comparator for sorting the domains.
     */
    private final DomainComparator comparator;

    /**
     * The Whois API.
     */
    private final WhoisApi whoisApi;

    /**
     * Sets the whois filter timeout.
     *
     * @param whoisApi
     *            the Whois API
     */
    public DomainListFilter(@Nonnull final WhoisApi whoisApi) {
        this.whoisApi = whoisApi;
        comparator = new DomainComparator();
    }

    @Override
    public DomainList filter(final DomainList domainList) throws InterruptedException {

        if (domainList == null) {
            return null;

        }

        List<Pattern> patterns = getPatterns(domainList);

        TopLevelDomainFilter domainFilter = new TopLevelDomainFilter(patterns, new WhoisErrorResponseDetector(domainList), whoisApi);

        AbstractListFilter<TopLevelDomain> domainsFilter = new ConcurrentListFilter<>(domainFilter);

        DomainList filtered = domainList.clone();

        List<TopLevelDomain> domains = domainsFilter.filter(domainList.getDomains());

        Collections.sort(domains, comparator);

        filtered.setDomains(domains);

        return filtered;
    }

    /**
     * Returns the known pattern list.
     *
     * @param domainList
     *            The domain list.
     * @return the pattern list.
     */
    private List<Pattern> getPatterns(final DomainList domainList) {
        DomainListToWhoisServerListConverter listConverter = new DomainListToWhoisServerListConverter();

        WhoisServerListToOrderedPatternListConverter patternConverter = new WhoisServerListToOrderedPatternListConverter();

        List<WhoisServer> servers = listConverter.convert(domainList.getDomains());

        return patternConverter.convert(servers);
    }

}
