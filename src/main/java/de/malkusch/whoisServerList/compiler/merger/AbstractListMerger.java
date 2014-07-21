package de.malkusch.whoisServerList.compiler.merger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.compiler.helper.converter.Converter;

/**
 * Merges two lists.
 *
 * @author markus@malkusch.de
 * @param <T> the type of the collection
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
abstract class AbstractListMerger<T> implements Merger<List<T>> {

    /**
     * Converts the element to an id.
     */
    private final Converter<? super T, ?> elementToIdConverter;

    /**
     * The element merger.
     */
    private final Merger<T> elementMerger;

    /**
     * Constructs the merger with an element converter and merger.
     *
     * The element converter should convert the element to an id. This id
     * will be used to match elements between the two lists. The
     * element merger will be used to merge matched elements.
     *
     * @param elementToIdConverter  the element converter
     * @param elementMerger  the element merger
     */
    AbstractListMerger(
            final Converter<? super T, ?> elementToIdConverter,
            final Merger<T> elementMerger) {

        this.elementToIdConverter = elementToIdConverter;
        this.elementMerger = elementMerger;
    }

    @Override
    public final List<T> merge(final List<T> left, final List<T> right)
            throws InterruptedException {

        if (left == null) {
            return right;

        } else if (right == null) {
            return left;

        }
        return mergeMaps(mapCollection(left), mapCollection(right));
    }

    /**
     * Merges two elements.
     *
     * @param left   the dominant element
     * @param right  the weaker element
     *
     * @return the merged element
     * @throws InterruptedException If the thread was interrupted
     */
    final T mergeElement(final T left, final T right)
            throws InterruptedException {

        return elementMerger.merge(left, right);
    }

    /**
     * Merges two lists.
     *
     * @param left   the dominant list, not null
     * @param right  the weaker list, not null
     *
     * @return the merged list, not null
     * @throws InterruptedException If the thread was interrupted
     */
    abstract List<T> mergeMaps(Map<Object, T> left, Map<Object, T> right)
            throws InterruptedException;

    /**
     * Converts the collection to a map.
     *
     * @param collection  the collection
     * @return the converted map
     */
    private Map<Object, T> mapCollection(final Collection<T> collection) {
        Map<Object, T> map = new HashMap<>();
        for (T item : collection) {
            map.put(elementToIdConverter.convert(item), item);

        }
        return map;
    }

}
