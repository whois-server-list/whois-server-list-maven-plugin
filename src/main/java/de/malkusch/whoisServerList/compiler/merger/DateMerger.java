package de.malkusch.whoisServerList.compiler.merger;

import java.util.Date;

import net.jcip.annotations.Immutable;

/**
 * Merges to the newest date.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class DateMerger implements Merger<Date> {

    @Override
    public Date merge(final Date left, final Date right) {
        if (left == null) {
            return right;

        }
        if (right == null) {
            return left;

        }
        if (right.after(left)) {
            return right;

        } else {
            return left;

        }
    }

}
