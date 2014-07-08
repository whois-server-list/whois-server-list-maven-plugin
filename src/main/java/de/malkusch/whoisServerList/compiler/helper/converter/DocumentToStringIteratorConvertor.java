package de.malkusch.whoisServerList.compiler.helper.converter;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.iterator.NodeListIterable;

public class DocumentToStringIteratorConvertor<T> implements
		ThrowableConverter<T, Iterable<String>, WhoisServerListException> {
	
	private XPathExpression xpath;
	
	private ThrowableConverter<T, Document, WhoisServerListException> documentConverter;

	public DocumentToStringIteratorConvertor(String xpath, ThrowableConverter<T, Document, WhoisServerListException> documentConverter) throws WhoisServerListException {
		try {
			this.xpath = XPathFactory.newInstance().newXPath().compile(xpath);
			this.documentConverter = documentConverter;
			
		} catch (XPathExpressionException e) {
			throw new WhoisServerListException(e);
			
		}
	}

	@Override
	public Iterable<String> convert(T input)
			throws WhoisServerListException {
		try {
			NodeList tldNodes = (NodeList) xpath.evaluate(documentConverter.convert(input), XPathConstants.NODESET);
			return new NodeListIterable<Node, String>(tldNodes, new NodeToValueConverter());
			
		} catch (XPathExpressionException e) {
			throw new WhoisServerListException(e);
			
		}
	}

}
