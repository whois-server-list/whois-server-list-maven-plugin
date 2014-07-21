package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;

public class RedirectRecorderTest {

    private HttpRequest request;

    private HttpContext context;

    private RedirectRecorder recorder;

    @Before
    public void setUp() throws MethodNotSupportedException {
        DefaultHttpRequestFactory requestFactory
                = new DefaultHttpRequestFactory();

        request = requestFactory.newHttpRequest(
                "HEAD", "http://www.example.org/");

        recorder = new RedirectRecorder();

        context = new BasicHttpContext();
    }

    @Test
    public void testNoRedirect() throws ProtocolException, MethodNotSupportedException {
        DefaultHttpResponseFactory responseFactory
                = new DefaultHttpResponseFactory();
        HttpResponse response = responseFactory.newHttpResponse(
                new ProtocolVersion("http", 1, 1), HttpStatus.SC_OK, context);

        assertFalse(recorder.isRedirected(request, response, context));
        assertNull(recorder.getRedirectedLocation());
    }

    @Test
    public void testTemporaryRedirect() throws MethodNotSupportedException, ProtocolException {
        DefaultHttpResponseFactory responseFactory
                = new DefaultHttpResponseFactory();
        {
            HttpResponse response = responseFactory.newHttpResponse(
                    new ProtocolVersion("http", 1, 1),
                    HttpStatus.SC_MOVED_TEMPORARILY, context);
            response.addHeader("Location", "http://www.example.net/");

            assertTrue(recorder.isRedirected(request, response, context));
            assertNull(recorder.getRedirectedLocation());
        }
        {
            RedirectRecorder recorder = new RedirectRecorder();
            HttpResponse response = responseFactory.newHttpResponse(
                    new ProtocolVersion("http", 1, 1),
                    HttpStatus.SC_TEMPORARY_REDIRECT, context);
            response.addHeader("Location", "http://www.example.net/");

            assertTrue(recorder.isRedirected(request, response, context));
            assertNull(recorder.getRedirectedLocation());
        }
    }

    @Test
    public void testPermanentRedirect() throws ProtocolException {
        DefaultHttpResponseFactory responseFactory
                = new DefaultHttpResponseFactory();
        HttpResponse response = responseFactory.newHttpResponse(
                new ProtocolVersion("http", 1, 1),
                HttpStatus.SC_MOVED_PERMANENTLY, context);
        response.addHeader("Location", "http://www.example.net/");

        recorder.getRedirect(request, response, context);

        assertEquals("http://www.example.net/",
                recorder.getRedirectedLocation().toString());
    }

    @Test
    public void testPermanentAfterTemporaryRedirect() throws ProtocolException {
        DefaultHttpResponseFactory responseFactory
                = new DefaultHttpResponseFactory();

        HttpResponse tempResponse = responseFactory.newHttpResponse(
                new ProtocolVersion("http", 1, 1),
                HttpStatus.SC_MOVED_TEMPORARILY, context);
        tempResponse.addHeader("Location", "http://www.example.net/");

        assertTrue(recorder.isRedirected(request, tempResponse, context));
        recorder.getRedirect(request, tempResponse, context);

        HttpResponse permResponse = responseFactory.newHttpResponse(
                new ProtocolVersion("http", 1, 1),
                HttpStatus.SC_MOVED_PERMANENTLY, context);
        permResponse.addHeader("Location", "http://www.example.com/");

        assertTrue(recorder.isRedirected(request, permResponse, context));
        assertNull(recorder.getRedirectedLocation());
    }

    @Test
    public void testTemporaryAfterPermanentRedirect() throws ProtocolException {
        DefaultHttpResponseFactory responseFactory
        = new DefaultHttpResponseFactory();

        HttpResponse permResponse = responseFactory.newHttpResponse(
                new ProtocolVersion("http", 1, 1),
                HttpStatus.SC_MOVED_PERMANENTLY, context);
        permResponse.addHeader("Location", "http://www.example.com/");

        assertTrue(recorder.isRedirected(request, permResponse, context));
        recorder.getRedirect(request, permResponse, context);

        HttpResponse tempResponse = responseFactory.newHttpResponse(
                new ProtocolVersion("http", 1, 1),
                HttpStatus.SC_MOVED_TEMPORARILY, context);
        tempResponse.addHeader("Location", "http://www.example.net/");

        assertTrue(recorder.isRedirected(request, tempResponse, context));
        recorder.getRedirect(request, tempResponse, context);

        assertEquals("http://www.example.com/",
                recorder.getRedirectedLocation().toString());

    }

}
