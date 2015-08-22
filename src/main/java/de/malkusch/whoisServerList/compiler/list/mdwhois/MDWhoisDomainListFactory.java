package de.malkusch.whoisServerList.compiler.list.mdwhois;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.AbstractDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.DocContext;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.HostContext;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.SuffixContext;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisParser.TldContext;

/**
 * This factory builds from Marco d'Itri's list.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://github.com/rfc1036/whois">Marco d'Itri's whois client</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
final public class MDWhoisDomainListFactory extends AbstractDomainListFactory {
    
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
                            addSuffix(suffix, host);
                            
                        } catch (WhoisServerListException | InterruptedException e) {
                            throw new IllegalStateException(e);
                            
                        } finally {
                            suffix = null;
                            host   = null;
                        }
                    }
                    
                }, ctx);
            }

            list.getDomains().addAll(getDomains());
            return list;
            
        } catch (IOException e) {
            throw new BuildListException(e);
        }
    }
    
    @Override
    public Source getSource() {
        return Source.MD_WHOIS;
    }

}
