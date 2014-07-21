package de.malkusch.whoisServerList.compiler.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;
import de.malkusch.whoisServerList.compiler.helper.converter.Converter;

/**
 * Merges two lists.
 *
 * @author markus@malkusch.de
 * @param <T> the type of the collection
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class ListMerger<T> extends AbstractListMerger<T> {

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
    ListMerger(
            final Converter<? super T, ?> elementToIdConverter,
            final Merger<T> elementMerger) {

        super(elementToIdConverter, elementMerger);
    }

    @Override
    List<T> mergeMaps(final Map<Object, T> leftMap,
            final Map<Object, T> rightMap) throws InterruptedException {

        List<T> mergedList = new ArrayList<>();
        for (Entry<Object, T> entry : leftMap.entrySet()) {
            T right = rightMap.remove(entry.getKey());
            T merged = mergeElement(entry.getValue(), right);
            mergedList.add(merged);
        }
        mergedList.addAll(rightMap.values());

        return mergedList;
    }

}
