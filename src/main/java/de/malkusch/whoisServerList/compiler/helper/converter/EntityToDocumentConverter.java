package de.malkusch.whoisServerList.compiler.helper.converter;

import java.io.IOException;

import net.jcip.annotations.Immutable;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.w3c.dom.Document;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

/**
 * HttpEntity to Document Converter.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class EntityToDocumentConverter implements
        DocumentConverter<HttpEntity> {

    /**
     * The default character encoding.
     */
    private final String defaultCharset;

    /**
     * Sets the default character encoding.
     *
     * @param defaultCharset  the default character encoding
     */
    public EntityToDocumentConverter(final String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    @Override
    public Document convert(final HttpEntity entity)
            throws WhoisServerListException {

        try {
            Header encoding = entity.getContentEncoding();
            String charset = this.defaultCharset;
            if (encoding != null) {
                charset = encoding.getValue();

            }
            InputStreamToDocumentConverter converter
                    = new InputStreamToDocumentConverter(charset);

            return converter.convert(entity.getContent());

        } catch (IOException e) {
            throw new WhoisServerListException(e);

        }
    }

}
