package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PropertyKey;
import javax.annotation.concurrent.Immutable;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.ConcurrencyService;
import de.malkusch.whoisServerList.compiler.helper.converter.DocumentToStringIteratorConvertor;
import de.malkusch.whoisServerList.compiler.helper.converter.EntityToDocumentConverter;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Domain list factory.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class IanaDomainListFactory implements DomainListFactory {

    /**
     * The property key for the URI to IANA's TLD list.
     */
    @PropertyKey
    public static final String PROPERTY_LIST_URI = "iana.list.uri";

    /**
     * The property key for the character encoding of IANA's TLD list.
     */
    @PropertyKey
    public static final String PROPERTY_LIST_CHARSET = "iana.list.charset";

    /**
     * The property key for the xpath expression to find
     * all TLDs from IANA's list.
     */
    @PropertyKey
    public static final String PROPERTY_LIST_TLD_XPATH = "iana.list.tld.xpath";

    /**
     * The property key for IANA's whois server.
     */
    @PropertyKey
    public static final String PROPERTY_WHOIS_HOST = "iana.whois.host";

    /**
     * The property key for IANA's whois server.
     */
    @PropertyKey
    public static final String PROPERTY_WHOIS_CHARSET = "iana.whois.charset";

    /**
     * The property key for IANA's whois server timeout.
     */
    @PropertyKey
    public static final String PROPERTY_WHOIS_TIMEOUT_SECONDS
        = "iana.whois.timeout.seconds";

    /**
     * The configuration.
     */
    private final Properties properties;

    /**
     * Initialize with a configuration.
     *
     * @param properties  the configuration
     */
    public IanaDomainListFactory(final Properties properties) {
        this.properties = properties;
    }

    @Override
    public DomainList buildList()
            throws BuildListException, InterruptedException {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpGet httpGet
                    = new HttpGet(properties.getProperty(PROPERTY_LIST_URI));

            CloseableHttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            String charset = properties.getProperty(PROPERTY_LIST_CHARSET);
            EntityToDocumentConverter documentConverter
                    = new EntityToDocumentConverter(charset);

            DocumentToStringIteratorConvertor<HttpEntity> tldConverter
                    = new DocumentToStringIteratorConvertor<>(
                            properties.getProperty(PROPERTY_LIST_TLD_XPATH),
                            documentConverter);

            ConcurrencyService concurrencyService
                    = ConcurrencyService.getService();

            List<FutureTask<TopLevelDomain>> tasks = new ArrayList<>();
            for (final String name : tldConverter.convert(entity)) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();

                }
                FutureTask<TopLevelDomain> task
                        = new FutureTask<>(new Callable<TopLevelDomain>() {

                    @Override
                    public TopLevelDomain call()
                            throws WhoisServerListException,
                            InterruptedException {

                        IANATopLevelDomainBuilder builder
                            = new IANATopLevelDomainBuilder(properties);
                        builder.setName(name);
                        return builder.build();
                    }

                });
                tasks.add(task);
                concurrencyService.getExecutor().execute(task);

            }

            List<TopLevelDomain> domains = new ArrayList<>();
            int timeout = Integer.parseInt(
                    properties.getProperty(PROPERTY_WHOIS_TIMEOUT_SECONDS));
            for (Future<TopLevelDomain> task : tasks) {
                domains.add(task.get(timeout, TimeUnit.SECONDS));

            }

            EntityUtils.consume(entity);

            DomainList list = new DomainList();
            list.setDomains(domains);

            return list;

        } catch (ExecutionException e) {
            if (e.getCause() instanceof InterruptedException) {
                throw (InterruptedException) e.getCause();

            } else {
                throw new BuildListException(e);

            }
        } catch (IOException | WhoisServerListException | TimeoutException e) {
            throw new BuildListException(e);

        }
    }

    @Override
    public Source getSource() {
        return Source.IANA;
    }

}
