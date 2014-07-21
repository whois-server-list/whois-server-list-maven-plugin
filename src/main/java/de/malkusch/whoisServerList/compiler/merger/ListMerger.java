package de.malkusch.whoisServerList.compiler.merger;

import java.util.ArrayList;
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
final class ListMerger<T> implements Merger<List<T>> {

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
    ListMerger(
            final Converter<? super T, ?> elementToIdConverter,
            final Merger<T> elementMerger) {

        this.elementToIdConverter = elementToIdConverter;
        this.elementMerger = elementMerger;
    }

    @Override
    public List<T> merge(final List<T> leftList, final List<T> rightList)
            throws InterruptedException {

        if (leftList == null) {
            return rightList;

        } else if (rightList == null) {
            return leftList;

        }

        List<T> mergedList = new ArrayList<>();
        Map<Object, T> rightMap = mapCollection(rightList);

        // TODO iterate threaded
        for (T left : leftList) {
            Object key = elementToIdConverter.convert(left);
            T right = rightMap.remove(key);

            T merged = elementMerger.merge(left, right);
            mergedList.add(merged);
        }

        mergedList.addAll(rightMap.values());

        return mergedList;
    }

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
