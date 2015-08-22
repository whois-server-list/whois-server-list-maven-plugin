package de.malkusch.whoisServerList.compiler.list.mdwhois;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.DomainBuilder;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.TopLevelDomainBuilder;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.WhoisServerBuilder;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.DocContext;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.HostContext;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.SuffixContext;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.TldContext;
import de.malkusch.whoisServerList.publicSuffixList.util.DomainUtil;

/**
 * This factory builds from Marco d'Itri's list.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://github.com/rfc1036/whois">Marco d'Itri's whois client</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
final public class MDWhoisDomainListFactory implements DomainListFactory{

    /**
     * The builded Top Level Domains.
     */
    private final Map<String, TopLevelDomain> topLevelDomains = new HashMap<>();

    /**
     * The top level domain builder.
     */
    private final TopLevelDomainBuilder tldBuilder
        = new TopLevelDomainBuilder(Source.MD_WHOIS);
    
    /**
     * The domain builder.
     */
    private final DomainBuilder<Domain> domainBuilder
        = new DomainBuilder<>(Source.MD_WHOIS, Domain.class);
    
    /**
     * The whois server builder.
     */
    private final WhoisServerBuilder whoisServerBuilder
        = new WhoisServerBuilder(Source.MD_WHOIS);
    
    /**
     * The uri.
     */
    private final URI uri;

    /**
     * The HTTP client
     */
    private final HttpClient client;
    
    /**
     * Initialize
     *
     * @param client  the HTTP client.
     * @param uri     the whois list URI.
     */
    public MDWhoisDomainListFactory(HttpClient client, final URI uri) {
        this.uri    = uri;
        this.client = client;
    }
    
    @Override
    public DomainList buildList()
            throws BuildListException, InterruptedException {
        
        try {
            DomainList list   = new DomainList();
            HttpEntity entity = client.execute(new HttpGet(uri)).getEntity();

            try (InputStream stream = entity.getContent()) {
                MDWhoisLexer lexer       = new MDWhoisLexer(new ANTLRInputStream(stream));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                MDWhoisParser parser     = new MDWhoisParser(tokens);
                
                DocContext ctx = parser.doc();
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(new MDWhoisBaseListener(){
                    
                    private String suffix;
                    
                    @Nullable
                    private String host;
                    
                    @Override
                    public void enterSuffix(SuffixContext ctx) {
                        suffix = ctx.getText();
                    }
                    
                    @Override
                    public void enterHost(HostContext ctx) {
                        host = ctx.getText();
                    }
                    
                    @Override
                    public void exitTld(TldContext ctx) {
                        try {
                            suffix = suffix.replaceAll("^\\.", "");
                            
                            String[] labels = DomainUtil.splitLabels(suffix);
                            
                            TopLevelDomain topLevelDomain
                                = getTopLevelDomain(labels[labels.length - 1]);
                            Domain domain = topLevelDomain;
        
                            if (ArrayUtils.getLength(labels) > 1) {
                                domainBuilder.setName(suffix);
                                domain = domainBuilder.build();
            
                                topLevelDomain.getDomains().add(domain);
                            }
                            
                            if (host != null) {
                                whoisServerBuilder.setHost(host);
                                domain.getWhoisServers().add(whoisServerBuilder.build());
                            }
                            
                        } catch (WhoisServerListException | InterruptedException e) {
                            throw new IllegalStateException(e);
                            
                        } finally {
                            suffix = null;
                            host   = null;
                        }
                    }
                    
                }, ctx);
            }

            list.getDomains().addAll(topLevelDomains.values());
            return list;
            
        } catch (IOException e) {
            throw new BuildListException(e);
        }
    }

    /**
     * Returns the top level domain.
     *
     * If the top level domain does not exist in {@link #topLevelDomains} it
     * will be created and stored and that map.
     *
     * @param name  the domain name, not null
     * @return the top level domain, not null
     */
    private TopLevelDomain getTopLevelDomain(final String name) {

        return this.topLevelDomains.computeIfAbsent(name, key -> {
            try {
                this.tldBuilder.setName(key);
                TopLevelDomain domain = this.tldBuilder.build();
                return domain;

            } catch (WhoisServerListException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
    }
    
    @Override
    public Source getSource() {
        return Source.MD_WHOIS;
    }

}
