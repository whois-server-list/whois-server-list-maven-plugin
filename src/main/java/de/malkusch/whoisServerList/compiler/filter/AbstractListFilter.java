package de.malkusch.whoisServerList.compiler.filter;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Filters each item of a list.
 *
 * @author markus@malkusch.de
 * @param <T> the list item type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@ThreadSafe
abstract class AbstractListFilter<T> implements Filter<List<T>> {

    /**
     * The item filter.
     */
    private final Filter<T> filter;

    /**
     * Sets the item filter.
     *
     * @param filter  the item filter
     */
    AbstractListFilter(final Filter<T> filter) {
        this.filter = filter;
    }

    /**
     * Filters a list item.
     *
     * @param item  a list item, may be null
     * @return the filtered item, or null
     * @throws InterruptedException if the thread was interrupted
     */
    @Nullable T filterItem(@Nullable final T item) throws InterruptedException {
        return filter.filter(item);
    }

}
