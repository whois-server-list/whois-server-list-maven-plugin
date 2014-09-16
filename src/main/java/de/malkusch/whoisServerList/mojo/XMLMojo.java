package de.malkusch.whoisServerList.mojo;

import java.io.File;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;

/**
 * Domain List Compiler Mojo.
 *
 * @author markus@malkusch.de
 *
 * @see DomainListCompiler
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
@Mojo(name = "xml")
public final class XMLMojo extends AbstractMojo {

    /**
     * Path to the generated xml file.
     */
    @Parameter(required = true)
    private File file;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            DomainListCompiler compiler = new DomainListCompiler();
            DomainList list = compiler.compile();

            JAXBContext context = JAXBContext.newInstance(DomainList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(list, file);

        } catch (JAXBException | BuildListException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }


}