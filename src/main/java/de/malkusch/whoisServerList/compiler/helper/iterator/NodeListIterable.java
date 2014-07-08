package de.malkusch.whoisServerList.compiler.helper.iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.malkusch.whoisServerList.compiler.helper.converter.Converter;

/**
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class NodeListIterable<N extends Node, T> implements Iterable<T> {
	
	private NodeList nodeList;
	
	private Converter<N, T> converter;
	
	public NodeListIterable(NodeList nodeList, Converter<N, T> converter) {
		this.nodeList = nodeList;
		this.converter = converter;
	}

	@Override
	public NodeListIterator<N, T> iterator() {
		return new NodeListIterator<N, T>(nodeList, converter);
	}

}
