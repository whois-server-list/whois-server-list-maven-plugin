package de.malkusch.whoisServerList.compiler.helper.converter;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import net.jcip.annotations.Immutable;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

/**
 * InputStream to Document Converter.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class InputStreamToDocumentConverter implements
		ThrowableConverter<InputStream, Document, WhoisServerListException> {

    /**
     * The character encoding.
     */
	private final String charset;

	/**
	 * Sets the character encoding.
	 *
	 * @param charset the character encoding
	 */
	public InputStreamToDocumentConverter(final String charset) {
		this.charset = charset;
	}

	@Override
	public Document convert(final InputStream stream) throws WhoisServerListException {
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
