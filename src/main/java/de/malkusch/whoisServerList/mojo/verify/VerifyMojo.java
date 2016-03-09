package de.malkusch.whoisServerList.mojo.verify;

import java.io.File;
import java.io.IOException;
import java.net.IDN;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.malkusch.whoisApi.RecoverableWhoisApiException;
import de.malkusch.whoisApi.WhoisApi;
import de.malkusch.whoisApi.WhoisApiException;
import de.malkusch.whoisServerList.api.v1.DomainListFactory;
import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.WhoisServer;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

@Mojo(name = "verify", defaultPhase = LifecyclePhase.VERIFY)
public final class VerifyMojo extends AbstractMojo {

    /**
     * Api key for the <a href="https://market.mashape.com/malkusch/whois">Whois
     * API</a>
     */
    @Parameter(required = true, property = "whoisApi.apiKey")
    private String apiKey;

    /**
     * Path to the generated xml file.
     */
    @Parameter(required = true)
    private File file;

    private WhoisApi whoisApi;

    private Collection<Pattern> errorPatterns;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Verifying generated list.");

        whoisApi = new WhoisApi(apiKey);

        try {
            DomainListFactory factory = new DomainListFactory();
            DomainList list = factory.build(file.toURI().toURL());

            errorPatterns = Stream
                    .concat(list.getDomains().stream().flatMap(domain -> domain.getWhoisServers().stream()),
                            list.getDomains().stream()
                                    .flatMap(tld -> tld.getDomains().stream()
                                            .flatMap(domain -> domain.getWhoisServers().stream())))
                    .flatMap(server -> server.getErrorPatterns().stream()).distinct().collect(Collectors.toList());

            ForkJoinPool executor = new ForkJoinPool(1000);
            boolean valid = executor.submit(() -> Stream.concat(list.getDomains().parallelStream(),
                    list.getDomains().parallelStream().flatMap(tld -> tld.getDomains().parallelStream())

            ).flatMap(VerifyMojo::convert).filter(DomainAndServerHolder::hasAvailablePattern).map(this::verify)
                    .reduce(true, (a, b) -> a && b)).get();

            if (!valid) {
                throw new MojoFailureException("The server list is not consistent.");
            }

        } catch (JAXBException | MalformedURLException e) {
            throw new MojoFailureException("Failed to unmarshal the generated XML list.", e);

        } catch (InterruptedException | ExecutionException e) {
            throw new MojoExecutionException("Failed to finish verification.", e);
        }
    }

    private boolean verify(final DomainAndServerHolder domainAndServer) {
        return verifyAvailable(domainAndServer, 0);
    }

    private boolean verifyAvailable(final DomainAndServerHolder domainAndServer, int iteration) {

        String host = domainAndServer.server.getHost();
        String domain = domainAndServer.domain.getName();

        try {
            String availableDomain = "fwslbokir." + IDN.toASCII(domain);

            String response = IOUtils.toString(whoisApi.query(host, availableDomain));

            boolean error = errorPatterns.stream().anyMatch(pattern -> pattern.matcher(response).find());
            if (error) {
                if (iteration > 5) {
                    getLog().warn(String.format("Could not verify host %s for domain %s: Proxy banned.", host, domain));
                    return true;
                }
                return verifyAvailable(domainAndServer, iteration + 1);
            }

            if (!domainAndServer.server.getAvailablePattern().matcher(response).find()) {
                getLog().error(String.format("Available domain %s was not detected for host %s of domain %s: %s",
                        availableDomain, host, domain, response));

                return false;
            }
            return true;

        } catch (WhoisApiException | RecoverableWhoisApiException | IOException e) {
            this.getLog()
                    .warn(String.format("Could not verify host %s for domain %s: %s", host, domain, e.getMessage()));
            return true;
        }
    }

    private static Stream<DomainAndServerHolder> convert(final Domain domain) {
        return domain.getWhoisServers().parallelStream().map(server -> new DomainAndServerHolder(domain, server));
    }

    private static class DomainAndServerHolder {

        private final Domain domain;

        private final WhoisServer server;

        private DomainAndServerHolder(final Domain domain, final WhoisServer server) {
            this.domain = domain;
            this.server = server;
        }

        private boolean hasAvailablePattern() {
            return server.getAvailablePattern() != null;
        }

    }

}
