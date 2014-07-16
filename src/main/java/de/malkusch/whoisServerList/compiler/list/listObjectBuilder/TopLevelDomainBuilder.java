package de.malkusch.whoisServerList.compiler.list.listObjectBuilder;

import net.jcip.annotations.NotThreadSafe;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Factory for TopLevelDomain.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public class TopLevelDomainBuilder
        extends AbstractDomainBuilder<TopLevelDomain> {

    /**
     * Sets the source for this factory.
     *
     * All created objects of this factory will have this source.
     *
     * @param source  the source
     */
    public TopLevelDomainBuilder(final Source source) {
        super(source);
    }

    /**
     * Completes a created domain.
     *
     * Subclasses may overwrite this method to complete created domains.
     *
     * @param domain the incomplete top level domain
     * @throws WhoisServerListException If completing failed
     */
    protected void completeTopLevelDomain(final TopLevelDomain domain)
            throws WhoisServerListException {
    }

    @Override
    protected final void completeDomain(final TopLevelDomain domain)
            throws WhoisServerListException {

        if (domain instanceof CountryCodeTopLevelDomain) {
            CountryCodeTopLevelDomain countryDomain
                = (CountryCodeTopLevelDomain) domain;

            countryDomain.setCountryCode(domain.getName().toUpperCase());
        }

        completeTopLevelDomain(domain);
    }

    @Override
    protected final Class<? extends TopLevelDomain> getObjectType() {
        if (DomainUtil.isCountryCode(DomainUtil.normalize(getName()))) {
            return CountryCodeTopLevelDomain.class;

        } else {
            return TopLevelDomain.class;

        }
    }

}
