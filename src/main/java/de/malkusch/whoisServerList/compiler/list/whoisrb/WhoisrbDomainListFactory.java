package de.malkusch.whoisServerList.compiler.list.whoisrb;

import java.io.IOException;
import java.net.URI;

import javax.annotation.concurrent.NotThreadSafe;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue.ValueType;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.AbstractDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;

/**
 * This factory builds from the Ruby Whois' list.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="http://whoisrb.org/">Ruby Whois</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
final public class WhoisrbDomainListFactory extends AbstractDomainListFactory {

    /**
     * The whois server attribute of the JSON object. 
     */
    private static final String HOST = "host";
    
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
                        
                        JsonObject object = (JsonObject) value;
                        String host = object.getString(HOST, null);
                        
                        addSuffix(suffix, host);

                    } catch (WhoisServerListException | InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
            
            list.getDomains().addAll(getDomains());
            return list;
            
        } catch (IOException e) {
            throw new BuildListException(e);
        }
    }
    
    @Override
    public Source getSource() {
        return Source.WHOIS_RB;
    }

}
