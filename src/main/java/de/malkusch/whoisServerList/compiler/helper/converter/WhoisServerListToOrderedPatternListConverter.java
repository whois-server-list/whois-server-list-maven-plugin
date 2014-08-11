package de.malkusch.whoisServerList.compiler.helper.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

/**
 * Converts a list of whois serves into an ordered list of available patterns.
 *
 * The list is ordered by the pattern's frequency. The most frequent
 * pattern is the first.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class WhoisServerListToOrderedPatternListConverter
        implements Converter<List<WhoisServer>, List<Pattern>> {

    /**
     * Map entry for storing the pattern and its frequency.
     *
     * @author markus@malkusch.de
     * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
     */
    @ThreadSafe
    private static final class PatternFrequency {

        /**
         * The available pattern.
         */
        private Pattern pattern;

        /**
         * The frequency of the pattern.
         */
        private int frequency;

    }

    @Override
    public List<Pattern> convert(final List<WhoisServer> servers) {
        if (servers == null) {
            return null;

        }

        Map<String, PatternFrequency> patternMap = new HashMap<>();
        for (WhoisServer server : servers) {
            if (server.getAvailablePattern() == null) {
                continue;

            }
            String pattern = server.getAvailablePattern().pattern();
            PatternFrequency patternFrequency = patternMap.get(pattern);
            if (patternFrequency == null) {
                patternFrequency = new PatternFrequency();
                patternFrequency.pattern = server.getAvailablePattern();
                patternMap.put(pattern, patternFrequency);

            }
            patternFrequency.frequency++;

        }

        Comparator<PatternFrequency> comparator 
                = new Comparator<PatternFrequency>() {

            @Override
            public int compare(
                    final PatternFrequency o1, final PatternFrequency o2) {
                return Integer.compare(o1.frequency, o2.frequency);
            }
        };

        List<PatternFrequency> patternFrequencies
                = new ArrayList<>(patternMap.values());
        Collections.sort(
                patternFrequencies, Collections.reverseOrder(comparator));

        List<Pattern> patterns = new ArrayList<>();
        for (PatternFrequency patternFrequency : patternFrequencies) {
            patterns.add(patternFrequency.pattern);

        }

        return patterns;
    }
}
