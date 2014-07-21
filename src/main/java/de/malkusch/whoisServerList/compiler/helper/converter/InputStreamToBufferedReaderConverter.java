package de.malkusch.whoisServerList.compiler.helper.converter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.annotation.concurrent.Immutable;

/**
 * InputStream to BufferedReader Converter.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class InputStreamToBufferedReaderConverter implements
        Converter<InputStream, BufferedReader> {

    /**
     * The character encoding.
     */
    private final Charset charset;

    /**
     * Sets the character encoding.
     *
     * @param charset  the character encoding
     */
    public InputStreamToBufferedReaderConverter(final Charset charset) {
        this.charset = charset;
    }

    @Override
    public BufferedReader convert(final InputStream input) {
        return new BufferedReader(new InputStreamReader(input, charset));
    }

}
