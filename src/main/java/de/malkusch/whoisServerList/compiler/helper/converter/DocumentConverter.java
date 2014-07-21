package de.malkusch.whoisServerList.compiler.helper.converter;

import javax.annotation.concurrent.ThreadSafe;

import org.w3c.dom.Document;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;

/**
 * Converter to a Document.
 *
 * @author markus@malkusch.de
 * @param <T>  the source type
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@ThreadSafe
public interface DocumentConverter<T> extends
    ThrowableConverter<T, Document, WhoisServerListException> {

}
