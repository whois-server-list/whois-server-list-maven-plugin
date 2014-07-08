package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.net.whois.WhoisClient;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.list.exception.BuildDomainException;
import de.malkusch.whoisServerList.compiler.list.iana.whois.Parser;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

public class TopLevelDomainFactory {
	
	public static final String KEY_WHOIS = "whois";
	public static final String KEY_CREATED = "created";
	public static final String KEY_CHANGED = "changed";
	public static final String KEY_STATE = "status";

	private Properties properties;

	public TopLevelDomainFactory(Properties properties) {
		this.properties = properties;
	}

	public TopLevelDomain build(String name) throws WhoisServerListException {
		Parser parser = null;
		try {
			name = DomainUtil.normalize(name);
			TopLevelDomain domain;

			if (DomainUtil.isCountryCode(name)) {
				CountryCodeTopLevelDomain countryDomain = new CountryCodeTopLevelDomain();
				countryDomain.setCountryCode(name.toUpperCase());
				domain = countryDomain;

			} else {
				domain = new TopLevelDomain();

			}
			
			domain.setName(name);
			
			WhoisClient whoisClient = new WhoisClient();
			whoisClient.connect(properties.getProperty(IanaDomainListFactory.PROPERTY_WHOIS_HOST));
			InputStream inputStream = whoisClient.getInputStream(name);
			
			parser = new Parser();
			parser.setKeys(KEY_CREATED, KEY_CHANGED, KEY_WHOIS, KEY_STATE);
			parser.parse(inputStream);
			
			domain.setState(parser.getState(KEY_STATE));

			domain.setCreated(parser.getDate(KEY_CREATED));
			
			domain.setChanged(parser.getDate(KEY_CHANGED));
			
			String host = parser.getString(KEY_WHOIS);
			if (host != null) {
				WhoisServer server = new WhoisServer();
				server.setHost(host);
				domain.getWhoisServers().add(server);
				
			}

			return domain;

		} catch (IOException e) {
			throw new BuildDomainException(e);

		} finally {
			try {
				if (parser != null) {
					parser.close();
					
				}
			} catch (IOException e) {
				throw new WhoisServerListException(e);
				
			}
		}
	}

}
