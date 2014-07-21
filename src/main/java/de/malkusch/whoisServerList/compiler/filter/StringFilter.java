package de.malkusch.whoisServerList.compiler.filter;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.StringUtils;

/**
 * Trims and filters empty strings.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class StringFilter implements Filter<String> {

    @Override
    public String filter(final String value) {
        String filtered = StringUtils.trim(value);
        if (StringUtils.isEmpty(filtered)) {
            return null;

        } else {
            return filtered;

        }
    }

}
