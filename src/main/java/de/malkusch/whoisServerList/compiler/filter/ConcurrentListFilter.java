package de.malkusch.whoisServerList.compiler.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import javax.annotation.concurrent.Immutable;

import de.malkusch.whoisServerList.compiler.helper.ConcurrencyService;

/**
 * Filters each item of a list concurrently.
 *
 * @author markus@malkusch.de
 * @param <T>
 *            the list item type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class ConcurrentListFilter<T> extends AbstractListFilter<T> {

    /**
     * The executor.
     */
    private final Executor executor;

    /**
     * Sets the item filter.
     *
     * @param filter
     *            the item filter
     */
    ConcurrentListFilter(final Filter<T> filter) {
        super(filter);

        executor = ConcurrencyService.getService().getExecutor();
    }

    @Override
    public List<T> filter(final List<T> list) throws InterruptedException {
        try {
            if (list == null) {
                return null;

            }

            List<T> filteredList = new ArrayList<T>();
            List<FutureTask<T>> tasks = new ArrayList<>();

            for (final T item : list) {
                FutureTask<T> task = new FutureTask<>(new Callable<T>() {

                    @Override
                    public T call() throws InterruptedException {
                        return filterItem(item);
                    }

                });
                tasks.add(task);
                executor.execute(task);
            }

            for (FutureTask<T> task : tasks) {
                T filtered = task.get();
                if (filtered != null) {
                    filteredList.add(filtered);

                }

            }

            return filteredList;

        } catch (ExecutionException e) {
            if (e.getCause() == null) {
                throw new RuntimeException(e);

            }
            if (e.getCause() instanceof InterruptedException) {
                throw (InterruptedException) e.getCause();

            } else {
                throw new RuntimeException(e.getCause());

            }
        }
    }

}
