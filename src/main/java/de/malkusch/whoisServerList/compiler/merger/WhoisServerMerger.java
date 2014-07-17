package de.malkusch.whoisServerList.compiler.merger;

import java.util.regex.Pattern;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;

/**
 * Merges Whois server.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class WhoisServerMerger implements Merger<WhoisServer> {

    /**
     * The merger for the host name.
     */
    private final StringMerger stringMerger = new StringMerger();

    /**
     * The merger for the available pattern.
     */
    private final NotNullMerger<Pattern> patternMerger = new NotNullMerger<>();

    @Override
    public WhoisServer merge(final WhoisServer left, final WhoisServer right) {
        if (left == null) {
            return right;

        }
        if (right == null) {
            return left;

        }

        WhoisServer merged = left.clone();
        merged.setHost(stringMerger.merge(left.getHost(), right.getHost()));
        merged.setAvailablePattern(patternMerger.merge(
                left.getAvailablePattern(), right.getAvailablePattern()));
        return merged;
    }

}
