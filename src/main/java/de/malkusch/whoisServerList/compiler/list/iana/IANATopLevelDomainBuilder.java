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
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.ChangedDateContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.CreatedDateContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.ResponseContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.StateContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.ValueContext;
import de.malkusch.whoisServerList.compiler.list.iana.IanaWhoisParser.WhoisHostContext;
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

    private final class DomainBuilder extends IanaWhoisBaseListener {
        
        private final Logger logger
            = LoggerFactory.getLogger(IANATopLevelDomainBuilder.class);
        
        private final TopLevelDomain domain;
        
        private final List<URL> urls = new ArrayList<>();
        
        private final WhoisServerBuilder serverBuilder
            = new WhoisServerBuilder(Source.IANA);
        
        /**
         * Exception holder
         */
        private InterruptedException interruptedException;
        
        /**
         * Exception holder
         */
        private WhoisServerListException exception;

        private DomainBuilder(TopLevelDomain domain) {
            this.domain = domain;
        }

        @Override
        public void enterWhoisHost(WhoisHostContext ctx) {
            try {
                serverBuilder.setHost(ctx.getText());
                WhoisServer server = serverBuilder.build();
                domain.getWhoisServers().add(server);
                
            } catch (WhoisServerListException e) {
                exception = e;
                
            } catch (InterruptedException e) {
                interruptedException = e;
            }
        }

        @Override
        public void enterCreatedDate(CreatedDateContext ctx) {
            domain.setCreated(convertStringToDate(ctx.getText()));
        }

        @Override
        public void enterChangedDate(ChangedDateContext ctx) {
            domain.setChanged(convertStringToDate(ctx.getText()));
        }

        @Override
        public void enterState(StateContext ctx) {
            if (ctx.NEW() != null) {
                domain.setState(State.NEW);
                
            } else if (ctx.ACTIVE() != null) {
                domain.setState(State.ACTIVE);
                
            } else if (ctx.INACTIVE() != null) {
                domain.setState(State.INACTIVE);
            }
        }

        @Override
        public void enterValue(ValueContext ctx) {
            findUrls(ctx.getText());
        }

        @Override
        public void exitResponse(ResponseContext ctx) {
            if (urls.size() == 1) {
                domain.setRegistrationService(urls.get(0));
            }
        }

        private void checkForErrors() throws InterruptedException, WhoisServerListException {
            if (interruptedException != null) {
                throw interruptedException;
            }
            if (exception != null) {
                throw exception;
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
                    logger.warn("found invalid URL: {} for {}", url, domain);
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
    }

    /**
     * The whois server's charset.
     */
    private final String charset;
    
    /**
     * The whois server host name.
     */
    private final String host;
    
    /**
     * The Whois client.
     */
    private final WhoisClient client;

    /**
     * Constructs the factory.
     *
     * @param client  the Whois client, not null
     * @param host    the whois host name, not null
     * @param charset the charset of the whois response, not null
     */
    IANATopLevelDomainBuilder(final WhoisClient client, final String host, final String charset) {
        super(Source.IANA);

        this.host    = host;
        this.charset = charset;
        this.client  = client;
    }

    @Override
    protected void completeTopLevelDomain(final TopLevelDomain domain)
            throws WhoisServerListException, InterruptedException {

        try {
            client.connect(host);
    
            try (InputStream stream = client.getInputStream(false, domain.getName(), charset)) {
                
                IanaWhoisLexer lexer   = new IanaWhoisLexer(new ANTLRInputStream(stream));
                IanaWhoisParser parser = new IanaWhoisParser(new CommonTokenStream(lexer));
                ResponseContext ctx    = parser.response();
                ParseTreeWalker walker = new ParseTreeWalker();
                DomainBuilder listener = new DomainBuilder(domain);
                
                walker.walk(listener, ctx);
                listener.checkForErrors();
            }

        } catch (IOException e) {
            throw new WhoisServerListException(e);
        }
    }
    
}
