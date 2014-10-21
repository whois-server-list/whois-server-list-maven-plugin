package de.malkusch.whoisServerList.compiler.helper;

import java.io.Closeable;

import javax.annotation.concurrent.Immutable;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

/**
 * Cache Factory.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class CacheFactory implements Closeable {
    
    /**
     * The caching provider.
     */
    private final CachingProvider cachingProvider;
    
    /**
     * The cache manager.
     */
    private final CacheManager cacheManager;
    
    public CacheFactory() {
        cachingProvider = Caching.getCachingProvider();
        cacheManager = cachingProvider.getCacheManager();
    }
    
    /**
     * Builds the query cache.
     * 
     * @return the query cache.
     */
    public Cache<String, String> buildQueryCache() {
        MutableConfiguration<String, String> configuration =
                new MutableConfiguration<>(); 
        configuration.setTypes(String.class, String.class);
        
        return cacheManager.createCache("queryCache", configuration);
    }

    @Override
    public void close() {
        cacheManager.close();
        cachingProvider.close();
    }

}
