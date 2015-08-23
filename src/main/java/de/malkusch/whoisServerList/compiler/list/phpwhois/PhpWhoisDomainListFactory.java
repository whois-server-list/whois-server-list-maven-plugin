package de.malkusch.whoisServerList.compiler.list.phpwhois;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.quercus.QuercusEngine;
import com.caucho.quercus.env.ArrayValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.ValueType;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.AbstractDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;

/**
 * This factory builds from phpWhois' list.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://raw.githubusercontent.com/phpWhois/phpWhois/master/src/whois.servers.php">phpWhois</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
final public class PhpWhoisDomainListFactory extends AbstractDomainListFactory {
    
    /**
     * The key of the whois server list.
     */
    private static final String LIST_KEY = "WHOIS_SPECIAL";
    
    /**
     * The uri.
     */
    private final URI uri;
    
    /**
     * The amount of seconds until execution starts.
     */
    private final int seconds;

    /**
     * The HTTP client
     */
    private final HttpClient client;
    
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * Initialize
     *
     * @param client  the HTTP client.
     * @param uri     the whois list URI.
     * @param seconds the amount of seconds until execution starts.
     */
    public PhpWhoisDomainListFactory(HttpClient client, final URI uri, int seconds) {
        this.uri     = uri;
        this.client  = client;
        this.seconds = seconds;
    }
    
    @Override
    public DomainList buildList()
            throws BuildListException, InterruptedException {
        
        try {
            DomainList list   = new DomainList();
            HttpEntity entity = client.execute(new HttpGet(uri)).getEntity();

            try (InputStream stream = entity.getContent()) {
                 // Oh yes, I'm really executing PHP from the internet. Shoot me!
                logger.warn("In {} seconds I'm going to execute PHP from {}.", seconds, uri);
                Thread.sleep(seconds * 1000);
                
                QuercusEngine engine = new QuercusEngine();
                engine.setOutputStream(new NullOutputStream());
                ArrayValue array = castArray(engine.execute(IOUtils.toString(stream)));
                ArrayValue listArray = castArray(array.get(StringValue.create(LIST_KEY)));
                
                listArray.entrySet().stream().forEach(entry -> {
                    String suffix = entry.getKey().toString();
                    
                    String host = entry.getValue().toString();
                    if (StringUtils.isEmpty(host)) {
                        host = null;
                    } else if (StringUtils.contains(host, "://")) {
                        host = null;
                    }
                    
                    try {
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
    
    /**
     * Casts an value to an array.
     * 
     * @param value The array
     * @return The array.
     */
    private ArrayValue castArray(Value value) throws BuildListException {
        if (value.getValueType() != ValueType.ARRAY) {
            throw new BuildListException("An array was expected.");
        }
        return (ArrayValue) value;
    }
    
    @Override
    public Source getSource() {
        return Source.PHP_WHOIS;
    }

}
