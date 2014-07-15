package de.malkusch.whoisServerList.compiler.helper.converter;

import net.jcip.annotations.Immutable;

import org.w3c.dom.Node;

/**
 * Converts a Node to its value.
 * 
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class NodeToValueConverter implements Converter<Node, String> {

	@Override
	public String convert(final Node value) {
		return value.getNodeValue();
	}

}
