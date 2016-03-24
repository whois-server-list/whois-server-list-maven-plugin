package de.malkusch.whoisServerList.compiler.list.whoisrb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
     * The configuration.
     */
    private final URI uri;

    /**
     * The http client.
     */
    private final HttpClient client;

    /**
     * Initialize
     *
     * @param client
     *            the HTTP client.
     * @param uri
     *            the Json URI.
     */
    public WhoisrbDomainListFactory(HttpClient client, final URI uri) {
        this.uri = uri;
        this.client = client;
    }

    @Override
    public DomainList buildList() throws BuildListException, InterruptedException {
        try {

            DomainList list = new DomainList();

            HttpEntity entity = client.execute(new HttpGet(uri)).getEntity();
            Map<String, Zone> zoneMap;
            try (InputStream stream = entity.getContent()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
                TypeReference<HashMap<String, Zone>> typeRef = new TypeReference<HashMap<String, Zone>>() {};
                
                zoneMap = mapper.readValue(stream, typeRef);
            }

            zoneMap.forEach((suffix, zone) -> {
                try {
                    addSuffix(suffix, zone.host());

                } catch (WhoisServerListException | InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            });

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
