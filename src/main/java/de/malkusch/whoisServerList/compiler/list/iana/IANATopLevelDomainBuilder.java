package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.exception.BuildDomainException;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.TopLevelDomainBuilder;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.WhoisServerBuilder;

/**
 * Builder for TopLevelDomain.
 *
 * This factory builds the top level domain from whois information.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class IANATopLevelDomainBuilder extends TopLevelDomainBuilder {

    /**
     * Whois key for the whois server.
     */
    public static final String KEY_WHOIS = "whois";

    /**
     * Whois key for the created date.
     */
    public static final String KEY_CREATED = "created";

    /**
     * Whois key for the changed date.
     */
    public static final String KEY_CHANGED = "changed";

    /**
     * Whois key for the state.
     */
    public static final String KEY_STATE = "status";

    /**
     * Factory properties.
     */
    private final Properties properties;
    
    /**
     * The Whois client.
     */
    private final WhoisClient client;

    /**
     * The whois server builder.
     */
    private final WhoisServerBuilder serverBuilder
        = new WhoisServerBuilder(Source.IANA);

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(IANATopLevelDomainBuilder.class);

    /**
     * Constructs the factory.
     *
     * @param client      the Whois client, not null
     * @param properties  the factory properties, not null
     */
    IANATopLevelDomainBuilder(final WhoisClient client, final Properties properties) {
        super(Source.IANA);

        this.properties = properties;
        this.client     = client;
    }

    @Override
    protected void completeTopLevelDomain(final TopLevelDomain domain)
            throws WhoisServerListException, InterruptedException {

        try (Parser parser = new Parser()) {
            String whoisHost = properties.getProperty(
                    IanaDomainListFactory.PROPERTY_WHOIS_HOST);
            client.connect(whoisHost);

            InputStream inputStream
                    = client.getInputStream(domain.getName());

            parser.setKeys(KEY_CREATED, KEY_CHANGED, KEY_WHOIS, KEY_STATE);

            String charset = properties.getProperty(
                    IanaDomainListFactory.PROPERTY_WHOIS_CHARSET);
            parser.parse(inputStream, Charset.forName(charset));

            domain.setState(parser.getState(KEY_STATE));

            domain.setCreated(parser.getDate(KEY_CREATED));

            domain.setChanged(parser.getDate(KEY_CHANGED));

            if (parser.getURLs().size() == 1) {
                domain.setRegistrationService(parser.getURLs().get(0));

            } else {
                LOGGER.info(
                    "found {} Url(s) for {}", parser.getURLs().size(), domain);

            }

            String host = parser.getString(KEY_WHOIS);
            if (host != null) {
                serverBuilder.setHost(host);
                WhoisServer server = serverBuilder.build();
                domain.getWhoisServers().add(server);

            } else {
                LOGGER.info("found no whois server for {}", domain);

            }

        } catch (IOException e) {
            throw new BuildDomainException(e);

        }
    }

}
