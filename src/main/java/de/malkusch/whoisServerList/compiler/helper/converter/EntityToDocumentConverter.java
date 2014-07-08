package de.malkusch.whoisServerList.compiler.helper.converter;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.w3c.dom.Document;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

public class EntityToDocumentConverter implements
		ThrowableConverter<HttpEntity, Document, WhoisServerListException> {

	private String defaultCharset;

	public EntityToDocumentConverter(String defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

	@Override
	public Document convert(HttpEntity entity) throws WhoisServerListException {
		try {
			Header encoding = entity.getContentEncoding();
			String charset = this.defaultCharset;
			if (encoding != null) {
				charset = encoding.getValue();

			}
			InputStreamToDocumentConverter converter = new InputStreamToDocumentConverter(
					charset);
			return converter.convert(entity.getContent());

		} catch (IOException e) {
			throw new WhoisServerListException(e);

		}
	}

}
