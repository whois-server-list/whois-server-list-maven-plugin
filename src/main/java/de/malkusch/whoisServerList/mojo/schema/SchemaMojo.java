package de.malkusch.whoisServerList.mojo.schema;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.malkusch.whoisServerList.api.v1.model.DomainList;

/**
 * Schema Mojo.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
@Mojo(name = "schema")
public final class SchemaMojo extends AbstractMojo {

    /**
     * Path to the generated schema file.
     */
    @Parameter(required = true)
    private File schema;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            JAXBContext context = JAXBContext.newInstance(DomainList.class);

            StreamSchemaOutputResolver resolver
                    = new StreamSchemaOutputResolver(schema);
            context.generateSchema(resolver);

        } catch (JAXBException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

}
