package de.malkusch.whoisServerList.compiler.helper.converter;

public interface Converter<S, T> extends ThrowableConverter<S, T, Throwable> {
	
	@Override
	public T convert(S value);

}
