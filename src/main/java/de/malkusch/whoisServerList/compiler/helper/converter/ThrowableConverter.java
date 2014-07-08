package de.malkusch.whoisServerList.compiler.helper.converter;

interface ThrowableConverter<S, T, E extends Throwable> {

	public T convert(S value) throws E;
	
}
