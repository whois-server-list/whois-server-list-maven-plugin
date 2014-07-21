package de.malkusch.whoisServerList.compiler.merger;

import java.net.URL;
import java.util.Properties;

import javax.annotation.concurrent.Immutable;
import de.malkusch.whoisServerList.compiler.helper.converter.DomainToNameConverter;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.model.domain.Domain.State;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Top level domain merger.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class TopLevelDomainMerger extends DomainMerger<TopLevelDomain> {

    /**
     * The country code merger.
     */
    private final StringMerger countryCodeMerger = new StringMerger();

    /**
     * The registration url merger.
     */
    private final URLMerger urlMerger;

    /**
     * The state merger.
     */
    private final NotNullMerger<State> stateMerger = new NotNullMerger<>();

    /**
     * The sub domain merger.
     */
    private final ListMerger<Domain> subdomainMerger = new ListMerger<>(
            new DomainToNameConverter(), new DomainMerger<Domain>());

    /**
     * Constructs the merger.
     *
     * @param properties  the application properties
     */
    public TopLevelDomainMerger(final Properties properties) {
        this.urlMerger = new URLMerger(properties);
    }

    @Override
    protected void completeMerge(final TopLevelDomain merged,
            final TopLevelDomain left, final TopLevelDomain right)
                    throws InterruptedException {

        NewestMerger<URL> newestUrlMerger = new NewestMerger<>(
                left.getChanged(), right.getChanged(), urlMerger);
        merged.setRegistratonService(newestUrlMerger.merge(
                left.getRegistratonService(),
                right.getRegistratonService()));

        merged.setDomains(subdomainMerger.merge(
                left.getDomains(), right.getDomains()));

        NewestMerger<State> newestStateMerger = new NewestMerger<>(
                left.getChanged(), right.getChanged(), stateMerger);
        merged.setState(newestStateMerger.merge(
                left.getState(), right.getState()));

        merged.setCountryCode(countryCodeMerger.merge(
                left.getCountryCode(), right.getCountryCode()));
    }

}
