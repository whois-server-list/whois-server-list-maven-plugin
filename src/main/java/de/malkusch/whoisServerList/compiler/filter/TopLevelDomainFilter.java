package de.malkusch.whoisServerList.compiler.filter;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisApi.WhoisApi;
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
     * Sets the dependencies.
     *
     * @param patterns
     *            the known patterns, not null
     * @param whoisApi
     *            Whois API, not null
     */
    TopLevelDomainFilter(@Nonnull final List<Pattern> patterns, @Nonnull final WhoisApi whoisApi) {

        super(patterns, whoisApi);

        this.comparator = new DomainComparator();
    }

    @Override
    TopLevelDomain filterDomain(final TopLevelDomain domain) {
        Collections.sort(domain.getDomains(), comparator);

        return domain;
    }

}
