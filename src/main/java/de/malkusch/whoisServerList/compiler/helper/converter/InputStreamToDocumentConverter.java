package de.malkusch.whoisServerList.compiler.helper.converter;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

public class InputStreamToDocumentConverter implements
		ThrowableConverter<InputStream, Document, WhoisServerListException> {

	private String charset;

	public InputStreamToDocumentConverter(String charset) {
		this.charset = charset;
	}

	@Override
	public Document convert(InputStream stream) throws WhoisServerListException {
		try {
			HtmlCleaner cleaner = new HtmlCleaner();
			CleanerProperties cleanerProperties = cleaner.getProperties();
			cleanerProperties.setCharset(charset);
			
			TagNode node = cleaner.clean(stream);
			
			return new DomSerializer(cleanerProperties, false).createDOM(node);
			
		} catch (IOException e) {
			throw new WhoisServerListException(e);
			
		} catch (ParserConfigurationException e) {
			throw new WhoisServerListException(e);
			
		}
	}

}
