package de.malkusch.whoisServerList.compiler.filter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Filters each item of a list.
 *
 * @author markus@malkusch.de
 * @param <T> the list item type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class ListFilter<T> extends AbstractListFilter<T> {

    /**
     * Sets the item filter.
     *
     * @param filter  the item filter
     */
    ListFilter(final Filter<T> filter) {
        super(filter);
    }

    @Override
    @Nullable
    public List<T> filter(@Nullable final List<T> list)
            throws InterruptedException {

        if (list == null) {
            return null;

        }
        List<T> filteredList = new ArrayList<>();

        for (T item : list) {
            T filteredItem = filterItem(item);
            if (filteredItem != null) {
                filteredList.add(filteredItem);

            }
        }

        return filteredList;
    }

}
