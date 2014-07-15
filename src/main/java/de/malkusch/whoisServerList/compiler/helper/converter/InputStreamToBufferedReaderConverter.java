package de.malkusch.whoisServerList.compiler.helper.converter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.jcip.annotations.Immutable;

/**
 * InputStream to BufferedReader Converter.
 * 
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class InputStreamToBufferedReaderConverter implements
        Converter<InputStream, BufferedReader> {

    @Override
    public BufferedReader convert(final InputStream input) {
        return new BufferedReader(new InputStreamReader(input));
    }

}
