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
     * Returns the filtered value.
     *
     * @param value  the value, may be null
     * @return the filtered value, may be null
     */
    @Nullable T filter(@Nullable T value);

}
