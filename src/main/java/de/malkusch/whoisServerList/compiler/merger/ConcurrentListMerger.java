package de.malkusch.whoisServerList.compiler.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.compiler.helper.ConcurrencyService;
import de.malkusch.whoisServerList.compiler.helper.converter.Converter;

/**
 * Merges two lists concurrently.
 *
 * @author markus@malkusch.de
 * @param <T> the type of the collection
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class ConcurrentListMerger<T> extends AbstractListMerger<T> {

    /**
     * The executor.
     */
    private final Executor executor;

    /**
     * Constructs the merger with an element converter and merger.
     *
     * The element converter should convert the element to an id. This id
     * will be used to match elements between the two lists. The
     * element merger will be used to merge matched elements.
     *
     * @param elementToIdConverter  the element converter
     * @param elementMerger         the element merger
     */
    ConcurrentListMerger(
            final Converter<? super T, ?> elementToIdConverter,
            final Merger<T> elementMerger) {

        super(elementToIdConverter, elementMerger);

        executor = ConcurrencyService.getService().getExecutor();
    }

    @Override
    List<T> mergeMaps(final Map<Object, T> leftMap,
            final Map<Object, T> rightMap) throws InterruptedException {

        final ConcurrentHashMap<Object, T> threadsafeRightMap
                = new ConcurrentHashMap<>(rightMap);

        List<FutureTask<T>> tasks = new ArrayList<>();
        for (final Entry<Object, T> entry : leftMap.entrySet()) {
            FutureTask<T> task = new FutureTask<>(new Callable<T>() {

                @Override
                public T call() throws InterruptedException {
                    T right = threadsafeRightMap.remove(entry.getKey());
                    return mergeElement(entry.getValue(), right);
                }

            });
            tasks.add(task);
            executor.execute(task);
        }

        List<T> mergedList = new ArrayList<>();
        for (FutureTask<T> task : tasks) {
            try {
                mergedList.add(task.get());

            } catch (ExecutionException e) {
                if (e.getCause() == null) {
                    throw new RuntimeException(e);

                }
                if (e.getCause() instanceof InterruptedException) {
                    throw (InterruptedException) e.getCause();

                } else {
                    throw new RuntimeException(e);

                }
            }
        }

        mergedList.addAll(threadsafeRightMap.values());

        return mergedList;
    }

}
