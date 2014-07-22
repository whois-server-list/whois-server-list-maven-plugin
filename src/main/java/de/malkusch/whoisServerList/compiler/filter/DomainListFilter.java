package de.malkusch.whoisServerList.compiler.filter;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.PropertyKey;
import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.helper.comparator.DomainComparator;

/**
 * Filter domains.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class DomainListFilter implements Filter<DomainList> {

    /**
     * The top level domain list filter.
     */
    private final AbstractListFilter<TopLevelDomain> domainFilter;

    /**
     * The comparator for sorting the domains.
     */
    private final DomainComparator comparator;

    /**
     * The configuration porperty name for the whois filter timeout.
     *
     * @see #timeout
     */
    @PropertyKey
    private static final String PROPERTY_TIMEOUT = "filter.whois.timeout";

    /**
     * Sets the whois filter timeout.
     *
     * @param timeout  the timeout in seconds
     */
    public DomainListFilter(final int timeout) {
        domainFilter
                = new ConcurrentListFilter<>(new TopLevelDomainFilter(timeout));

        comparator = new DomainComparator();
    }

    /**
     * Sets the whois filter timeout.
     *
     * @param properties  the application properties
     * @see #PROPERTY_TIMEOUT
     */
    public DomainListFilter(final Properties properties) {
        this(Integer.parseInt(properties.getProperty(PROPERTY_TIMEOUT)));
    }

    @Override
    public DomainList filter(final DomainList domainList)
            throws InterruptedException {

        if (domainList == null) {
            return null;

        }
        DomainList filtered = domainList.clone();

        List<TopLevelDomain> domains
                = domainFilter.filter(domainList.getDomains());

        Collections.sort(domains, comparator);

        filtered.setDomains(domains);

        return filtered;
    }


}
