package de.malkusch.whoisServerList.compiler.test;

import javax.cache.Cache;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.malkusch.whoisServerList.compiler.helper.CacheFactory;

public class CacheRule implements MethodRule {

    private Cache<String, String> cache;
    
    public Cache<String, String> getQueryCache() {
        return cache;
    }
    
    @Override
    public Statement apply(final Statement base, final FrameworkMethod method,
            final Object target) {
        
        final CacheRule rule = this;
        
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try (CacheFactory cacheFactory = new CacheFactory();
                        Cache<String, String> cache =
                                cacheFactory.buildQueryCache()) {

                    rule.cache = cache;
                    base.evaluate();
                    cacheFactory.getManager().destroyCache(cache.getName());
                }
            }
            
        };
    }

}
