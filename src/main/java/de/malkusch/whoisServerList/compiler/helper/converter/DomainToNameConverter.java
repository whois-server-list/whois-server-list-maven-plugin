package de.malkusch.whoisServerList.compiler.helper.converter;

import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

/**
 * Converts a domain to its name.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class DomainToNameConverter
        implements Converter<Domain, String> {

    @Override
    public String convert(final Domain value) {
        if (value == null) {
            return null;

        }
        return value.getName();
    }

}
