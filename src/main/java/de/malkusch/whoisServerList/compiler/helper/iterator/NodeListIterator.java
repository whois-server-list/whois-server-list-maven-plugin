package de.malkusch.whoisServerList.compiler.helper.iterator;

import java.util.Iterator;

import net.jcip.annotations.NotThreadSafe;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.malkusch.whoisServerList.compiler.helper.converter.Converter;

/**
 * Convertable Node list iterator.
 *
 * @author markus@malkusch.de
 *
 * @param <N>  the node type
 * @param <T>  the converted node type
 *
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public final class NodeListIterator<N extends Node, T> implements Iterator<T> {

    /**
     * The node list.
     */
    private final NodeList nodeList;

    /**
     * The node converter.
     */
    private final Converter<N, T> converter;

    /**
     * The iterator state.
     */
    private int i = 0;

    /**
     * Initializes the iterator with a node list and a node converter.
     *
     * @param nodeList   the node list
     * @param converter  the node converter
     */
    public NodeListIterator(
            final NodeList nodeList, final Converter<N, T> converter) {

        this.nodeList = nodeList;
        this.converter = converter;
    }

    @Override
    public boolean hasNext() {
        return i < nodeList.getLength();
    }

    @Override
    public T next() {
        @SuppressWarnings("unchecked")
        N next = (N) nodeList.item(i);
        i++;
        return converter.convert(next);
    }

    @Override
    public void remove() {
        // Not implemented
    }

}
