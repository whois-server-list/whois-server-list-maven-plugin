package de.malkusch.whoisServerList.compiler.helper.converter;

import net.jcip.annotations.ThreadSafe;

/**
 * Converter without throwing an exception.
 *
 * @author markus@malkusch.de
 *
 * @param <S>  the source type
 * @param <T>  the converted type
 *
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@ThreadSafe
public interface Converter<S, T> extends ThrowableConverter<S, T, Throwable> {

    /**
     * Converts a type into another type without throwing an exception.
     *
     * @param value  the source value
     * @return  the converted value
     */
    @Override
    T convert(S value);

}
