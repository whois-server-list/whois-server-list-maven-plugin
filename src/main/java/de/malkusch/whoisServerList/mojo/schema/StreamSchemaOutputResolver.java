package de.malkusch.whoisServerList.mojo.schema;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * Schema printer.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class StreamSchemaOutputResolver extends SchemaOutputResolver {

    /**
     * Path to the generated schema file.
     */
    private final File file;

    /**
     * Sets the file path.
     *
     * @param file  the path to the generated schema file, not null
     */
    public StreamSchemaOutputResolver(final File file) {
        this.file = file;
    }

    @Override
    public Result createOutput(
            final String namespaceUri, final String suggestedFileName)
                    throws IOException {

        Result result = new StreamResult(file);
        result.setSystemId(file.toURI().toURL().toString());
        return result;
    }

}
