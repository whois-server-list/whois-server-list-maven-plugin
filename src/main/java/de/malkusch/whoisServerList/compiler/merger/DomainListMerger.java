package de.malkusch.whoisServerList.compiler.merger;

import java.util.Properties;

import javax.annotation.concurrent.Immutable;
import de.malkusch.whoisServerList.compiler.helper.converter.DomainToNameConverter;
import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Merges DomainList.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class DomainListMerger implements Merger<DomainList> {

    /**
     * The merger for the description.
     */
    private final StringMerger descriptionMerger = new StringMerger();

    /**
     * The merger for the version.
     */
    private final StringMerger versionMerger = new StringMerger();

    /**
     * The merger for the date.
     */
    private final DateMerger dateMerger = new DateMerger();

    /**
     * The domain list merger.
     */
    private final ConcurrentListMerger<TopLevelDomain> domainsMerger;

    /**
     * Constructs the merger.
     *
     * @param properties  the application properties
     */
    public DomainListMerger(final Properties properties) {
        this.domainsMerger
                = new ConcurrentListMerger<>(new DomainToNameConverter(),
                            new TopLevelDomainMerger(properties));
    }

    @Override
    public DomainList merge(final DomainList left, final DomainList right)
            throws InterruptedException {

        if (left == null) {
            return right;

        }
        if (right == null) {
            return left;

        }

        DomainList merged = left.clone();

        merged.setDate(dateMerger.merge(left.getDate(), right.getDate()));

        merged.setDescription(descriptionMerger.merge(
                left.getDescription(), right.getDescription()));

        merged.setDomains(
                domainsMerger.merge(left.getDomains(), right.getDomains()));

        merged.setVersion(
                versionMerger.merge(left.getVersion(), right.getVersion()));

        return merged;
    }

}
