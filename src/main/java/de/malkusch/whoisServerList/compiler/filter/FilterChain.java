package de.malkusch.whoisServerList.compiler.filter;

import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

/**
 * Filter.
 *
 * @author markus@malkusch.de
 * @param <T> the filtered type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class FilterChain<T> implements Filter<T> {

    /**
     * The filter chain.
     */
    private final Iterable<Filter<T>> chain;

    /**
     * Sets the filter chain.
     *
     * @param chain  the filter chain
     */
    FilterChain(final Iterable<Filter<T>> chain) {
        this.chain = chain;
    }

    /**
     * Sets the filter chain.
     *
     * @param chain  the filter chain
     */
    @SafeVarargs
    FilterChain(final Filter<T>... chain) {
        this(Arrays.asList(chain));
    }

    @Override
    public T filter(final T value) throws InterruptedException {
        T filtered = value;
        for (Filter<T> filter : chain) {
            if (Thread.interrupted()) {
                throw new InterruptedException();

            }
            filtered = filter.filter(filtered);

        }
        return filtered;
    }

}
