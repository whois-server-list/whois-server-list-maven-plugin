package de.malkusch.whoisServerList.compiler.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.cache.Cache;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;
import de.malkusch.whoisServerList.compiler.test.CacheRule;

public class WhoisServerFilterCacheTest {

    @Rule
    public CacheRule rule = new CacheRule();
    
    private WhoisServer iana;
    
    private Cache<String, String> cache;
    
    @Before
    public void setIana() {
        Properties properties = DomainListCompiler.getDefaultProperties();
        
        iana = new WhoisServer();
        iana.setHost(
            properties.getProperty(IanaDomainListFactory.PROPERTY_WHOIS_HOST));
    }
    
    @Before
    public void setCache() {
        this.cache = rule.getQueryCache();
    }
    
    /**
     * A query populates the cache.
     */
    @Test
    public void queryPopulatesCache() {
        buildFilter("org").filter(iana);
        assertNotNull(cache.get(iana.getHost()));
    }

    /**
     * Different Queries on same server produce only one cache entry.
     */
    @Test
    public void differentQueriesOnSameServer() {
        buildFilter("org").filter(iana);
        String cachedResponse = cache.get(iana.getHost());
        buildFilter("com").filter(iana);
        
        assertEquals(cachedResponse, cache.get(iana.getHost()));
    }
    
    /**
     * Same Queries on different server produce new cache entries.
     */
    @Test
    public void sameQueriesOnDifferentServer() {
        WhoisServerFilter filter = buildFilter("example.org");
        
        filter.filter(iana);
        
        WhoisServer denic = new WhoisServer();
        denic.setHost("whois.nic.de");
        filter.filter(denic);
        
        assertNotNull(cache.get(iana.getHost()));
        assertNotNull(cache.get(denic.getHost()));
        assertNotEquals(cache.get(iana.getHost()), cache.get(denic.getHost()));
    }
    
    private WhoisServerFilter buildFilter(String query) {
        WhoisServerFilter filter = new WhoisServerFilter(
                query, 5, new ArrayList<Pattern>(), cache);
        return filter;
    }

}
