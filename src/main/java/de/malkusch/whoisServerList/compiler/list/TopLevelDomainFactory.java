package de.malkusch.whoisServerList.compiler.list;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Factory for TopLevelDomain.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public class TopLevelDomainFactory {

    /**
     * Completes a created domain.
     *
     * Subclasses may overwrite this method to complete created domains.
     *
     * @param domain the incomplete top level domain
     * @throws WhoisServerListException If completing failed
     */
    protected void completeDomain(final TopLevelDomain domain)
            throws WhoisServerListException {
    }

    /**
     * Builds a top level domain.
     *
     * @param domainName  the case insensitive domain name, null returns null
     * @return the top level domain
     * @throws WhoisServerListException If building failed
     */
    public final TopLevelDomain build(final String domainName)
            throws WhoisServerListException {

        if (domainName == null) {
            return null;

        }
        String name = DomainUtil.normalize(domainName);

        TopLevelDomain domain;

        if (DomainUtil.isCountryCode(name)) {
            CountryCodeTopLevelDomain countryDomain
                = new CountryCodeTopLevelDomain();
            countryDomain.setCountryCode(name.toUpperCase());
            domain = countryDomain;

        } else {
            domain = new TopLevelDomain();

        }

        domain.setName(name);

        this.completeDomain(domain);

        return domain;
    }

}
