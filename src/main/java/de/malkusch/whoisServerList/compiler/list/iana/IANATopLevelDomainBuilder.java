package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain.State;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.ChangedContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.CreatedContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.ResponseContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.StateContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.ValueContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.WhoisContext;
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
     * Exception holder
     */
    private InterruptedException interruptedException;
    
    /**
     * Exception holder
     */
    private WhoisServerListException exception;

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

        try {
            interruptedException = null;
            exception            = null;
            
            String charset   = properties.getProperty(IanaDomainListFactory.PROPERTY_WHOIS_CHARSET);
            String whoisHost = properties.getProperty(IanaDomainListFactory.PROPERTY_WHOIS_HOST);
            
            client.connect(whoisHost);
    
            try (InputStream stream = client.getInputStream(false, domain.getName(), charset)) {
                
                IanaWhoisLexer lexer   = new IanaWhoisLexer(new ANTLRInputStream(stream));
                IanaWhoisParser parser = new IanaWhoisParser(new CommonTokenStream(lexer));
                
                ResponseContext ctx    = parser.response();
                ParseTreeWalker walker = new ParseTreeWalker();
    
                walker.walk(new IanaWhoisBaseListener() {
    
                    private final List<URL> urls = new ArrayList<>();
    
                    private String value;
                    
                    @Override
                    public void enterValue(ValueContext ctx) {
                        value = ctx.getText();
                        findUrls(ctx.getText());
                    }
                    
                    @Override
                    public void exitWhois(WhoisContext ctx) {
                        try {
                            serverBuilder.setHost(value);
                            WhoisServer server = serverBuilder.build();
                            domain.getWhoisServers().add(server);
                            
                        } catch (WhoisServerListException e) {
                            exception = e;
                            
                        } catch (InterruptedException e) {
                            interruptedException = e;
                        }
                    }
                    
                    @Override
                    public void exitCreated(CreatedContext ctx) {
                        domain.setCreated(convertStringToDate(value));
                    }
                    
                    @Override
                    public void exitChanged(ChangedContext ctx) {
                        domain.setChanged(convertStringToDate(value));
                    }
                    
                    @Override
                    public void exitState(StateContext ctx) {
                        domain.setState(convertStringToState(value));
                    }
                    
                    @Override
                    public void exitResponse(ResponseContext ctx) {
                        if (urls.size() == 1) {
                            domain.setRegistrationService(urls.get(0));
                        }
                    }
                    
                    private void findUrls(String value) {
                        Pattern urlPattern = Pattern.compile("(https?://\\S+)(\\s|$)", Pattern.CASE_INSENSITIVE);
                        Matcher urlMatcher = urlPattern.matcher(value);
                        while (urlMatcher.find()) {
                            String url = urlMatcher.group(1);
                            try {
                                urls.add(new URL(url));
                                
                            } catch (MalformedURLException e) {
                                LOGGER.warn("found invalid URL: {} for {}", url, domain);
                            }
                        }
                    }
                    
                    /**
                     * Returns the value as a {@code Date} from a whois result for a key.
                     *
                     * @param key  the whois result key, not null
                     * @return the whois result value as Date, or null
                     */
                    private Date convertStringToDate(final String date) {
                        try {
                            if (date == null) {
                                return null;

                            }
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            return dateFormat.parse(date);

                        } catch (ParseException e) {
                            exception = new WhoisServerListException(e);
                            return null;
                        }
                    }
                    
                    /**
                     * Returns the value as a {@code State} from a whois result for a key.
                     *
                     * @param key  the whois result key, not null
                     * @return the whois result value as state, or null
                     */
                    private State convertStringToState(final String state) {
                        if (state == null) {
                            return null;

                        }
                        switch (state) {

                        case "ACTIVE":
                            return State.ACTIVE;

                        case "NEW":
                            return State.NEW;

                        case "INACTIVE":
                            return State.INACTIVE;

                        default:
                            exception = new WhoisServerListException(String.format(
                                    "unexpected state %s", state));
                            return null;
                        }
                    }
                    
                }, ctx);

                if (interruptedException != null) {
                    throw interruptedException;

                }
                if (exception != null) {
                    throw exception;
                    
                }
            }
        } catch (IOException e) {
            throw new WhoisServerListException(e);
        }
    }
    
}
