package de.malkusch.whoisServerList.compiler.helper.converter;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.jcip.annotations.Immutable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.iterator.NodeListIterable;

/**
 * Converts a Document to a string iterator for a given xpath expression.
 * 
 * @author markus@malkusch.de
 * @param <T> the source type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class DocumentToStringIteratorConvertor<T> implements
		ThrowableConverter<T, Iterable<String>, WhoisServerListException> {
	
    /**
     * The xpath expression.
     */
	private final XPathExpression xpath;
	
	/**
	 * The document converter.
	 */
	private final ThrowableConverter<T, Document, WhoisServerListException>
	    documentConverter;

	/**
	 * Initializes with an xpath expression and a document converter.
	 * 
	 * @param xpath              the xpath expression
	 * @param documentConverter  the document converter
	 *
	 * @throws WhoisServerListException If compilation of the xpath expression
	 *                                  failed
	 */
	public DocumentToStringIteratorConvertor(final String xpath,
	        final ThrowableConverter<T, Document, WhoisServerListException> documentConverter)
	               throws WhoisServerListException {

		try {
			this.xpath = XPathFactory.newInstance().newXPath().compile(xpath);
			this.documentConverter = documentConverter;
			
		} catch (XPathExpressionException e) {
			throw new WhoisServerListException(e);
			
		}
	}

	@Override
	public Iterable<String> convert(final T input)
			throws WhoisServerListException {
		try {
			NodeList tldNodes = (NodeList) xpath.evaluate(documentConverter.convert(input), XPathConstants.NODESET);
			return new NodeListIterable<Node, String>(tldNodes, new NodeToValueConverter());
			
		} catch (XPathExpressionException e) {
			throw new WhoisServerListException(e);
			
		}
	}

}
