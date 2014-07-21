package de.malkusch.whoisServerList.compiler.helper;

import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import net.jcip.annotations.Immutable;

/**
 * Concurrency service.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class ConcurrencyService {

    /**
     * The property key for the concurrency level.
     */
    public static final String PROPERTY_LEVEL = "concurrency.level";

    /**
     * The executor.
     */
    private final Executor executor;

    /**
     * Initializes the concurrency service with the default configuration.
     */
    public ConcurrencyService() {
        this(DomainListCompiler.getDefaultProperties());
    }

    /**
     * Initializes the concurrency service with a configuration.
     *
     * @param properties  the configuration
     */
    public ConcurrencyService(final Properties properties) {
        this.executor = Executors.newFixedThreadPool(
                Integer.parseInt(properties.getProperty(PROPERTY_LEVEL)));
    }

    /**
     * Returns the service provider.
     *
     * @return the service provider
     */
    public static ConcurrencyService getService() {
        return ServiceLoader.load(ConcurrencyService.class).iterator().next();
    }

    /**
     * Returns the executor.
     *
     * @return the executor, not null
     */
    public Executor getExecutor() {
        return executor;
    }

}
