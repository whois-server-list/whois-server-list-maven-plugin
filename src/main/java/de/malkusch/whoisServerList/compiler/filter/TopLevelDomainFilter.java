package de.malkusch.whoisServerList.compiler.filter;

import java.util.Collections;

import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.helper.comparator.DomainComparator;

/**
 * Filter top level domains.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class TopLevelDomainFilter extends DomainFilter<TopLevelDomain> {

    /**
     * The comparator for sorting the domains.
     */
    private final DomainComparator comparator;

    /**
     * Sets the timeout.
     *
     * @param timeout  the timeout in seconds
     */
    TopLevelDomainFilter(final int timeout) {
        super(timeout);

        this.comparator = new DomainComparator();
    }

    @Override
    TopLevelDomain filterDomain(TopLevelDomain domain) {
        Collections.sort(domain.getDomains(), comparator);

        return domain;
    }

}
