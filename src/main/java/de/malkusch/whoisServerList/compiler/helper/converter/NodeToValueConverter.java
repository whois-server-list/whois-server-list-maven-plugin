package de.malkusch.whoisServerList.compiler.helper.converter;

import org.w3c.dom.Node;

public class NodeToValueConverter implements Converter<Node, String> {

	@Override
	public String convert(Node value) {
		return value.getNodeValue();
	}

}
