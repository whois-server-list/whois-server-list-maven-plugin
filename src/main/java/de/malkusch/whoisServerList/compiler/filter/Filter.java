package de.malkusch.whoisServerList.compiler.filter;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Filter.
 *
 * @author markus@malkusch.de
 * @param <T> the filtered type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@ThreadSafe
interface Filter<T> {

    /**
     * Returns whether a value should be included.
     *
     * @param value  the filtered value, null returns always false
     * @return {@code true} if the value should be included
     */
    boolean isValid(@Nullable T value);

}
