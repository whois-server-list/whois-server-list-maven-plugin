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
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.DomainBuilder;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.TopLevelDomainBuilder;
import de.malkusch.whoisServerList.compiler.model.Source;
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

    @Override
    public TopLevelDomain convert(final Domain domain) {
        try {
            TopLevelDomainBuilder tldBuilder
                = new TopLevelDomainBuilder(Source.XML);
            tldBuilder.setName(domain.getName());
            TopLevelDomain tld = tldBuilder.build();

            if (!StringUtils.isEmpty(domain.getNic())) {
                try {
                    tld.setRegistratonService(new URL(domain.getNic()));

                } catch (MalformedURLException e) {
                    LOGGER.warn("NIC {} for domain {} was invalid.",
                            domain.getNic(), domain.getName());
                }
            }

            if (DomainUtil.isCountryCode(tld.getName())) {
                tld.setCountryCode(tld.getName().toUpperCase());

            }

            if (domain.getCountry() != null) {
                if (tld.getCountryCode() == null) {
                    LOGGER.warn("domain {} with country {} is no CCTLD.",
                            domain.getName(), domain.getCountry());
                }
            } else {
                if (tld.getCountryCode() != null) {
                    LOGGER.warn("domain {} should have a country code.",
                            domain.getName());
                }

            }

            DomainBuilder<?> domainBuilder = new DomainBuilder<>(Source.XML,
                de.malkusch.whoisServerList.compiler.model.domain.Domain.class);
            for (Domain subdomain : domain.getDomain()) {
                domainBuilder.setName(subdomain.getName());
                tld.getDomains().add(domainBuilder.build());
            }

            return tld;

        } catch (WhoisServerListException e) {
            throw new RuntimeException(e);
        }
    }

}
