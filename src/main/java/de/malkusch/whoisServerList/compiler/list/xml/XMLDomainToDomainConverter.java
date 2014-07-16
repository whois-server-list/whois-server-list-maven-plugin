package de.malkusch.whoisServerList.compiler.list.xml;

import java.net.MalformedURLException;
import java.net.URL;

import net.jcip.annotations.Immutable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.api.v0.model.Domain;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.helper.converter.Converter;
import de.malkusch.whoisServerList.compiler.list.TopLevelDomainFactory;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Converts a XML Domain.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class XMLDomainToDomainConverter
        implements Converter<Domain, TopLevelDomain> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(XMLDomainToDomainConverter.class);

    /**
     * The top level domain factory.
     */
    private final TopLevelDomainFactory tldFactory
        = new TopLevelDomainFactory();

    @Override
    public TopLevelDomain convert(final Domain domain) {
        try {
            TopLevelDomain tld = this.tldFactory.build(domain.getName());

            if (!StringUtils.isEmpty(domain.getNic())) {
                try {
                    tld.setRegistratonService(new URL(domain.getNic()));

                } catch (MalformedURLException e) {
                    LOGGER.warn("NIC {} for domain {} was invalid.",
                            domain.getNic(), domain.getName());
                }
            }

            if (!StringUtils.isEmpty(domain.getCountry())) {
                if (!(tld instanceof CountryCodeTopLevelDomain)) {
                    LOGGER.warn("domain {} with country {} is no CCTLD.",
                            domain.getName(), domain.getCountry());
                }
            }

            for (Domain subdomain : domain.getDomain()) {
                de.malkusch.whoisServerList.compiler.model.domain.Domain sld;
                sld = new
                    de.malkusch.whoisServerList.compiler.model.domain.Domain();

                sld.setName(DomainUtil.normalize(subdomain.getName()));

                tld.getDomains().add(sld);
            }

            return tld;

        } catch (WhoisServerListException e) {
            throw new RuntimeException(e);
        }
    }

}
