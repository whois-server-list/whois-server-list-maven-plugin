package de.malkusch.whoisServerList.compiler.helper.converter;

import net.jcip.annotations.ThreadSafe;

/**
 * Converter.
 * 
 * @author markus@malkusch.de
 * 
 * @param <S>  the source type
 * @param <T>  the converted type
 * @param <E>  the conversion exception type
 * 
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@ThreadSafe
interface ThrowableConverter<S, T, E extends Throwable> {

    /**
     * Converts a type into another type. 
     *
     * @param value  the source value
     * @return  the converted value
     * @throws E If conversion failed
     */
	public T convert(S value) throws E;
	
}
