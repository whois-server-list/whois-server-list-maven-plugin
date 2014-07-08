package de.malkusch.whoisServerList.compiler.list.iana.whois;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.converter.InputStreamToBufferedReaderConverter;
import de.malkusch.whoisServerList.compiler.model.domain.Domain.State;

public class Parser implements Closeable {
	
	final public static String STATE_ACTIVE = "ACTIVE";
	final public static String STATE_INACTIVE = "INACTIVE";
	private static final String STATE_NEW = "NEW";

	private String[] keys;

	private DateFormat dateFormat;

	private Map<String, String> result;

	private BufferedReader reader;

	public Parser() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	}

	public void setKeys(String... keys) {
		this.keys = keys;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	public void parse(BufferedReader reader) throws IOException {
		this.reader = reader;
		result = new HashMap<>();
		String regex = String.format("^(%s):\\s+(\\S.*\\S)\\s*$",
				StringUtils.join(keys, "|"));
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		String line;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				continue;

			}
			String key = matcher.group(1);
			String value = matcher.group(2);
			result.put(key, value);

		}
	}

	public void parse(InputStream stream) throws IOException {
		parse(new InputStreamToBufferedReaderConverter().convert(stream));
	}

	public String getString(String key) {
		return result.get(key);
	}

	public State getState(String key) throws WhoisServerListException {
		String state = getString(key);
		if (state == null) {
			return null;
			
		}
		switch (state) {
		
		case STATE_ACTIVE:
			return State.ACTIVE;
			
		case STATE_NEW:
			return State.NEW;
			
		case STATE_INACTIVE:
			return State.INACTIVE;
			
		default:
			throw new WhoisServerListException(String.format(
					"unexpected state %s", state));

		}
	}

	public Date getDate(String key) throws WhoisServerListException {
		try {
			String date = getString(key);
			if (date == null) {
				return null;

			}
			return dateFormat.parse(date);

		} catch (ParseException e) {
			throw new WhoisServerListException(e);

		}
	}

}
