package de.malkusch.whoisServerList.compiler.merger;

import net.jcip.annotations.ThreadSafe;

/**
 * Object merger.
 *
 * @author markus@malkusch.de
 * @param <T> the mergable object type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@ThreadSafe
interface Merger<T> {

    /**
     * Merges two objects.
     *
     * The left object is the dominant object.
     *
     * @param left  the dominant object, may be null
     * @param right  the weak object, may be null
     * @return the merged object, or null if both objects were null
     */
    T merge(T left, T right);

}
