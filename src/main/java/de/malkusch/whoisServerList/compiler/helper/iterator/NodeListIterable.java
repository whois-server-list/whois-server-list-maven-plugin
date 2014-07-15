package de.malkusch.whoisServerList.compiler.helper.iterator;

import net.jcip.annotations.Immutable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.malkusch.whoisServerList.compiler.helper.converter.Converter;

/**
 * Converted Iterable for a NodeList.
 *
 * @author markus@malkusch.de
 *
 * @param <N>  the node type
 * @param <T>  the converted node type
 *
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class NodeListIterable<N extends Node, T> implements Iterable<T> {
	
    /**
     * The node list.
     */
	private final NodeList nodeList;
	
	/**
	 * The node converter.
	 */
	private final Converter<N, T> converter;
	
	/**
	 * Sets the node list and the node converter.
	 *
	 * @param nodeList   the node list
	 * @param converter  the node converter
	 */
	public NodeListIterable(
	        final NodeList nodeList, final Converter<N, T> converter) {

		this.nodeList = nodeList;
		this.converter = converter;
	}

	@Override
	public NodeListIterator<N, T> iterator() {
		return new NodeListIterator<N, T>(nodeList, converter);
	}

}
