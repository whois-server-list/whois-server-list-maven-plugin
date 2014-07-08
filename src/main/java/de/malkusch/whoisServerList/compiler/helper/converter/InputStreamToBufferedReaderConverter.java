package de.malkusch.whoisServerList.compiler.helper.converter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamToBufferedReaderConverter implements Converter<InputStream, BufferedReader> {

	@Override
	public BufferedReader convert(InputStream input) {
		return new BufferedReader(new InputStreamReader(input));
	}

}
