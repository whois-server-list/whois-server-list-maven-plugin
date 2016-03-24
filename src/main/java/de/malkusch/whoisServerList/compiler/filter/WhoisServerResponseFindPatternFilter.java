package de.malkusch.whoisServerList.compiler.filter;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

/**
 * Tries to find an unavailable pattern.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class WhoisServerResponseFindPatternFilter
        implements WhoisServerResponseFilter {

    /**
     * The list of all unavailable patterns.
     */
    private List<Pattern> patterns;

    /**
     * Sets the list of unavailable patterns.
     *
     * @param patterns  The list of all unavailable patterns
     */
    WhoisServerResponseFindPatternFilter(final List<Pattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    @Nullable
    public WhoisServer filter(
            @Nullable final WhoisServer server, final String availableResponse, final Optional<String> unavailableResponse) {
        if (server == null) {
            return null;

        }
        if (server.getAvailablePattern() != null) {
            return server;

        }

        WhoisServer filtered = server.clone();

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(availableResponse);
            if (matcher.find()) {
                
                if (unavailableResponse.isPresent()) {
                    Matcher unavailableMatcher = pattern.matcher(unavailableResponse.get());
                    if (unavailableMatcher.find()) {
                        continue;
                    }
                }
                
                filtered.setAvailablePattern(pattern);
                break;

            }
        }

        return filtered;
    }

}
