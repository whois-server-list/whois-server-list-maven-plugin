package de.malkusch.whoisServerList.compiler.merger;

import net.jcip.annotations.Immutable;

/**
 * Merges to the object which is not null.
 *
 * @author markus@malkusch.de
 * @param <T> the mergable object type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class NotNullMerger<T> implements Merger<T> {

    @Override
    public T merge(final T left, final T right) {
        if (left == null) {
            return right;

        }
        return left;
    }

}
