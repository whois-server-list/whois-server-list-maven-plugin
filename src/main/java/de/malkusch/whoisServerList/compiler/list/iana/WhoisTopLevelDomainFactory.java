package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import net.jcip.annotations.Immutable;

import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.TopLevelDomainFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildDomainException;
import de.malkusch.whoisServerList.compiler.list.iana.whois.Parser;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Factory for TopLevelDomain.
 *
 * This factory builds the top level domain from whois information.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class WhoisTopLevelDomainFactory extends TopLevelDomainFactory {
	
    /**
     * Whois key for the whois server
     */
	public static final String KEY_WHOIS = "whois";
	
	/**
     * Whois key for the created date
     */
	public static final String KEY_CREATED = "created";
	
	/**
     * Whois key for the changed date
     */
	public static final String KEY_CHANGED = "changed";
	
	/**
     * Whois key for the state
     */
	public static final String KEY_STATE = "status";

	/**
	 * Factory properties
	 */
	private final Properties properties;
	
	/**
	 * Logger
	 */
	private final static Logger logger
	    = LoggerFactory.getLogger(WhoisTopLevelDomainFactory.class);

	/**
	 * Constructs the factory.
	 *
	 * @param properties  the factory properties, not null
	 */
	public WhoisTopLevelDomainFactory(final Properties properties) {
		this.properties = properties;
	}

	@Override
	public TopLevelDomain build(final String name)
	        throws WhoisServerListException {

		try (Parser parser = new Parser()) {
			TopLevelDomain domain = super.build(name);

			String whoisHost = properties.getProperty(
                    IanaDomainListFactory.PROPERTY_WHOIS_HOST);
			WhoisClient whoisClient = new WhoisClient();
			whoisClient.connect(whoisHost);
			
			InputStream inputStream = whoisClient.getInputStream(name);
			
			parser.setKeys(KEY_CREATED, KEY_CHANGED, KEY_WHOIS, KEY_STATE);
			
			String charset= properties.getProperty(IanaDomainListFactory.PROPERTY_WHOIS_CHARSET);
			parser.parse(inputStream, Charset.forName(charset));
			
			domain.setState(parser.getState(KEY_STATE));

			domain.setCreated(parser.getDate(KEY_CREATED));
			
			domain.setChanged(parser.getDate(KEY_CHANGED));
			
			if (parser.getURLs().size() == 1) {
				domain.setRegistratonService(parser.getURLs().get(0));
				
			} else {
				logger.warn(
			        "found {} Url(s) for {}", parser.getURLs().size(), domain);
				
			}
			
			String host = parser.getString(KEY_WHOIS);
			if (host != null) {
				WhoisServer server = new WhoisServer();
				server.setHost(host);
				domain.getWhoisServers().add(server);
				
			} else {
				logger.warn("found no whois server for {}", domain);
				
			}

			return domain;

		} catch (IOException e) {
			throw new BuildDomainException(e);

		}
	}

}
