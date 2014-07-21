package de.malkusch.whoisServerList.compiler.merger;

import java.util.Date;

import net.jcip.annotations.Immutable;

/**
 * Merges from the newer object.
 *
 * @author markus@malkusch.de
 * @param <T> the mergable object type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class NewestMerger<T> implements Merger<T> {

    /**
     * The dominant date.
     */
    private final Date leftDate;

    /**
     * The weaker date.
     */
    private final Date rightDate;

    /**
     * The merger.
     */
    private final Merger<T> merger;

    /**
     * Sets the dates and merger.
     *
     * @param leftDate   the dominant date
     * @param rightDate  the weaker date
     * @param merger     the merger
     */
    NewestMerger(
            final Date leftDate, final Date rightDate, final Merger<T> merger) {

        this.leftDate = leftDate;
        this.rightDate = rightDate;
        this.merger = merger;
    }

    @Override
    public T merge(final T left, final T right) throws InterruptedException {
        T dominant = left;
        T weak = right;

        if (leftDate != null && rightDate != null
                && rightDate.after(leftDate)) {
            dominant = right;
            weak = left;

        }

        return merger.merge(dominant, weak);
    }

}
