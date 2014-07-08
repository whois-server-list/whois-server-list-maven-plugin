package de.malkusch.whoisServerList.compiler.helper.iterator;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.malkusch.whoisServerList.compiler.helper.converter.Converter;

/**
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class NodeListIterator<N extends Node, T> implements Iterator<T> {
	
	private NodeList nodeList;
	
	private Converter<N, T> converter;
	
	private int i = 0;
	
	public NodeListIterator(NodeList nodeList, Converter<N, T> converter) {
		this.nodeList = nodeList;
		this.converter = converter;
	}

	@Override
	public boolean hasNext() {
		return i < nodeList.getLength();
	}

	@Override
	final public T next() {
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
