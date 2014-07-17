package de.malkusch.whoisServerList.compiler.merger;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.compiler.helper.converter.WhoisServerToHostConverter;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;

/**
 * Abstract domain merger.
 *
 * @author markus@malkusch.de
 * @param <T> the mergable domain type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public class DomainMerger<T extends Domain> implements Merger<T> {

    /**
     * The whois server list merger.
     */
    private final ListMerger<WhoisServer> whoisListMerger
        = new ListMerger<>(new WhoisServerToHostConverter(),
                new WhoisServerMerger());

    /**
     * The date merger.
     */
    private final DateMerger dateMerger = new DateMerger();

    /**
     * The name merger.
     */
    private final StringMerger nameMerger = new StringMerger();

    @Override
    public final T merge(final T left, final T right) {
        if (left == null) {
            return right;

        }
        if (right == null) {
            return left;

        }

        @SuppressWarnings("unchecked")
        T merged = (T) left.clone();

        merged.setName(nameMerger.merge(left.getName(), right.getName()));

        merged.setChanged(
                dateMerger.merge(left.getChanged(), right.getChanged()));

        merged.setCreated(
                dateMerger.merge(left.getCreated(), right.getCreated()));

        merged.setWhoisServers(whoisListMerger.merge(
                left.getWhoisServers(), right.getWhoisServers()));

        completeMerge(merged, left, right);

        return merged;
    }

    /**
     * Completes the merging.
     *
     * @param merged  the merged object
     * @param left    the dominant object
     * @param right   the weak object
     */
    protected void completeMerge(
            final T merged, final T left, final T right) {
    }

}
