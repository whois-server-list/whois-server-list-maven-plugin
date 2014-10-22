package de.malkusch.whoisServerList.compiler.filter;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.cache.Cache;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.helper.comparator.WhoisServerComparator;

/**
 * Filter domains.
 *
 * @author markus@malkusch.de
 * @param <T>  the domain type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
class DomainFilter<T extends Domain> implements Filter<T> {

    /**
     * The timeout in seconds.
     */
    private final int timeout;

    /**
     * The name filter.
     */
    private final StringFilter nameFilter;

    /**
     * The query cache.
     */
    private final Cache<String, String> cache;

    /**
     * The sort comparator for the whois servers.
     */
    private final WhoisServerComparator comparator;

    /**
     * A query for an unavailable object.
     */
    private static final String UNAVAILABLE_QUERY = "hST4vcMRppEPgENMHD2";

    /**
     * The known patterns.
     */
    private final List<Pattern> patterns;

    /**
     * Sets the timeout.
     *
     * @param timeout   the timeout in seconds
     * @param patterns  the known patterns, not null
     * @param cache     the query cache, not null
     */
    DomainFilter(final int timeout, @Nonnull final List<Pattern> patterns,
            @Nonnull final Cache<String, String> cache) {

        this.patterns = patterns;
        this.timeout = timeout;
        this.cache = cache;
        this.nameFilter = new StringFilter();
        this.comparator = new WhoisServerComparator();
    }

    @Override
    public final T filter(final T domain)
            throws InterruptedException {

        if (domain == null) {
            return null;

        }
        @SuppressWarnings("unchecked")
        T filtered = (T) domain.clone();

        filtered.setName(nameFilter.filter(domain.getName()));

        String query
                = String.format("%s.%s", UNAVAILABLE_QUERY, domain.getName());

        WhoisServerFilter serverFilter
                = new WhoisServerFilter(query, timeout, patterns, cache);

        ListFilter<WhoisServer> listFilter = new ListFilter<>(serverFilter);

        List<WhoisServer> filteredServers
                = listFilter.filter(domain.getWhoisServers());

        // Sort the servers
        Collections.sort(filteredServers, comparator);

        filtered.setWhoisServers(filteredServers);

        filtered = filterDomain(filtered);

        return filtered;
    }

    /**
     * Filter domain.
     *
     * Subclasses may overwrite this method.
     *
     * @param domain  the domain
     * @return the filtered domain
     */
    T filterDomain(final T domain) {
        return domain;
    }

}
