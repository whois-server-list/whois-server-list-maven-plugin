package de.malkusch.whoisServerList.compiler.merger;

import net.jcip.annotations.Immutable;

/**
 * Merges to the not empty string.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class StringMerger implements Merger<String> {

    @Override
    public String merge(final String left, final String right) {
        if (left == null) {
            return right;

        }
        if (right == null) {
            return left;

        }
        if (left.isEmpty()) {
            return right;

        } else {
            return left;

        }
    }

}
