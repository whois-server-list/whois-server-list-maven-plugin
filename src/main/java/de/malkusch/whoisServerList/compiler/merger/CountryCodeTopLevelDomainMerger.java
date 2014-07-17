package de.malkusch.whoisServerList.compiler.merger;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;

/**
 * Country code top level domain merger.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class CountryCodeTopLevelDomainMerger
        extends TopLevelDomainMerger<CountryCodeTopLevelDomain> {

    /**
     * The country code merger.
     */
    private final StringMerger countryCodeMerger = new StringMerger();

    /**
     * Completes the merging.
     *
     * @param merged  the merged object
     * @param left    the dominant object
     * @param right   the weak object
     */
    @Override
    protected void completeMergeTld(final CountryCodeTopLevelDomain merged,
            final CountryCodeTopLevelDomain left,
            final CountryCodeTopLevelDomain right) {

        merged.setCountryCode(countryCodeMerger.merge(
                left.getCountryCode(), right.getCountryCode()));
    }

}
