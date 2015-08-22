package de.malkusch.whoisServerList.compiler.list.whoisrb;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue.ValueType;

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
import de.malkusch.whoisServerList.publicSuffixList.util.DomainUtil;

/**
 * This factory builds from the Ruby Whois' list.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="http://whoisrb.org/">Ruby Whois</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
final public class WhoisrbDomainListFactory implements DomainListFactory{

    /**
     * The whois server attribute of the JSON object. 
     */
    private static final String HOST = "host";
    
    /**
     * The builded Top Level Domains.
     */
    private final Map<String, TopLevelDomain> topLevelDomains = new HashMap<>();

    /**
     * The top level domain builder.
     */
    private final TopLevelDomainBuilder tldBuilder
        = new TopLevelDomainBuilder(Source.WHOIS_RB);
    
    /**
     * The domain builder.
     */
    private final DomainBuilder<Domain> domainBuilder
        = new DomainBuilder<>(Source.WHOIS_RB, Domain.class);
    
    /**
     * The whois server builder.
     */
    private final WhoisServerBuilder whoisServerBuilder
        = new WhoisServerBuilder(Source.WHOIS_RB);
    
    /**
     * The configuration.
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
     * @param uri     the Json URI.
     */
    public WhoisrbDomainListFactory(HttpClient client, final URI uri) {
        this.uri    = uri;
        this.client = client;
    }
    
    @Override
    public DomainList buildList()
            throws BuildListException, InterruptedException {
        
        try {
            DomainList list   = new DomainList();
            HttpEntity entity = client.execute(new HttpGet(uri)).getEntity();

            try (JsonReader reader = Json.createReader(entity.getContent())) {
                reader.readObject().forEach((suffix, value) -> {
                    try {
                        if (value.getValueType() != ValueType.OBJECT) {
                            return;
                        }
                        
                        String name = suffix.replaceAll("^\\.", "");
                        String[] labels = DomainUtil.splitLabels(name);
    
                        TopLevelDomain topLevelDomain
                            = getTopLevelDomain(labels[labels.length - 1]);
                        Domain domain = topLevelDomain;
    
                        if (ArrayUtils.getLength(labels) > 1) {
                            domainBuilder.setName(name);
                            domain = domainBuilder.build();
        
                            topLevelDomain.getDomains().add(domain);
                        }
                        
                        JsonObject object = (JsonObject) value;
                        String host = object.getString(HOST, null);
                        if (host != null) {
                            whoisServerBuilder.setHost(host);
                            domain.getWhoisServers().add(whoisServerBuilder.build());
                        }

                    } catch (WhoisServerListException | InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                });
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
        return Source.WHOIS_RB;
    }

}
