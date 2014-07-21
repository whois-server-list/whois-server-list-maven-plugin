package de.malkusch.whoisServerList.compiler.merger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * Decorates the default redirect strategy to record the last permanent
 * redirect.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
final class RedirectRecorder extends DefaultRedirectStrategy {

    /**
     * The redirected URL, may be null.
     */
    private URL redirectedLocation;

    /**
     * Whether there was a temporary redirect.
     */
    private boolean temporaryRedirect = false;

    @Override
    public URI getLocationURI(final HttpRequest request,
            final HttpResponse response, final HttpContext context)
                    throws ProtocolException {

        try {
            URI location = super.getLocationURI(request, response, context);

            // Only record until the first temporary redirect.
            if (this.temporaryRedirect) {
                return location;

            }

            switch (response.getStatusLine().getStatusCode()) {

            // Only record permanent redirects
            case HttpStatus.SC_MOVED_PERMANENTLY:
                this.redirectedLocation = location.toURL();
                break;

            // Set the flag to stop recording after temporary redirect
            case HttpStatus.SC_TEMPORARY_REDIRECT:
            case HttpStatus.SC_MOVED_TEMPORARILY:
                this.temporaryRedirect = true;
                break;

            // ignore other states
            default:
                break;

            }
            return location;

        } catch (MalformedURLException e) {
            throw new ProtocolException(e.getMessage(), e);

        }
    }

    /**
     * Returns the last permanent redirect.
     *
     * @return the last permanent redirect
     */
    URL getRedirectedLocation() {
        return redirectedLocation;
    }

}
