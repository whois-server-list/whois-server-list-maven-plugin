package de.malkusch.whoisServerList.compiler.filter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

/**
 * Filter domains.
 *
 * @author markus@malkusch.de
 * @param <T>  the domain type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class DomainFilter<T extends Domain> implements Filter<T> {

    /**
     * The timeout in seconds.
     */
    private final int timeout;

    /**
     * The name filter
     */
    private final StringFilter nameFilter;

    /**
     * The whois server filter.
     */
    private final WhoisServerFilter whoisServerFilter;

    /**
     * A query for an unavailable object.
     */
    private static final String UNAVAILABLE_QUERY = "hST4vcMRppEPgENMHD2";

    /**
     * Sets the timeout.
     *
     * @param timeout  the timeout in seconds
     */
    DomainFilter(final int timeout) {
        this.whoisServerFilter = new WhoisServerFilter(timeout);
        this.timeout = timeout;
        this.nameFilter = new StringFilter();
    }

    @Override
    public T filter(final T domain)
            throws InterruptedException {

        if (domain == null) {
            return null;

        }
        @SuppressWarnings("unchecked")
        T filtered = (T) domain.clone();

        filtered.setName(nameFilter.filter(domain.getName()));

        // Whois server Filter chain
        List<Filter<WhoisServer>> chain = new ArrayList<>();

        // accessible
        chain.add(whoisServerFilter);

        // pattern
        String query
            = String.format("%s.%s", UNAVAILABLE_QUERY, domain.getName());
        chain.add(new WhoisServerPatternFilter(query, timeout));

        ListFilter<WhoisServer> listFilter
                = new ListFilter<>(new FilterChain<>(chain));
        filtered.setWhoisServers(listFilter.filter(domain.getWhoisServers()));

        return filtered;
    }

}
