package de.malkusch.whoisServerList.compiler.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DomainUtil {
	
	private static Set<String> countries;
	
	{
		countries = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
	}

	static public String normalize(String name) {
		return name != null ? name.toLowerCase() : null;
	}
	
	static public boolean isCountryCode(String name) {
		if (name == null) {
			return false;
			
		}
		name = normalize(name);
		return countries.contains(name);
	}
	
}
