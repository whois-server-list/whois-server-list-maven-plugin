package de.malkusch.whoisServerList.compiler.merger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PropertyKey;
import javax.annotation.concurrent.Immutable;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.compiler.helper.ConcurrencyService;

/**
 * Merges URLs.
 *
 * This merger checks if an URL returns a HTTP 200 status response.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
final class URLMerger implements Merger<URL> {

    /**
     * Merger for the accessible urls.
     */
    private final NotNullMerger<URL> notNullMerger = new NotNullMerger<>();

    /**
     * The timeout for the HTTP requests in seconds.
     */
    private final int timeout;

    /**
     * The configuration porperty name for the request timeout.
     *
     * @see #timeout
     */
    @PropertyKey
    private static final String PROPERTY_TIMEOUT = "merger.url.timeout.seconds";

    /**
     * Executor for the requests.
     */
    private final Executor executor;

    /**
     * The logger.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(URLMerger.class);

    /**
     * Milliseconds for one second.
     */
    private static final int SECOND = 1000;

    /**
     * Sets the timeout in seconds for the HTTP requests.
     *
     * @param properties  the application properties
     * @see #PROPERTY_TIMEOUT
     */
    URLMerger(final Properties properties) {
        this(Integer.parseInt(properties.getProperty(PROPERTY_TIMEOUT)));
    }

    /**
     * Sets the timeout in seconds for the HTTP requests.
     *
     * @param timeout  the timeout in seconds
     */
    URLMerger(final int timeout) {
        this.timeout = timeout;
        this.executor = ConcurrencyService.getService().getExecutor();
    }

    @Override
    public URL merge(final URL left, final URL right)
            throws InterruptedException {

        FutureTask<URL> leftTask = buildAccessibleURLTask(left);
        FutureTask<URL> rightTask = buildAccessibleURLTask(right);

        executor.execute(leftTask);
        executor.execute(rightTask);

        URL accessibleLeft;
        try {
            accessibleLeft = leftTask.get(timeout, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException e) {
            accessibleLeft = null;
        }

        URL accessibleRight;
        try {
            accessibleRight = rightTask.get(timeout, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException e) {
            accessibleRight = null;
        }

        return notNullMerger.merge(accessibleLeft, accessibleRight);
    }

    /**
     * Builds a task to get the accessible URL.
     *
     * @param url  the url, may be null
     * @return the task to get the accessible URL
     * @see #getAccessibleURL(URL)
     */
    private FutureTask<URL> buildAccessibleURLTask(final URL url) {
        return new FutureTask<>(new Callable<URL>() {

                @Override
                public URL call() {
                    return getAccessibleURL(url);
                }

        });
    }

    /**
     * Returns the accessible URL.
     *
     * This method follows permanent redirections.
     *
     * @param url  the url, may be null
     * @return the accessible URL, or null
     */
    private URL getAccessibleURL(final URL url) {
        if (url == null) {
            return null;
        }

        RedirectRecorder redirectRecorder = new RedirectRecorder();
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setRedirectStrategy(redirectRecorder);

        try {
            clientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial((chain, authType) -> true).build()));
            
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            LOGGER.error("Can't built SSL Socket factory for URL '{}'", url, e);
            return null;
        }

        try (CloseableHttpClient httpclient = clientBuilder.build()) {
            HttpHead httpHead = new HttpHead(url.toURI());
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectTimeout(
                            timeout * SECOND).build();
            httpHead.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpHead);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return null;

            }

            URL redirectedURL = redirectRecorder.getRedirectedLocation();
            if (redirectedURL != null) {
                return redirectedURL;

            } else {
                return url;

            }

        } catch (IOException | URISyntaxException e) {
            LOGGER.warn("Removing inaccessible URL '{}'", url);
            return null;

        }
    }

}
